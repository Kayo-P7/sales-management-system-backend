package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "brand", ignore = true)
    Vehicle toVehicle(VehicleRegister vehicleRegister);

    @Mapping(target = "brandId", source = "brand.id")
    VehicleResponse toVehicleResponse(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "brand", ignore = true)
    void updateVehicle(VehicleRegister vehicleRegister, @MappingTarget Vehicle vehicle);
}
