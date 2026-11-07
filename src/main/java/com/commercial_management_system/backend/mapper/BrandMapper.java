package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.brand.BrandRequest;
import com.commercial_management_system.backend.dto.brand.BrandResponse;
import com.commercial_management_system.backend.model.Brand;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface BrandMapper {

    // converte requisicao em entidade marca
    Brand toBrand(BrandRequest request);

    // converte entidade marca em dto de resposta
    BrandResponse toBrandResponse(Brand brand);
}
