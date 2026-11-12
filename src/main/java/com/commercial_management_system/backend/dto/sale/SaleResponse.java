package com.commercial_management_system.backend.dto.sale;

import com.commercial_management_system.backend.enums.SalePaymentMethod;
import com.commercial_management_system.backend.enums.SaleStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "dados de resposta de uma venda")
public record SaleResponse(
        @Schema(description = "identificador unico da venda", example = "1")
        Long id,

        @Schema(description = "identificador do veiculo vendido", example = "1")
        Long vehicleId,

        @Schema(description = "nome do veiculo vendido", example = "Corolla XEi")
        String vehicleName,

        @Schema(description = "identificador do cliente comprador", example = "1")
        Long customerId,

        @Schema(description = "nome do cliente comprador", example = "Maria Silva")
        String customerName,

        @Schema(description = "identificador do vendedor responsavel", example = "3")
        Long sellerId,

        @Schema(description = "nome do vendedor responsavel", example = "Carlos Silva")
        String sellerName,

        @Schema(description = "valor fechado da venda", example = "85000.00")
        BigDecimal salePrice,

        @Schema(description = "data e hora do registro da venda")
        LocalDateTime saleDate,

        @Schema(description = "status da venda", example = "COMPLETED")
        SaleStatus status,

        @Schema(description = "forma de pagamento", example = "FINANCIAMENTO")
        SalePaymentMethod paymentMethod,

        @Schema(description = "observacoes da venda")
        String notes,

        @Schema(description = "motivo do cancelamento quando aplicavel")
        String cancellationReason
) {
}
