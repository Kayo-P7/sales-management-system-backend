package com.commercial_management_system.backend.dto.brand;

import jakarta.validation.constraints.NotBlank;

public record BrandRequest(
        @NotBlank String name
) {
}
