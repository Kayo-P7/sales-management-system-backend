package com.commercial_management_system.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank Double price,
        Double description
) {
}
