package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.enums.VehicleColor;
import com.commercial_management_system.backend.enums.VehicleType;
import com.commercial_management_system.backend.service.VehicleService;
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

import java.math.BigDecimal;
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
class VehicleControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    // valida cadastro de veiculo retornando status 201
    @Test
    @DisplayName("deve cadastrar veiculo e retornar status 201")
    void deveCadastrarVeiculoRetornando201() throws Exception {
        VehicleRegister request = new VehicleRegister("Yaris", VehicleColor.PRATA, VehicleType.CAR, new BigDecimal("95000.00"), 1L);
        VehicleResponse response = new VehicleResponse(1L, "Yaris", VehicleColor.PRATA, VehicleType.CAR, new BigDecimal("95000.00"), 1L);

        when(vehicleService.save(any(VehicleRegister.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yaris"));
    }

    // valida busca de veiculo por id retornando 200 ok
    @Test
    @DisplayName("deve buscar veiculo por id e retornar status 200")
    void deveBuscarVeiculoPorIdRetornando200() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Yaris", VehicleColor.PRATA, VehicleType.CAR, new BigDecimal("95000.00"), 1L);

        when(vehicleService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yaris"));
    }

    // valida exclusao de veiculo retornando 204 no content
    @Test
    @DisplayName("deve excluir veiculo e retornar status 204")
    void deveExcluirVeiculoRetornando204() throws Exception {
        mockMvc.perform(delete("/api/v1/vehicles/1"))
                .andExpect(status().isNoContent());

        verify(vehicleService).deleteVehicleById(1L);
    }

    // valida listagem de veiculos retornando 200 ok
    @Test
    @DisplayName("deve listar veiculos com paginacao e retornar status 200")
    void deveListarVeiculosRetornando200() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Yaris", VehicleColor.PRATA, VehicleType.CAR, new BigDecimal("95000.00"), 1L);

        when(vehicleService.findVehicle(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Yaris"));
    }
}
