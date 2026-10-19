package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
import com.commercial_management_system.backend.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    // valida cadastro de cliente retornando status 201
    @Test
    @DisplayName("deve cadastrar cliente e retornar status 201")
    void deveCadastrarClienteRetornando201() throws Exception {
        ClienteRequest request = new ClienteRequest("Lucas Santos", "11988887777", "lucas@example.com", "interessado");
        ClienteResponse response = new ClienteResponse(1L, "Lucas Santos", "11988887777", "lucas@example.com", "interessado", LocalDateTime.now(), null, "10", null);

        when(clienteService.save(any(ClienteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Lucas Santos"));
    }

    // valida busca de cliente por id retornando 200 ok
    @Test
    @DisplayName("deve buscar cliente por id e retornar status 200")
    void deveBuscarClientePorIdRetornando200() throws Exception {
        ClienteResponse response = new ClienteResponse(1L, "Lucas Santos", "11988887777", "lucas@example.com", null, LocalDateTime.now(), null, "10", null);

        when(clienteService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Lucas Santos"));
    }

    // valida exclusao de cliente retornando 204 no content
    @Test
    @DisplayName("deve excluir cliente e retornar status 204")
    void deveExcluirClienteRetornando204() throws Exception {
        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService).deleteClienteById(1L);
    }

    // valida listagem de clientes retornando 200 ok
    @Test
    @DisplayName("deve listar clientes com paginacao e retornar status 200")
    void deveListarClientesRetornando200() throws Exception {
        ClienteResponse response = new ClienteResponse(1L, "Lucas Santos", "11988887777", "lucas@example.com", null, LocalDateTime.now(), null, "10", null);

        when(clienteService.listarClientes(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Lucas Santos"));
    }
}
