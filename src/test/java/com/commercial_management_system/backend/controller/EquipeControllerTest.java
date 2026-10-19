package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.service.EquipeService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EquipeControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EquipeService equipeService;

    @InjectMocks
    private EquipeController equipeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(equipeController).build();
    }

    // valida cadastro de equipe retornando status 201
    @Test
    @DisplayName("deve cadastrar equipe e retornar status 201")
    void deveCadastrarEquipeRetornando201() throws Exception {
        EquipeRequest request = new EquipeRequest("Equipe Vendas", List.of(2L));
        UserSummaryResponse sup = new UserSummaryResponse(1L, "Supervisor", "sup@example.com", "11988880000", UserType.ROLE_SUPERVISOR);
        UserSummaryResponse vend = new UserSummaryResponse(2L, "Vendedor", "vend@example.com", "11977770000", UserType.ROLE_VENDEDOR);
        EquipeResponse response = new EquipeResponse(1L, List.of(vend), sup);

        when(equipeService.save(any(EquipeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/equipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.supervisor.id").value(1));
    }

    // valida busca de equipe do supervisor retornando 200 ok
    @Test
    @DisplayName("deve buscar equipe do supervisor e retornar status 200")
    void deveBuscarEquipeDoSupervisorRetornando200() throws Exception {
        UserSummaryResponse sup = new UserSummaryResponse(1L, "Supervisor", "sup@example.com", "11988880000", UserType.ROLE_SUPERVISOR);
        EquipeResponse response = new EquipeResponse(1L, List.of(), sup);

        when(equipeService.findAllBySupervisorId()).thenReturn(response);

        mockMvc.perform(get("/api/v1/equipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // valida remocao de vendedor da equipe retornando status 204
    @Test
    @DisplayName("deve remover vendedor da equipe e retornar status 204")
    void deveRemoverVendedorRetornando204() throws Exception {
        mockMvc.perform(delete("/api/v1/equipes/vendedores/2"))
                .andExpect(status().isNoContent());

        verify(equipeService).deleteByVendedor(2L);
    }
}
