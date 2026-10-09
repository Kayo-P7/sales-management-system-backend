package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.user.UserRequest;
import com.commercial_management_system.backend.dto.user.UserResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.exception.UserAlreadyExistsException;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // valida que a senha do usuario e salva criptografada
    @Test
    @DisplayName("deve cadastrar usuario criptografando a senha")
    void deveCadastrarUsuarioComSenhaCriptografada() {
        UserRequest request = new UserRequest(
                "João Silva",
                "joao@example.com",
                "11999998888",
                "senha123",
                UserType.ROLE_USER
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByTelephone(request.telephone())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash_criptografado");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = userService.salvar(request);

        assertNotNull(resultado);
        assertEquals("hash_criptografado", resultado.getPassword());
        assertEquals(UserStatus.ACTIVE, resultado.getUserStatus());
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
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
                UserType.ROLE_USER
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.salvar(request));
        verify(userRepository, never()).save(any(User.class));
    }

    // valida consulta paginada de usuarios
    @Test
    @DisplayName("deve listar usuarios com paginacao")
    void deveListarUsuariosComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User("Carlos", "carlos@example.com", "11977776666", "hash", UserType.ROLE_USER, UserStatus.ACTIVE);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserResponse> resultado = userService.listar(null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(userRepository).findAll(pageable);
    }
}
