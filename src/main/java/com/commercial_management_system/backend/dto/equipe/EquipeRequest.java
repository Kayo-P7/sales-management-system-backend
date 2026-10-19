package com.commercial_management_system.backend.dto.equipe;

import java.util.List;

public record EquipeRequest(
        String nome,
        List<Long> vendedores

) {
}
