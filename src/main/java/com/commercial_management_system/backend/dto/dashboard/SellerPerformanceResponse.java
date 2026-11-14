package com.commercial_management_system.backend.dto.dashboard;

public record SellerPerformanceResponse(
        Long sellerId,
        String sellerName,
        long closedDeals,
        long negotiations,
        long appointments
) {
}
