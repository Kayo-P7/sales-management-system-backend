package com.commercial_management_system.backend.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        long totalVehicles,
        BigDecimal inventoryValue,
        long totalCustomers,
        long totalBrands,
        long totalClosedDeals,
        long totalNegotiations,
        List<SellerPerformanceResponse> salesBySeller,
        List<SalesEvolutionResponse> salesEvolution
) {
}
