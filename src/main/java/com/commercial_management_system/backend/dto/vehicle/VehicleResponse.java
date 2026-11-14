package com.commercial_management_system.backend.dto.vehicle;

import com.commercial_management_system.backend.enums.VehicleColor;
import com.commercial_management_system.backend.enums.VehicleType;
import com.commercial_management_system.backend.model.Vehicle;

import java.math.BigDecimal;

public record VehicleResponse(
        Long id,
        String name,
        VehicleColor color,
        VehicleType vehicleType,
        BigDecimal price,
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
