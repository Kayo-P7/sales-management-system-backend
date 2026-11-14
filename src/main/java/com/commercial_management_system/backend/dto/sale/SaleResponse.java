package com.commercial_management_system.backend.dto.sale;

import com.commercial_management_system.backend.enums.SalePaymentMethod;
import com.commercial_management_system.backend.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleResponse(
        Long id,
        Long vehicleId,
        String vehicleName,
        Long customerId,
        String customerName,
        Long sellerId,
        String sellerName,
        BigDecimal salePrice,
        LocalDateTime saleDate,
        SaleStatus status,
        SalePaymentMethod paymentMethod,
        String notes,
        String cancellationReason
) {
}
