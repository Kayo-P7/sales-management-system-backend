package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.user.UserRequest;
import com.commercial_management_system.backend.dto.user.UserResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    // valida cadastro de usuario com retorno 201 created
    @Test
    @DisplayName("deve cadastrar usuario e retornar status 201")
    void deveCadastrarUsuarioRetornando201() throws Exception {
        UserRequest request = new UserRequest("Carlos Silva", "carlos@example.com", "11988887777", "senha123", UserType.ROLE_ADMIN);
        UserResponse response = new UserResponse(1L, "Carlos Silva", "carlos@example.com", "11988887777", UserType.ROLE_ADMIN, UserStatus.ACTIVE);

        when(userService.salvar(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlos Silva"))
                .andExpect(jsonPath("$.email").value("carlos@example.com"))
                .andExpect(jsonPath("$.userType").value("ROLE_ADMIN"));
    }

    // valida listagem de usuarios com retorno 200 ok
    @Test
    @DisplayName("deve listar usuarios e retornar status 200")
    void deveListarUsuariosRetornando200() throws Exception {
        UserResponse response = new UserResponse(1L, "Carlos Silva", "carlos@example.com", "11988887777", UserType.ROLE_ADMIN, UserStatus.ACTIVE);
        when(userService.listar(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Carlos Silva"));
    }

    // valida busca de usuario por id com retorno 200 ok
    @Test
    @DisplayName("deve buscar usuario por id e retornar status 200")
    void deveBuscarUsuarioPorIdRetornando200() throws Exception {
        UserResponse response = new UserResponse(1L, "Carlos Silva", "carlos@example.com", "11988887777", UserType.ROLE_ADMIN, UserStatus.ACTIVE);
        when(userService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlos Silva"));
    }

    // valida desativacao de usuario com retorno 204 no content
    @Test
    @DisplayName("deve desativar usuario e retornar status 204")
    void deveDesativarUsuarioRetornando204() throws Exception {
        doNothing().when(userService).desativar(1L);

        mockMvc.perform(patch("/api/v1/users/1/deactivate"))
                .andExpect(status().isNoContent());

        verify(userService).desativar(1L);
    }

    // valida ativacao de usuario com retorno 204 no content
    @Test
    @DisplayName("deve ativar usuario e retornar status 204")
    void deveAtivarUsuarioRetornando204() throws Exception {
        doNothing().when(userService).ativar(1L);

        mockMvc.perform(patch("/api/v1/users/1/activate"))
                .andExpect(status().isNoContent());

        verify(userService).ativar(1L);
    }
}
