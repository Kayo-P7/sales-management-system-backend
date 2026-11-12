package com.commercial_management_system.backend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "indicadores consolidados do painel principal")
public record DashboardResponse(
        @Schema(description = "quantidade de veiculos cadastrados", example = "14")
        long totalVehicles,

        @Schema(description = "soma dos precos dos veiculos em estoque", example = "1250000.00")
        BigDecimal inventoryValue,

        @Schema(description = "quantidade de clientes cadastrados", example = "22")
        long totalCustomers,

        @Schema(description = "quantidade de marcas cadastradas", example = "4")
        long totalBrands,

        @Schema(description = "total de fechamentos registrados pelos vendedores", example = "8")
        long totalClosedDeals,

        @Schema(description = "total de negociacoes registradas pelos vendedores", example = "26")
        long totalNegotiations,

        @Schema(description = "desempenho consolidado por vendedor")
        List<SellerPerformanceResponse> salesBySeller
) {
}
