package com.commercial_management_system.backend.dto;

public record TokenResponse(
        String token,
        String refreshToken
) {
    public TokenResponse(String token) {
        this(token, null);
    }
}
