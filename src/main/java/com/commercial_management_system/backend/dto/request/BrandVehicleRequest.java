package com.commercial_management_system.backend.dto.request;

import com.commercial_management_system.backend.model.Vehicle;

import java.util.List;

public record BrandVehicleRequest(
        long id, //brand
        String name,//brand
        List<Vehicle> vehicleList


) {
}
