package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.enums.VehicleColor;
import com.commercial_management_system.backend.enums.VehicleType;
import com.commercial_management_system.backend.model.Brand;
import com.commercial_management_system.backend.model.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class VehicleMapperTest {

    private final VehicleMapper mapper = Mappers.getMapper(VehicleMapper.class);

    // valida conversao de dados de registro para entidade veiculo
    @Test
    @DisplayName("deve converter request de veiculo para entidade")
    void deveConverterRequestParaEntidadeVeiculo() {
        VehicleRegister request = new VehicleRegister("Corolla", VehicleColor.PRETO, VehicleType.CAR, new BigDecimal("120000.00"), 1L);

        Vehicle entity = mapper.toVehicle(request);

        assertNotNull(entity);
        assertEquals("Corolla", entity.getName());
        assertEquals(VehicleColor.PRETO, entity.getColor());
        assertEquals(VehicleType.CAR, entity.getVehicleType());
        assertEquals(new BigDecimal("120000.00"), entity.getPrice());
        assertNull(entity.getBrand());
    }

    // valida extracao correta do identificador da marca associada
    @Test
    @DisplayName("deve converter entidade veiculo para response mapeando brandId")
    void deveConverterEntidadeParaVehicleResponseMapeandoBrandId() {
        Brand brand = new Brand();
        brand.setId(5L);
        brand.setName("Toyota");

        Vehicle entity = new Vehicle();
        entity.setId(100L);
        entity.setName("Yaris");
        entity.setColor(VehicleColor.BRANCO);
        entity.setVehicleType(VehicleType.CAR);
        entity.setPrice(new BigDecimal("85000.00"));
        entity.setBrand(brand);

        VehicleResponse response = mapper.toVehicleResponse(entity);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Yaris", response.name());
        assertEquals(VehicleColor.BRANCO, response.color());
        assertEquals(VehicleType.CAR, response.vehicleType());
        assertEquals(new BigDecimal("85000.00"), response.price());
        assertEquals(5L, response.brandId());
    }

    // valida atualizacao dos dados do veiculo
    @Test
    @DisplayName("deve atualizar atributos do veiculo")
    void deveAtualizarVeiculoCompletamente() {
        Vehicle entity = new Vehicle();
        entity.setName("Civic Antigo");
        entity.setColor(VehicleColor.CINZA);
        entity.setVehicleType(VehicleType.CAR);
        entity.setPrice(new BigDecimal("90000.00"));

        VehicleRegister update = new VehicleRegister("Civic Touring", VehicleColor.PRETO, VehicleType.CAR, new BigDecimal("150000.00"), 2L);

        mapper.updateVehicle(update, entity);

        assertEquals("Civic Touring", entity.getName());
        assertEquals(VehicleColor.PRETO, entity.getColor());
        assertEquals(new BigDecimal("150000.00"), entity.getPrice());
    }
}
