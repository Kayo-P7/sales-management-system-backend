package com.commercial_management_system.backend.dto.brand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "dados de entrada para criacao de marca de veiculo")
public record BrandRequest(
        @Schema(description = "nome da marca", example = "Toyota")
        @NotBlank String name
) {
}
