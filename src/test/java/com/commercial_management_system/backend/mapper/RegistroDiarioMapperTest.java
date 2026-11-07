package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioRequest;
import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.model.RegistroDiario;
import com.commercial_management_system.backend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RegistroDiarioMapperTest {

    private final RegistroDiarioMapper mapper = Mappers.getMapper(RegistroDiarioMapper.class);

    // valida conversao de requisicao para entidade de registro diario
    @Test
    @DisplayName("deve converter request em entidade de registro diario")
    void deveConverterRequestParaEntidadeRegistroDiario() {
        RegistroDiarioRequest request = new RegistroDiarioRequest(3, 10, 5, 2, 1, 4, 1, 8);

        RegistroDiario entity = mapper.toRegistroDiario(request);

        assertNotNull(entity);
        assertEquals(3, entity.getPendenciasNovas());
        assertEquals(10, entity.getLigacoes());
        assertEquals(5, entity.getNegociacoes());
        assertEquals(2, entity.getAgendamentos());
        assertEquals(1, entity.getChamadasVideo());
        assertEquals(4, entity.getClientesEmLoja());
        assertEquals(1, entity.getFechamentos());
        assertEquals(8, entity.getAnuncios());
        assertNull(entity.getId());
        assertNull(entity.getVendedor());
    }

    // valida conversao de entidade mapeando dados do vendedor associado
    @Test
    @DisplayName("deve converter entidade para registro diario response mapeando dados do vendedor")
    void deveConverterEntidadeParaRegistroDiarioResponseMapeandoVendedor() {
        User vendedor = new User("Lucas Vendedor", "lucas@example.com", "11988884444", "hash", UserType.ROLE_VENDEDOR, UserStatus.ACTIVE);
        vendedor.setId(7L);

        LocalDate data = LocalDate.of(2026, 11, 6);
        RegistroDiario entity = new RegistroDiario();
        entity.setId(50L);
        entity.setVendedor(vendedor);
        entity.setData(data);
        entity.setPendenciasNovas(2);
        entity.setLigacoes(15);
        entity.setNegociacoes(4);
        entity.setAgendamentos(3);
        entity.setChamadasVideo(2);
        entity.setClientesEmLoja(5);
        entity.setFechamentos(1);
        entity.setAnuncios(12);

        RegistroDiarioResponse response = mapper.toRegistroDiarioResponse(entity);

        assertNotNull(response);
        assertEquals(50L, response.id());
        assertEquals(7L, response.vendedorId());
        assertEquals("Lucas Vendedor", response.nomeVendedor());
        assertEquals(data, response.data());
        assertEquals(2, response.pendenciasNovas());
        assertEquals(15, response.ligacoes());
        assertEquals(1, response.fechamentos());
    }
}
