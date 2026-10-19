package com.commercial_management_system.backend.dto.cliente;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "dados de resposta de cliente")
public record ClienteResponse(
        @Schema(description = "identificador unico do cliente", example = "1")
        Long id,

        @Schema(description = "nome completo", example = "Maria Silva")
        String name,

        @Schema(description = "telefone de contato", example = "11999998888")
        String telephone,

        @Schema(description = "e-mail cadastrado", example = "maria@example.com")
        String email,

        @Schema(description = "observacoes gerais")
        String notes,

        @Schema(description = "data e hora de criacao")
        LocalDateTime createdAt,

        @Schema(description = "data e hora da ultima atualizacao")
        LocalDateTime updatedAt,

        @Schema(description = "identificador do usuario criador")
        String createdBy,

        @Schema(description = "identificador do usuario da ultima atualizacao")
        String updatedBy
) {
}
