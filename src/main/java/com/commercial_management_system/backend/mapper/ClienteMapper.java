package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
import com.commercial_management_system.backend.model.Cliente;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = GlobalMapperConfig.class)
public interface ClienteMapper {

    // converte requisicao em entidade cliente
    Cliente toCliente(ClienteRequest clienteRequest);

    // converte entidade cliente em dto de resposta
    ClienteResponse toClienteResponse(Cliente cliente);

    // atualiza todos os dados da entidade a partir da requisicao
    void updateCliente(ClienteRequest clienteRequest, @MappingTarget Cliente cliente);

    // atualiza parcialmente ignorando campos nulos na requisicao
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUpdate(ClienteRequest clienteRequest, @MappingTarget Cliente cliente);
}
