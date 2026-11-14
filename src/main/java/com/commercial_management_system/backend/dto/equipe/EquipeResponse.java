package com.commercial_management_system.backend.dto.equipe;

import com.commercial_management_system.backend.dto.user.UserSummaryResponse;

import java.util.List;

public record EquipeResponse(
        Long id,
        String nome,
        List<UserSummaryResponse> vendedores,
        UserSummaryResponse supervisor
) {
}
