package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.enums.VehicleColor;
import com.commercial_management_system.backend.enums.VehicleType;
import com.commercial_management_system.backend.exception.ResourceNotFoundException;
import com.commercial_management_system.backend.mapper.VehicleMapper;
import com.commercial_management_system.backend.model.Brand;
import com.commercial_management_system.backend.model.Vehicle;
import com.commercial_management_system.backend.repository.BrandRepository;
import com.commercial_management_system.backend.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    // valida cadastro de veiculo associado a uma marca existente
    @Test
    @DisplayName("deve salvar veiculo com marca associada com sucesso")
    void deveSalvarVeiculoComSucesso() {
        VehicleRegister request = new VehicleRegister("Civic", VehicleColor.PRETO, VehicleType.CAR, new BigDecimal("120000.00"), 1L);
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Honda");

        Vehicle vehicle = new Vehicle();
        vehicle.setName("Civic");
        VehicleResponse responseEsperada = new VehicleResponse(1L, "Civic", VehicleColor.PRETO, VehicleType.CAR, new BigDecimal("120000.00"), 1L);

        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(vehicleMapper.toVehicle(request)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toVehicleResponse(vehicle)).thenReturn(responseEsperada);

        VehicleResponse response = vehicleService.save(request);

        assertNotNull(response);
        assertEquals("Civic", response.name());
        verify(brandRepository).findById(1L);
        verify(vehicleRepository).save(vehicle);
    }

    // valida lancamento de excecao quando marca informada no cadastro nao existir
    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando marca nao existir no cadastro")
    void deveLancarExcecaoQuandoMarcaNaoExistirNoCadastro() {
        VehicleRegister request = new VehicleRegister("Civic", VehicleColor.PRETO, VehicleType.CAR, new BigDecimal("120000.00"), 99L);

        when(brandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.save(request));
    }

    // valida busca de veiculo existente por id
    @Test
    @DisplayName("deve buscar veiculo por id com sucesso")
    void deveBuscarVeiculoPorIdComSucesso() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        VehicleResponse responseEsperada = new VehicleResponse(1L, "Corolla", VehicleColor.BRANCO, VehicleType.CAR, new BigDecimal("140000.00"), 2L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toVehicleResponse(vehicle)).thenReturn(responseEsperada);

        VehicleResponse response = vehicleService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(vehicleRepository).findById(1L);
    }

    // valida lancamento de excecao quando veiculo nao for encontrado por id
    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando veiculo nao for encontrado")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.findById(99L));
    }

    // valida exclusao de veiculo existente
    @Test
    @DisplayName("deve excluir veiculo existente com sucesso")
    void deveExcluirVeiculoExistente() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);

        vehicleService.deleteVehicleById(1L);

        verify(vehicleRepository).deleteById(1L);
    }

    // valida lancamento de excecao ao tentar excluir veiculo inexistente
    @Test
    @DisplayName("deve lancar ResourceNotFoundException ao tentar excluir veiculo inexistente")
    void deveLancarExcecaoAoExcluirVeiculoInexistente() {
        when(vehicleRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.deleteVehicleById(99L));
    }

    // valida busca paginada de veiculos com filtros
    @Test
    @DisplayName("deve buscar veiculos de forma paginada")
    @SuppressWarnings("unchecked")
    void deveBuscarVeiculosComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        Vehicle vehicle = new Vehicle();
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));
        VehicleResponse responseEsperada = new VehicleResponse(1L, "Onix", VehicleColor.BRANCO, VehicleType.CAR, new BigDecimal("75000.00"), 3L);

        when(vehicleRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(vehicleMapper.toVehicleResponse(vehicle)).thenReturn(responseEsperada);

        Page<VehicleResponse> resultado = vehicleService.findVehicle(null, null, null, null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }
}
