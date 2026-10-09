package com.commercial_management_system.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "dados para autenticacao do usuario")
public record AuthenticationRequest(
        @Schema(description = "e-mail de acesso cadastrado", example = "admin@example.com")
        @NotBlank @Email String email,

        @Schema(description = "senha de acesso", example = "senha123")
        @NotBlank String password
) {
}
