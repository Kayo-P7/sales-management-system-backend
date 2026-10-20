package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.dto.user.UserSummaryResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.exception.BusinessException;
import com.commercial_management_system.backend.mapper.EquipeMapper;
import com.commercial_management_system.backend.model.Equipe;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.EquipeRepository;
import com.commercial_management_system.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EquipeMapper equipeMapper;

    @InjectMocks
    private EquipeService equipeService;

    private User supervisor;

    @BeforeEach
    void setUp() {
        supervisor = new User("Supervisor Teste", "sup@example.com", "11988880000", "hash", UserType.ROLE_SUPERVISOR, UserStatus.ACTIVE);
        supervisor.setId(10L);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                supervisor, null, supervisor.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // valida cadastro de equipe com supervisor e vendedor
    @Test
    @DisplayName("deve cadastrar equipe com sucesso")
    void deveCadastrarEquipeComSucesso() {
        EquipeRequest request = new EquipeRequest("Equipe Alpha", List.of(20L));
        Equipe equipe = new Equipe();
        equipe.setId(1L);
        equipe.setNome("Equipe Alpha");

        User vendedor = new User("Vendedor 1", "vendedor@example.com", "11977770000", "hash", UserType.ROLE_VENDEDOR, UserStatus.ACTIVE);
        vendedor.setId(20L);

        UserSummaryResponse sup = new UserSummaryResponse(10L, "Supervisor Teste", "sup@example.com", "11988880000", UserType.ROLE_SUPERVISOR);
        UserSummaryResponse vend = new UserSummaryResponse(20L, "Vendedor 1", "vendedor@example.com", "11977770000", UserType.ROLE_VENDEDOR);
        EquipeResponse responseEsperada = new EquipeResponse(1L, "Equipe Alpha", List.of(vend), sup);

        when(equipeRepository.existsBySupervisorId(10L)).thenReturn(false);
        when(equipeMapper.toEquipe(request)).thenReturn(equipe);
        when(equipeRepository.save(equipe)).thenReturn(equipe);
        when(userRepository.findById(20L)).thenReturn(Optional.of(vendedor));
        when(equipeMapper.toEquipeResponse(equipe)).thenReturn(responseEsperada);

        EquipeResponse response = equipeService.save(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Equipe Alpha", response.nome());
        verify(userRepository).save(vendedor);
    }

    // valida lancamento de excecao quando supervisor ja possui equipe
    @Test
    @DisplayName("deve lancar BusinessException quando supervisor ja possui equipe")
    void deveLancarExcecaoQuandoSupervisorJaPossuiEquipe() {
        EquipeRequest request = new EquipeRequest("Equipe Beta", List.of(20L));

        when(equipeRepository.existsBySupervisorId(10L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> equipeService.save(request));
    }

    // valida busca de equipe do supervisor autenticado
    @Test
    @DisplayName("deve buscar equipe do supervisor autenticado")
    void deveBuscarEquipeDoSupervisor() {
        Equipe equipe = new Equipe();
        equipe.setId(1L);
        equipe.setNome("Equipe Alpha");
        UserSummaryResponse sup = new UserSummaryResponse(10L, "Supervisor Teste", "sup@example.com", "11988880000", UserType.ROLE_SUPERVISOR);
        EquipeResponse responseEsperada = new EquipeResponse(1L, "Equipe Alpha", List.of(), sup);

        when(equipeRepository.findAllBySupervisorId(10L)).thenReturn(Optional.of(equipe));
        when(equipeMapper.toEquipeResponse(equipe)).thenReturn(responseEsperada);

        EquipeResponse response = equipeService.findAllBySupervisorId();

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Equipe Alpha", response.nome());
    }

    // valida remocao de vendedor da equipe do supervisor
    @Test
    @DisplayName("deve remover vendedor da equipe com sucesso")
    void deveRemoverVendedorDaEquipe() {
        Equipe equipe = new Equipe();
        equipe.setId(1L);
        equipe.setSupervisor(supervisor);

        User vendedor = new User("Vendedor 1", "vendedor@example.com", "11977770000", "hash", UserType.ROLE_VENDEDOR, UserStatus.ACTIVE);
        vendedor.setId(20L);
        vendedor.setEquipe(equipe);

        when(userRepository.findById(20L)).thenReturn(Optional.of(vendedor));

        equipeService.deleteByVendedor(20L);

        assertNull(vendedor.getEquipe());
        verify(userRepository).save(vendedor);
    }
}
