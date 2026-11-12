package com.commercial_management_system.backend.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "dados para cancelamento de venda")
public record SaleCancelRequest(
        @Schema(description = "motivo do cancelamento da venda", example = "desistencia do comprador")
        @NotBlank(message = "o motivo do cancelamento e obrigatorio")
        String reason
) {
}
