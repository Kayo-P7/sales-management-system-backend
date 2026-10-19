package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.brand.BrandRequest;
import com.commercial_management_system.backend.dto.brand.BrandResponse;
import com.commercial_management_system.backend.service.BrandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BrandControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandController brandController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(brandController).build();
    }

    // valida criacao de marca com retorno 201 created
    @Test
    @DisplayName("deve cadastrar marca e retornar status 201")
    void deveCadastrarMarcaRetornando201() throws Exception {
        BrandRequest request = new BrandRequest("Toyota");
        BrandResponse response = new BrandResponse(1L, "Toyota");

        when(brandService.salvar(any(BrandRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Toyota"));
    }

    // valida listagem de marcas com retorno 200 ok
    @Test
    @DisplayName("deve listar marcas e retornar status 200")
    void deveListarMarcasRetornando200() throws Exception {
        when(brandService.listarTodos()).thenReturn(List.of(new BrandResponse(1L, "Honda")));

        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Honda"));
    }

    // valida busca de marca por id com retorno 200 ok
    @Test
    @DisplayName("deve buscar marca por id e retornar status 200")
    void deveBuscarMarcaPorIdRetornando200() throws Exception {
        when(brandService.buscarPorId(1L)).thenReturn(new BrandResponse(1L, "Honda"));

        mockMvc.perform(get("/api/v1/brands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Honda"));
    }
}
