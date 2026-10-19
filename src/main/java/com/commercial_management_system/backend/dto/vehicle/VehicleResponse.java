package com.commercial_management_system.backend.dto.vehicle;

import com.commercial_management_system.backend.enums.VehicleColor;
import com.commercial_management_system.backend.enums.VehicleType;
import com.commercial_management_system.backend.model.Vehicle;

import java.math.BigDecimal;

public record VehicleResponse(
        String name,
        VehicleColor color,
        VehicleType vehicleType,
        BigDecimal price,
        Long brand

) {
    public VehicleResponse(Vehicle vehicle) {
        this(
                vehicle.getName(),
                vehicle.getColor(),
                vehicle.getVehicleType(),
                vehicle.getPrice(),
                vehicle.getBrand().getId()
        );
    }
}
