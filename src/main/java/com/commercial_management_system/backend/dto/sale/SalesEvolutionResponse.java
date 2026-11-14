package com.commercial_management_system.backend.dto.dashboard;

import java.math.BigDecimal;

public record SalesEvolutionResponse(
        String period,
        BigDecimal totalRevenue,
        long totalSales
) {}