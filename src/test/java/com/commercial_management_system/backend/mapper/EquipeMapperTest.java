package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.dto.equipe.EquipeResponsePage;
import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.model.Equipe;
import com.commercial_management_system.backend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipeMapperTest {

    private final EquipeMapper mapper = Mappers.getMapper(EquipeMapper.class);

    // valida conversao de requisicao em entidade equipe
    @Test
    @DisplayName("deve converter request em entidade equipe")
    void deveConverterRequestParaEntidadeEquipe() {
        EquipeRequest request = new EquipeRequest("Equipe Alpha", List.of(1L, 2L));

        Equipe entity = mapper.toEquipe(request);

        assertNotNull(entity);
        assertEquals("Equipe Alpha", entity.getNome());
        assertNull(entity.getSupervisor());
        assertTrue(entity.getVendedores().isEmpty());
    }

    // valida conversao completa de entidade para dto de equipe detalhada
    @Test
    @DisplayName("deve converter entidade para equipe response")
    void deveConverterEntidadeParaEquipeResponse() {
        User supervisor = new User("Supervisor", "sup@example.com", "11988880000", "hash", UserType.ROLE_SUPERVISOR, UserStatus.ACTIVE);
        supervisor.setId(1L);

        User vendedor = new User("Vendedor", "vend@example.com", "11977770000", "hash", UserType.ROLE_VENDEDOR, UserStatus.ACTIVE);
        vendedor.setId(2L);

        Equipe equipe = new Equipe();
        equipe.setId(10L);
        equipe.setNome("Equipe Vendas");
        equipe.setSupervisor(supervisor);
        equipe.setVendedores(List.of(vendedor));

        EquipeResponse response = mapper.toEquipeResponse(equipe);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Equipe Vendas", response.nome());
        assertNotNull(response.supervisor());
        assertEquals("Supervisor", response.supervisor().name());
        assertEquals(1, response.vendedores().size());
        assertEquals("Vendedor", response.vendedores().get(0).name());
    }

    // valida conversao de entidade usuario para resumo publico
    @Test
    @DisplayName("deve converter entidade usuario para user summary response")
    void deveConverterUsuarioParaUserSummaryResponse() {
        User user = new User("Mariana", "mariana@example.com", "11966665555", "hash", UserType.ROLE_VENDEDOR, UserStatus.ACTIVE);
        user.setId(3L);

        UserSummaryResponse response = mapper.toUserSummaryResponse(user);

        assertNotNull(response);
        assertEquals(3L, response.id());
        assertEquals("Mariana", response.name());
        assertEquals("mariana@example.com", response.email());
        assertEquals(UserType.ROLE_VENDEDOR, response.userType());
    }

    // valida conversao de equipe para dto de paginacao
    @Test
    @DisplayName("deve converter equipe para equipe response page")
    void deveConverterEquipeParaEquipeResponsePage() {
        User supervisor = new User("Supervisor", "sup@example.com", "11988880000", "hash", UserType.ROLE_SUPERVISOR, UserStatus.ACTIVE);
        supervisor.setId(1L);

        Equipe equipe = new Equipe();
        equipe.setId(15L);
        equipe.setNome("Equipe Regional");
        equipe.setSupervisor(supervisor);
        equipe.setVendedores(List.of());

        EquipeResponsePage response = mapper.toEquipeResponsePage(equipe);

        assertNotNull(response);
        assertEquals(15L, response.id());
        assertEquals("Equipe Regional", response.nome());
        assertEquals("Supervisor", response.supervisor().name());
        assertEquals(0, response.vendedores().size());
    }
}
