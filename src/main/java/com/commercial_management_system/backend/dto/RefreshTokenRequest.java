package com.commercial_management_system.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "dados de entrada para renovacao ou revogacao de token")
public record RefreshTokenRequest(
        @Schema(description = "token de atualizacao (uuid)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotBlank(message = "o refresh token e obrigatorio")
        String refreshToken
) {
}
