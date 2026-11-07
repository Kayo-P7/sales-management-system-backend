package com.commercial_management_system.backend.dto.user;

import com.commercial_management_system.backend.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "resumo publico de dados do usuario")
public record UserSummaryResponse(
        @Schema(description = "identificador unico do usuario", example = "1")
        Long id,

        @Schema(description = "nome completo", example = "Mariana Lima")
        String name,

        @Schema(description = "e-mail de contato", example = "mariana@example.com")
        String email,

        @Schema(description = "telefone de contato", example = "11988887777")
        String telephone,

        @Schema(description = "perfil de acesso", example = "ROLE_VENDEDOR")
        UserType userType
) {
}
