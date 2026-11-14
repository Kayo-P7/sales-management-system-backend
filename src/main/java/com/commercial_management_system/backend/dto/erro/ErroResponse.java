package com.commercial_management_system.backend.dto.erro;

import java.time.Instant;

public record ErroResponse(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho
) {
}
