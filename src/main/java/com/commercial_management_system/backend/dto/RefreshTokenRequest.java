package com.commercial_management_system.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "o refresh token e obrigatorio")
        String refreshToken
) {
}
