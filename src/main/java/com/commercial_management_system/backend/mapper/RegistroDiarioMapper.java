package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioRequest;
import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioResponse;
import com.commercial_management_system.backend.model.RegistroDiario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistroDiarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendedor", ignore = true)
    @Mapping(target = "data", ignore = true)
    RegistroDiario toRegistroDiario(RegistroDiarioRequest request);

    @Mapping(source = "vendedor.id", target = "vendedorId")
    @Mapping(source = "vendedor.name", target = "nomeVendedor")
    RegistroDiarioResponse toRegistroDiarioResponse(RegistroDiario registroDiario);
}