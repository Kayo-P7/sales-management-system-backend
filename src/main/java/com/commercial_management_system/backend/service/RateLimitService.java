package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.config.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RateLimitProperties properties;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // verifica e consome um token do bucket correspondente ao cliente
    public ConsumptionProbe tryConsume(HttpServletRequest request) {
        String clientIp = extrairIpCliente(request);
        String uri = request.getRequestURI();

        // define politica para rotas de autenticacao e rotas gerais
        boolean rotaAuth = uri != null && uri.startsWith("/api/v1/auth");
        String chave = (rotaAuth ? "auth:" : "general:") + clientIp;
        RateLimitProperties.Policy policy = rotaAuth ? properties.getAuth() : properties.getGeneral();

        Bucket bucket = this.buckets.computeIfAbsent(chave, k -> criarBucket(policy));
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    // indica se o rate limiting esta habilitado
    public boolean isEnabled() {
        return properties.isEnabled();
    }

    // extrai o ip real do cliente considerando cabecalhos de proxy reverso
    public String extrairIpCliente(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr != null && !remoteAddr.isBlank()) ? remoteAddr : "unknown";
    }

    // controi um novo bucket com base na politica configurada
    private Bucket criarBucket(RateLimitProperties.Policy policy) {
        Bandwidth limite = Bandwidth.builder()
                .capacity(policy.getCapacity())
                .refillGreedy(policy.getRefillTokens(), Duration.ofSeconds(policy.getRefillDurationInSeconds()))
                .build();

        return Bucket.builder()
                .addLimit(limite)
                .build();
    }

    // limpa o mapa de buckets em memoria para fins de teste
    public void limparBuckets() {
        this.buckets.clear();
    }
}
