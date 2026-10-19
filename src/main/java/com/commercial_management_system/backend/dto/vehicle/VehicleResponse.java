package com.commercial_management_system.backend.dto.vehicle;

import com.commercial_management_system.backend.enums.VehicleColor;
import com.commercial_management_system.backend.enums.VehicleType;
import com.commercial_management_system.backend.model.Vehicle;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "dados de resposta de veiculo")
public record VehicleResponse(
        @Schema(description = "identificador unico do veiculo", example = "1")
        Long id,

        @Schema(description = "nome ou modelo do veiculo", example = "Corolla")
        String name,

        @Schema(description = "cor predominante do veiculo", example = "PRETO")
        VehicleColor color,

        @Schema(description = "tipo do veiculo", example = "CAR")
        VehicleType vehicleType,

        @Schema(description = "preco de venda", example = "120000.00")
        BigDecimal price,

        @Schema(description = "identificador da marca associada", example = "1")
        Long brandId
) {
    public VehicleResponse(Vehicle vehicle) {
        this(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getColor(),
                vehicle.getVehicleType(),
                vehicle.getPrice(),
                vehicle.getBrand() != null ? vehicle.getBrand().getId() : null
        );
    }
}
