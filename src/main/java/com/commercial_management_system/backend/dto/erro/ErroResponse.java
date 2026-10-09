package com.commercial_management_system.backend.dto.erro;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "estrutura padronizada de resposta de erro da api")
public record ErroResponse(
        @Schema(description = "timestamp do instante da ocorrencia do erro", example = "2026-10-08T23:50:00Z")
        Instant timestamp,

        @Schema(description = "codigo de status http", example = "400")
        int status,

        @Schema(description = "descricao do status http", example = "Bad Request")
        String erro,

        @Schema(description = "mensagem explicativa do erro", example = "dados de requisicao invalidos")
        String mensagem,

        @Schema(description = "caminho do endpoint acessado", example = "/api/v1/users")
        String caminho
) {
}
