package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface VehicleMapper {

    // converte dados de cadastro em entidade veiculo
    Vehicle toVehicle(VehicleRegister vehicleRegister);

    // converte entidade veiculo em dto de resposta mapeando identificador da marca
    @Mapping(target = "brandId", source = "brand.id")
    VehicleResponse toVehicleResponse(Vehicle vehicle);

    // atualiza atributos da entidade veiculo a partir do dto de registro
    void updateVehicle(VehicleRegister vehicleRegister, @MappingTarget Vehicle vehicle);
}
