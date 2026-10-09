package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    // gera chave secreta a partir do segredo configurado
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // gera token jwt assinado com subject, claims e expiracao
    public String gerarToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer("sales-management-system-backend")
                .claim("id", user.getId())
                .claim("role", user.getUserType() != null ? user.getUserType().name() : "ROLE_USER")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(dataExpiracao()))
                .signWith(getSigningKey())
                .compact();
    }

    // valida token e recupera o email do usuario autenticado
    public String getSubject(String tokenJWT) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(tokenJWT)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // calcula data de expiracao em fuso horario de brasilia
    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
