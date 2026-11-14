package com.commercial_management_system.backend.dto.cliente;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String name,
        String telephone,
        String email,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {
}
