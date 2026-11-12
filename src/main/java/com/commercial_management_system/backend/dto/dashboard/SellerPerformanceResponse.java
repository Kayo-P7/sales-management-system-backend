package com.commercial_management_system.backend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "desempenho consolidado de um vendedor com base nos registros diarios")
public record SellerPerformanceResponse(
        @Schema(description = "identificador do vendedor", example = "1")
        Long sellerId,

        @Schema(description = "nome do vendedor", example = "Carlos Silva")
        String sellerName,

        @Schema(description = "total de fechamentos registrados", example = "5")
        long closedDeals,

        @Schema(description = "total de negociacoes registradas", example = "18")
        long negotiations,

        @Schema(description = "total de agendamentos registrados", example = "9")
        long appointments
) {
}
