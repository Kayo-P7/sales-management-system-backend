package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
                .claim("role", user.getUserType() != null ? user.getUserType().name() : "ROLE_VENDEDOR")
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

    // calcula instante de expiracao de forma agnostica a fuso horario
    private Instant dataExpiracao() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}
