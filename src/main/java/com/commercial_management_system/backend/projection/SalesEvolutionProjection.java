package com.commercial_management_system.backend.projection;

import java.math.BigDecimal;

public interface SalesEvolutionProjection {
    String getPeriod();

    BigDecimal getTotalRevenue();

    Long getTotalSales();
}
