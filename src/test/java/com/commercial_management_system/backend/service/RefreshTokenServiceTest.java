package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.TokenResponse;
import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.exception.InvalidRefreshTokenException;
import com.commercial_management_system.backend.model.RefreshToken;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Teste", "teste@email.com", "11999999999", "senha", UserType.ROLE_USER, UserStatus.ACTIVE);
    }

    // valida que um refresh token e criado com validade futura e salvo no repositorio
    @Test
    @DisplayName("deve criar refresh token valido para o usuario")
    void deveCriarRefreshTokenValido() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        RefreshToken token = refreshTokenService.criarRefreshToken(user);

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertFalse(token.isRevoked());
        assertTrue(token.getExpiryDate().isAfter(Instant.now()));
        assertEquals(user, token.getUser());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    // valida rotacao de refresh token gerando novo par e revogando o anterior
    @Test
    @DisplayName("deve renovar token aplicando rotacao e revogando o anterior")
    void deveRenovarTokenComRotacao() {
        String tokenAntigoValor = "token-antigo-uuid";
        RefreshToken tokenAntigo = new RefreshToken(tokenAntigoValor, user, Instant.now().plus(7, ChronoUnit.DAYS));

        when(refreshTokenRepository.findByToken(tokenAntigoValor)).thenReturn(Optional.of(tokenAntigo));
        when(tokenService.gerarToken(user)).thenReturn("novo-jwt-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        TokenResponse response = refreshTokenService.renovarToken(tokenAntigoValor);

        assertNotNull(response);
        assertEquals("novo-jwt-token", response.token());
        assertNotNull(response.refreshToken());
        assertNotEquals(tokenAntigoValor, response.refreshToken());
        assertTrue(tokenAntigo.isRevoked());
    }

    // valida que token expirado dispara excecao de refresh token invalido
    @Test
    @DisplayName("deve rejeitar renovacao quando refresh token estiver expirado")
    void deveRejeitarTokenExpirado() {
        String tokenExpiradoValor = "token-expirado";
        RefreshToken tokenExpirado = new RefreshToken(tokenExpiradoValor, user, Instant.now().minus(1, ChronoUnit.DAYS));

        when(refreshTokenRepository.findByToken(tokenExpiradoValor)).thenReturn(Optional.of(tokenExpirado));

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.renovarToken(tokenExpiradoValor));
    }

    // valida que token revogado dispara excecao ao tentar renovar
    @Test
    @DisplayName("deve rejeitar renovacao quando refresh token ja tiver sido revogado")
    void deveRejeitarTokenRevogado() {
        String tokenRevogadoValor = "token-revogado";
        RefreshToken tokenRevogado = new RefreshToken(tokenRevogadoValor, user, Instant.now().plus(7, ChronoUnit.DAYS));
        tokenRevogado.setRevoked(true);

        when(refreshTokenRepository.findByToken(tokenRevogadoValor)).thenReturn(Optional.of(tokenRevogado));

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.renovarToken(tokenRevogadoValor));
    }

    // valida revogacao de token ao invocar logout
    @Test
    @DisplayName("deve revogar refresh token ativo no logout")
    void deveRevogarTokenNoLogout() {
        String tokenValor = "token-para-logout";
        RefreshToken token = new RefreshToken(tokenValor, user, Instant.now().plus(7, ChronoUnit.DAYS));

        when(refreshTokenRepository.findByToken(tokenValor)).thenReturn(Optional.of(token));

        refreshTokenService.revogarRefreshToken(tokenValor);

        assertTrue(token.isRevoked());
        verify(refreshTokenRepository).save(token);
    }
}
