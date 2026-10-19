package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.model.Vehicle;
import com.commercial_management_system.backend.repository.BrandRepository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityNotFoundException;

@Mapper(componentModel = "spring", imports = {EntityNotFoundException.class})
public abstract class VehicleMapper {
    @Autowired
    protected BrandRepository brandRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(
            target = "brand",
            expression = """
                    java(brandRepository.findById(vehicleRegister.brandId())
                        .orElseThrow(() ->
                            new EntityNotFoundException("Marca não encontrada")))
                    """
    )
    public abstract Vehicle toVehicle(VehicleRegister vehicleRegister);

    @Mapping(target = "brandId", source = "brand.id")
    public abstract VehicleResponse toVehicleResponse(Vehicle vehicle);

    @Mapping(target = "brandId", source = "brand.id")
    public abstract VehicleResponse toVehicleResponseSpecs(Vehicle vehicle);

    public abstract void updateVehicle(VehicleRegister vehicleRegister, @MappingTarget Vehicle vehicle);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "brand", expression = (
            """
       java(brandRepository
           .findById(vehicleRegister.brandId())
           .orElseThrow(() -> new EntityNotFoundException("Marca não encontrada"))
       )
       """))
    public abstract void updateVehicleBrand(VehicleRegister vehicleRegister, @MappingTarget Vehicle vehicle);
}
