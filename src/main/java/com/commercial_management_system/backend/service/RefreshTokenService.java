package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.dto.TokenResponse;
import com.commercial_management_system.backend.exception.InvalidRefreshTokenException;
import com.commercial_management_system.backend.model.RefreshToken;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

    // gera novo refresh token com validade de 7 dias vinculado ao usuario
    @Transactional
    public RefreshToken criarRefreshToken(User user) {
        Instant expiryDate = Instant.now().plus(7, ChronoUnit.DAYS);
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken(token, user, expiryDate);
        return refreshTokenRepository.save(refreshToken);
    }

    // renova os tokens aplicando rotacao estrita (invalida o token anterior)
    @Transactional
    public TokenResponse renovarToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("refresh token nao encontrado"));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("refresh token ja foi revogado");
        }

        if (refreshToken.isExpired()) {
            throw new InvalidRefreshTokenException("refresh token expirado");
        }

        // invalida o refresh token atual para evitar reutilizacao
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();

        // gera novo access token jwt e novo refresh token rotacionado
        String novoAccessToken = tokenService.gerarToken(user);
        RefreshToken novoRefreshToken = criarRefreshToken(user);

        return new TokenResponse(novoAccessToken, novoRefreshToken.getToken());
    }

    // revoga refresh token ativo no encerramento de sessao (logout)
    @Transactional
    public void revogarRefreshToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return;
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue).orElse(null);
        if (refreshToken != null && !refreshToken.isRevoked()) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        }
    }
}
