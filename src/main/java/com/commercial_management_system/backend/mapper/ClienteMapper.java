package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
import com.commercial_management_system.backend.model.Cliente;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class ClienteMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract Cliente toCliente(ClienteRequest clienteRequest);


    public abstract ClienteResponse toClientResponse(Cliente cliente);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract void updateCliente(ClienteRequest clienteRequest, @MappingTarget Cliente cliente);

    @BeanMapping(ignoreByDefault = true,
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "name", ignore = false)
    @Mapping(target = "telephone", ignore = false)
    @Mapping(target = "email", ignore = false)
    @Mapping(target = "notes", ignore = false)
    public abstract void parcialPatchUpdate
            (ClienteRequest clienteRequest,
             @MappingTarget Cliente cliente);



}
