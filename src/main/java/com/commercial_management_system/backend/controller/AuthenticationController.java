package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.AuthenticationRequest;
import com.commercial_management_system.backend.dto.RefreshTokenRequest;
import com.commercial_management_system.backend.dto.TokenResponse;
import com.commercial_management_system.backend.model.RefreshToken;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.service.RefreshTokenService;
import com.commercial_management_system.backend.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    // autentica credenciais e retorna token jwt com refresh token
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthenticationRequest authRequest) {
        // cria token de autenticacao com email e senha informados
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                authRequest.email(), authRequest.password());

        // valida credenciais atraves do authentication manager
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User user = (User) authentication.getPrincipal();

        // gera token jwt de acesso e refresh token persistido
        String jwt = tokenService.gerarToken(user);
        RefreshToken refreshToken = refreshTokenService.criarRefreshToken(user);

        return ResponseEntity.ok(new TokenResponse(jwt, refreshToken.getToken()));
    }

    // renova token de acesso aplicando rotacao no refresh token
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = refreshTokenService.renovarToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    // encerra a sessao invalidando o refresh token
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revogarRefreshToken(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
