package com.commercial_management_system.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "resposta de autenticacao contendo o token jwt")
public record TokenResponse(
        @Schema(description = "token jwt de acesso (bearer)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
