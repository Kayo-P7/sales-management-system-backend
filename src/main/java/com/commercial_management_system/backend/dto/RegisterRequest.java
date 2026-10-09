package com.commercial_management_system.backend.dto;

import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "dados para registro de um novo usuario")
public record RegisterRequest(
        @Schema(description = "nome completo", example = "Carlos Souza")
        @NotBlank String name,

        @Schema(description = "e-mail corporativo ou pessoal", example = "carlos@example.com")
        @NotBlank @Email String email,

        @Schema(description = "telefone com ddd", example = "91999998888")
        @NotBlank String telephone,

        @Schema(description = "senha de acesso", example = "senha123")
        @NotBlank String password,

        @Schema(description = "perfil de autorizacao", example = "ROLE_USER")
        @NotNull UserType userType,

        @Schema(description = "status inicial da conta", example = "ACTIVE")
        @NotNull UserStatus userStatus
) {
}
