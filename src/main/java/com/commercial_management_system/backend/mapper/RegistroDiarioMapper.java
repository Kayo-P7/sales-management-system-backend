package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioRequest;
import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioResponse;
import com.commercial_management_system.backend.model.RegistroDiario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface RegistroDiarioMapper {

    // converte requisicao de indicadores em entidade de registro diario
    RegistroDiario toRegistroDiario(RegistroDiarioRequest request);

    // converte entidade em dto de resposta mapeando dados do vendedor associado
    @Mapping(source = "vendedor.id", target = "vendedorId")
    @Mapping(source = "vendedor.name", target = "nomeVendedor")
    RegistroDiarioResponse toRegistroDiarioResponse(RegistroDiario registroDiario);
}
