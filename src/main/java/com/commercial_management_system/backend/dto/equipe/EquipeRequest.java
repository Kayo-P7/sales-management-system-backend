package com.commercial_management_system.backend.dto.equipe;

import com.commercial_management_system.backend.model.User;

import java.util.List;

public record EquipeRequest(
        String nome,
        List<Long> vendedores

) {
}
