package com.commercial_management_system.backend.mapper;

import com.commercial_management_system.backend.dto.user.UserRequest;
import com.commercial_management_system.backend.dto.user.UserResponse;
import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    // valida conversao de request para entidade usuario com senha ignorada
    @Test
    @DisplayName("deve converter request em entidade usuario ignorando senha")
    void deveConverterRequestParaEntidadeUsuario() {
        UserRequest request = new UserRequest("Ana Paula", "ana@example.com", "11988883333", "segredo", UserType.ROLE_SUPERVISOR);

        User entity = mapper.toUser(request);

        assertNotNull(entity);
        assertEquals("Ana Paula", entity.getName());
        assertEquals("ana@example.com", entity.getEmail());
        assertEquals("11988883333", entity.getTelephone());
        assertEquals(UserType.ROLE_SUPERVISOR, entity.getUserType());
        assertNull(entity.getPassword());
        assertNull(entity.getId());
    }

    // valida conversao de entidade para dto de resposta completo
    @Test
    @DisplayName("deve converter entidade para user response")
    void deveConverterEntidadeParaUserResponse() {
        User entity = new User("Bruno Costa", "bruno@example.com", "11977772222", "hash", UserType.ROLE_ADMIN, UserStatus.ACTIVE);
        entity.setId(8L);

        UserResponse response = mapper.toUserResponse(entity);

        assertNotNull(response);
        assertEquals(8L, response.id());
        assertEquals("Bruno Costa", response.name());
        assertEquals("bruno@example.com", response.email());
        assertEquals("11977772222", response.telephone());
        assertEquals(UserType.ROLE_ADMIN, response.userType());
        assertEquals(UserStatus.ACTIVE, response.userStatus());
    }

    // valida conversao de entidade para dto resumido
    @Test
    @DisplayName("deve converter entidade para user summary response")
    void deveConverterEntidadeParaUserSummaryResponse() {
        User entity = new User("Gabriel Lima", "gabriel@example.com", "11966661111", "hash", UserType.ROLE_VENDEDOR, UserStatus.ACTIVE);
        entity.setId(14L);

        UserSummaryResponse response = mapper.toUserSummaryResponse(entity);

        assertNotNull(response);
        assertEquals(14L, response.id());
        assertEquals("Gabriel Lima", response.name());
        assertEquals("gabriel@example.com", response.email());
        assertEquals("11966661111", response.telephone());
        assertEquals(UserType.ROLE_VENDEDOR, response.userType());
    }
}
