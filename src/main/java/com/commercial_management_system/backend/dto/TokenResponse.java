package com.commercial_management_system.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "resposta de autenticacao contendo o token de acesso e o refresh token")
public record TokenResponse(
        @Schema(description = "token jwt de acesso (bearer)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "token de atualizacao para renovacao da sessao", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String refreshToken
) {
    public TokenResponse(String token) {
        this(token, null);
    }
}
