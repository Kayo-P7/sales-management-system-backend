package com.commercial_management_system.backend.dto.user;

import com.commercial_management_system.backend.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "dados de entrada para criacao de usuario")
public record UserRequest(
        @Schema(description = "nome completo do usuario", example = "Mariana Lima")
        @NotBlank String name,

        @Schema(description = "e-mail unico do usuario", example = "mariana@example.com")
        @NotBlank @Email String email,

        @Schema(description = "telefone unico de contato", example = "11988887777")
        @NotBlank String telephone,

        @Schema(description = "senha em texto claro que sera criptografada", example = "senhaForte@123")
        @NotBlank String password,

        @Schema(description = "perfil de acesso do usuario", example = "ROLE_ADMIN")
        @NotNull UserType userType
) {
}
