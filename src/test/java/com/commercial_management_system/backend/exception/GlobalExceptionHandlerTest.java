package com.commercial_management_system.backend.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test/not-found")
        public ResponseEntity<Void> notFound() {
            throw new ResourceNotFoundException("registro de teste nao encontrado");
        }

        @GetMapping("/test/business-error")
        public ResponseEntity<Void> businessError() {
            throw new BusinessException("regra de negocio violada");
        }

        @GetMapping("/test/generic-error")
        public ResponseEntity<Void> genericError() {
            throw new RuntimeException("falha critica no driver de conexao");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // valida retorno 404 para ResourceNotFoundException
    @Test
    @DisplayName("deve retornar 404 quando ResourceNotFoundException for lancada")
    void deveRetornar404QuandoResourceNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("registro de teste nao encontrado"));
    }

    // valida retorno 400 para BusinessException
    @Test
    @DisplayName("deve retornar 400 quando BusinessException for lancada")
    void deveRetornar400QuandoBusinessException() throws Exception {
        mockMvc.perform(get("/test/business-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("regra de negocio violada"));
    }

    // valida mensagem sanitizada e status 500 para excecao generica
    @Test
    @DisplayName("deve retornar 500 com mensagem sanitizada para excecao generica")
    void deveRetornar500SanitizadoParaExcecaoGenerica() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.mensagem").value("ocorreu um erro interno no servidor"));
    }
}
