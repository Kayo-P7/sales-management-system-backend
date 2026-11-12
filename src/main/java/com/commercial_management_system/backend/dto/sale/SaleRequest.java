package com.commercial_management_system.backend.dto.sale;

import com.commercial_management_system.backend.enums.SalePaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "dados para registro de venda de veiculo")
public record SaleRequest(
        @Schema(description = "identificador do veiculo vendido", example = "1")
        @NotNull(message = "o veiculo e obrigatorio")
        @Positive(message = "o identificador do veiculo deve ser valido")
        Long vehicleId,

        @Schema(description = "identificador do cliente comprador", example = "1")
        @NotNull(message = "o cliente e obrigatorio")
        @Positive(message = "o identificador do cliente deve ser valido")
        Long customerId,

        @Schema(description = "valor fechado da venda", example = "85000.00")
        @NotNull(message = "o valor da venda e obrigatorio")
        @Positive(message = "o valor da venda deve ser maior que zero")
        BigDecimal salePrice,

        @Schema(description = "forma de pagamento", example = "FINANCIAMENTO")
        SalePaymentMethod paymentMethod,

        @Schema(description = "observacoes adicionais da venda")
        String notes
) {
}
