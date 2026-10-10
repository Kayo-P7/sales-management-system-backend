package com.commercial_management_system.backend.dto.brand;

import com.commercial_management_system.backend.model.Brand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "dados de saida de marca de veiculo")
public record BrandResponse(
        @Schema(description = "identificador unico da marca", example = "1")
        Long id,

        @Schema(description = "nome da marca", example = "Toyota")
        String name
) {
    public static BrandResponse from(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName()
        );
    }
}
