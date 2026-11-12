package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.user.UserRequest;
import com.commercial_management_system.backend.dto.user.UserResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.exception.BusinessException;
import com.commercial_management_system.backend.exception.UserAlreadyExistsException;
import com.commercial_management_system.backend.mapper.UserMapper;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    // limpa o contexto de seguranca entre os testes
    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    // valida que a senha do usuario e salva criptografada
    @Test
    @DisplayName("deve cadastrar usuario criptografando a senha")
    void deveCadastrarUsuarioComSenhaCriptografada() {
        UserRequest request = new UserRequest(
                "João Silva",
                "joao@example.com",
                "11999998888",
                "senha123",
                UserType.ROLE_ADMIN
        );
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setTelephone(request.telephone());
        user.setUserType(request.userType());

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByTelephone(request.telephone())).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode("senha123")).thenReturn("hash_criptografado");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse responseEsperada = new UserResponse(1L, "João Silva", "joao@example.com", "11999998888", UserType.ROLE_ADMIN, UserStatus.ACTIVE);
        when(userMapper.toUserResponse(user)).thenReturn(responseEsperada);

        UserResponse resultado = userService.salvar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("João Silva", resultado.name());
        assertEquals("joao@example.com", resultado.email());
        assertEquals(UserType.ROLE_ADMIN, resultado.userType());
        assertEquals(UserStatus.ACTIVE, resultado.userStatus());
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(user);
    }

    // valida lancamento de excecao quando o email ja existe
    @Test
    @DisplayName("deve lancar excecao ao tentar cadastrar email duplicado")
    void deveLancarExcecaoQuandoEmailDuplicado() {
        UserRequest request = new UserRequest(
                "Maria Souza",
                "maria@example.com",
                "11988887777",
                "senha123",
                UserType.ROLE_ADMIN
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.salvar(request));
        verify(userRepository, never()).save(any(User.class));
    }

    // valida bloqueio da auto inativacao do usuario autenticado
    @Test
    @DisplayName("deve impedir que o usuario autenticado desative a propria conta")
    void deveImpedirAutoDesativacao() {
        User autenticado = new User(
                "Victor",
                "victor@example.com",
                "11999999999",
                "hash",
                UserType.ROLE_ADMIN,
                UserStatus.ACTIVE
        );
        autenticado.setId(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(autenticado, null, autenticado.getAuthorities())
        );

        assertThrows(BusinessException.class, () -> userService.desativar(1L));
        verify(userRepository, never()).save(any(User.class));
    }

    // valida consulta paginada de usuarios
    @Test
    @DisplayName("deve listar usuarios com paginacao")
    void deveListarUsuariosComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User("Carlos", "carlos@example.com", "11977776666", "hash", UserType.ROLE_ADMIN, UserStatus.ACTIVE);
        Page<User> page = new PageImpl<>(List.of(user));
        UserResponse responseEsperada = new UserResponse(1L, "Carlos", "carlos@example.com", "11977776666", UserType.ROLE_ADMIN, UserStatus.ACTIVE);

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toUserResponse(user)).thenReturn(responseEsperada);

        Page<UserResponse> resultado = userService.listar(null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(userRepository).findAll(pageable);
    }
}
