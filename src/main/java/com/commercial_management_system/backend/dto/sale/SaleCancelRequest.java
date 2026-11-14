package com.commercial_management_system.backend.dto.sale;

import jakarta.validation.constraints.NotBlank;

public record SaleCancelRequest(
        @NotBlank(message = "o motivo do cancelamento e obrigatorio")
        String reason
) {
}
