package com.commercial_management_system.backend.dto.equipe;

import com.commercial_management_system.backend.model.User;

import java.util.List;

public record EquipeResponse(
        Long id,
        List<User> vendedores,
        User supervisor
) {
}
