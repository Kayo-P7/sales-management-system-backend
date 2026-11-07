package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.brand.BrandRequest;
import com.commercial_management_system.backend.dto.brand.BrandResponse;
import com.commercial_management_system.backend.model.Brand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BrandMapperTest {

    private final BrandMapper mapper = Mappers.getMapper(BrandMapper.class);

    // valida conversao de request para entidade marca
    @Test
    @DisplayName("deve converter request em entidade marca")
    void deveConverterRequestParaEntidadeMarca() {
        BrandRequest request = new BrandRequest("Volkswagen");

        Brand entity = mapper.toBrand(request);

        assertNotNull(entity);
        assertEquals("Volkswagen", entity.getName());
        assertNull(entity.getId());
    }

    // valida conversao de entidade para dto de resposta
    @Test
    @DisplayName("deve converter entidade em brand response")
    void deveConverterEntidadeParaBrandResponse() {
        Brand entity = new Brand();
        entity.setId(12L);
        entity.setName("Chevrolet");

        BrandResponse response = mapper.toBrandResponse(entity);

        assertNotNull(response);
        assertEquals(12L, response.id());
        assertEquals("Chevrolet", response.name());
    }
}
