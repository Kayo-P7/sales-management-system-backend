package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
import com.commercial_management_system.backend.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClienteMapperTest {

    private final ClienteMapper mapper = Mappers.getMapper(ClienteMapper.class);

    // valida conversao completa de request para entidade cliente
    @Test
    @DisplayName("deve converter request em entidade cliente")
    void deveConverterRequestParaEntidadeCliente() {
        ClienteRequest request = new ClienteRequest("João Silva", "11999998888", "joao@example.com", "cliente fidelizado");

        Cliente entity = mapper.toCliente(request);

        assertNotNull(entity);
        assertEquals("João Silva", entity.getName());
        assertEquals("11999998888", entity.getTelephone());
        assertEquals("joao@example.com", entity.getEmail());
        assertEquals("cliente fidelizado", entity.getNotes());
        assertNull(entity.getId());
    }

    // valida conversao de entidade para dto de resposta
    @Test
    @DisplayName("deve converter entidade em dto cliente response")
    void deveConverterEntidadeParaClienteResponse() {
        LocalDateTime agora = LocalDateTime.now();
        Cliente entity = new Cliente();
        entity.setId(10L);
        entity.setName("Maria Santos");
        entity.setTelephone("11988887777");
        entity.setEmail("maria@example.com");
        entity.setNotes("preferencia por suv");
        entity.setCreatedAt(agora);
        entity.setUpdatedAt(agora);
        entity.setCreatedBy(1L);
        entity.setUpdatedBy(2L);

        ClienteResponse response = mapper.toClienteResponse(entity);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Maria Santos", response.name());
        assertEquals("11988887777", response.telephone());
        assertEquals("maria@example.com", response.email());
        assertEquals("preferencia por suv", response.notes());
        assertEquals(agora, response.createdAt());
        assertEquals("1", response.createdBy());
        assertEquals("2", response.updatedBy());
    }

    // valida atualizacao total dos atributos da entidade
    @Test
    @DisplayName("deve atualizar atributos da entidade cliente a partir do request")
    void deveAtualizarClienteCompletamente() {
        Cliente entity = new Cliente();
        entity.setName("Nome Antigo");
        entity.setTelephone("11111111111");
        entity.setEmail("antigo@example.com");
        entity.setNotes("nota antiga");

        ClienteRequest request = new ClienteRequest("Nome Novo", "22222222222", "novo@example.com", "nota nova");

        mapper.updateCliente(request, entity);

        assertEquals("Nome Novo", entity.getName());
        assertEquals("22222222222", entity.getTelephone());
        assertEquals("novo@example.com", entity.getEmail());
        assertEquals("nota nova", entity.getNotes());
    }

    // valida que a atualizacao parcial ignora campos nulos
    @Test
    @DisplayName("deve atualizar apenas campos informados ignorando nulos no patch")
    void deveAtualizarClienteParcialmenteIgnorandoNulos() {
        Cliente entity = new Cliente();
        entity.setName("Nome Mantido");
        entity.setTelephone("11999990000");
        entity.setEmail("original@example.com");
        entity.setNotes("nota original");

        ClienteRequest patchRequest = new ClienteRequest(null, "11988881111", null, "nota atualizada");

        mapper.patchUpdate(patchRequest, entity);

        assertEquals("Nome Mantido", entity.getName());
        assertEquals("11988881111", entity.getTelephone());
        assertEquals("original@example.com", entity.getEmail());
        assertEquals("nota atualizada", entity.getNotes());
    }
}
