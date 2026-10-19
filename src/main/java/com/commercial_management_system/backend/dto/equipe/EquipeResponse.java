package com.commercial_management_system.backend.dto.equipe;

import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "detalhes de resposta de equipe de vendas")
public record EquipeResponse(
        @Schema(description = "identificador unico da equipe", example = "1")
        Long id,

        @Schema(description = "nome identificador da equipe", example = "Equipe Alpha")
        String nome,

        @Schema(description = "lista de vendedores vinculados a equipe")
        List<UserSummaryResponse> vendedores,

        @Schema(description = "supervisor responsavel pela equipe")
        UserSummaryResponse supervisor
) {
}
