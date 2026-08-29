package com.commercial_management_system.backend.dto.response;

import com.commercial_management_system.backend.model.Brand;
import com.commercial_management_system.backend.model.Vehicle;

import java.util.List;

public record BrandVehicleResponse(
        Long id,
        String name,
        boolean isActive,
        List<Vehicle> vehicles
) {

    public static BrandVehicleResponse from(Brand brand){
        return new BrandVehicleResponse(
                brand.getId(),
                brand.getName(),
                brand.isActive(),
                brand.getVehicle()
        );
    }
}
