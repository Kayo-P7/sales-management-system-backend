package com.commercial_management_system.backend.dto.sale;

import com.commercial_management_system.backend.enums.SalePaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SaleRequest(
        @NotNull(message = "o veiculo e obrigatorio")
        @Positive(message = "o identificador do veiculo deve ser valido")
        Long vehicleId,

        @NotNull(message = "o cliente e obrigatorio")
        @Positive(message = "o identificador do cliente deve ser valido")
        Long customerId,

        @NotNull(message = "o valor da venda e obrigatorio")
        @Positive(message = "o valor da venda deve ser maior que zero")
        BigDecimal salePrice,

        SalePaymentMethod paymentMethod,

        String notes
) {
}
