package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.exception.ResourceNotFoundException;
import com.commercial_management_system.backend.mapper.ClienteMapper;
import com.commercial_management_system.backend.model.Cliente;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.ClienteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    private User usuarioAutenticado;

    @BeforeEach
    void setUp() {
        usuarioAutenticado = new User("Vendedor Teste", "vendedor@example.com", "11988887777", "hash", UserType.ROLE_VENDEDOR, UserStatus.ACTIVE);
        usuarioAutenticado.setId(10L);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                usuarioAutenticado, null, usuarioAutenticado.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // valida cadastro de cliente com sucesso
    @Test
    @DisplayName("deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        ClienteRequest request = new ClienteRequest("João Silva", "11999998888", "joao@example.com", "nota");
        Cliente cliente = new Cliente();
        cliente.setName("João Silva");
        ClienteResponse responseEsperada = new ClienteResponse(1L, "João Silva", "11999998888", "joao@example.com", "nota", LocalDateTime.now(), null, "10", null);

        when(clienteMapper.toCliente(request)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toClientResponse(cliente)).thenReturn(responseEsperada);

        ClienteResponse response = clienteService.save(request);

        assertNotNull(response);
        assertEquals("João Silva", response.name());
        verify(clienteRepository).save(cliente);
    }

    // valida busca de cliente existente por identificador unico
    @Test
    @DisplayName("deve buscar cliente por id com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        ClienteResponse responseEsperada = new ClienteResponse(1L, "Maria", "11999991111", "maria@example.com", null, LocalDateTime.now(), null, "10", null);

        when(clienteRepository.findByIdAndCreatedBy(1L, 10L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toClientResponse(cliente)).thenReturn(responseEsperada);

        ClienteResponse response = clienteService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(clienteRepository).findByIdAndCreatedBy(1L, 10L);
    }

    // valida lancamento de excecao quando cliente nao pertencer ao usuario autenticado
    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando cliente nao for encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        when(clienteRepository.findByIdAndCreatedBy(99L, 10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clienteService.findById(99L));
        verify(clienteRepository).findByIdAndCreatedBy(99L, 10L);
    }

    // valida exclusao de cliente vinculado ao usuario
    @Test
    @DisplayName("deve excluir cliente com sucesso")
    void deveExcluirClienteComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        when(clienteRepository.findByIdAndCreatedBy(1L, 10L)).thenReturn(Optional.of(cliente));

        clienteService.deleteClienteById(1L);

        verify(clienteRepository).delete(cliente);
    }

    // valida listagem paginada de clientes do usuario autenticado
    @Test
    @DisplayName("deve listar clientes com paginacao")
    void deveListarClientesPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        Cliente cliente = new Cliente();
        Page<Cliente> pagina = new PageImpl<>(List.of(cliente));
        ClienteResponse responseEsperada = new ClienteResponse(1L, "Carlos", "11999992222", "carlos@example.com", null, LocalDateTime.now(), null, "10", null);

        when(clienteRepository.findAllByCreatedBy(10L, pageable)).thenReturn(pagina);
        when(clienteMapper.toClientResponse(cliente)).thenReturn(responseEsperada);

        Page<ClienteResponse> resultado = clienteService.listarClientes(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(clienteRepository).findAllByCreatedBy(10L, pageable);
    }
}
