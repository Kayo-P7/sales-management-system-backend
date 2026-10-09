package com.commercial_management_system.backend.dto.user;

import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "representacao de dados do usuario")
public record UserResponse(
        @Schema(description = "identificador unico do usuario", example = "1")
        Long id,

        @Schema(description = "nome completo", example = "Mariana Lima")
        String name,

        @Schema(description = "e-mail cadastrado", example = "mariana@example.com")
        String email,

        @Schema(description = "telefone de contato", example = "11988887777")
        String telephone,

        @Schema(description = "perfil de acesso", example = "ROLE_ADMIN")
        UserType userType,

        @Schema(description = "status da conta", example = "ACTIVE")
        UserStatus userStatus
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getTelephone(),
                user.getUserType(),
                user.getUserStatus()
        );
    }
}
