package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.config.RateLimitProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final RateLimitProperties properties;
    private final MeterRegistry meterRegistry;
    private final Cache<String, Bucket> buckets;

    public RateLimitService(RateLimitProperties properties, MeterRegistry meterRegistry) {
        this.properties = properties;
        this.meterRegistry = meterRegistry;
        // configura cache em memoria com politica de expiracao por inatividade e capacidade maxima
        this.buckets = Caffeine.newBuilder()
                .maximumSize(50_000)
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .build();
        // registra medicao continua de buckets ativos em memoria
        meterRegistry.gauge("rate_limit_active_buckets", this.buckets, b -> (double) b.estimatedSize());
    }

    // verifica e consome um token do bucket correspondente ao cliente
    public ConsumptionProbe tryConsume(HttpServletRequest request) {
        String clientIp = extrairIpCliente(request);
        String uri = request.getRequestURI();

        // define politica para rotas de autenticacao e rotas gerais
        boolean rotaAuth = uri != null && uri.startsWith("/api/v1/auth");
        String chave = (rotaAuth ? "auth:" : "general:") + clientIp;
        RateLimitProperties.Policy policy = rotaAuth ? properties.getAuth() : properties.getGeneral();

        Bucket bucket = buckets.get(chave, k -> criarBucket(policy));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // registra metricas de requisicoes permitidas e rejeitadas
        String routeTag = rotaAuth ? "auth" : "general";
        String resultTag = probe.isConsumed() ? "allowed" : "rejected";
        meterRegistry.counter("rate_limit_requests_total", "route", routeTag, "result", resultTag).increment();

        return probe;
    }

    // indica se o rate limiting esta habilitado
    public boolean isEnabled() {
        return properties.isEnabled();
    }

    // extrai o ip real do cliente considerando o proxy reverso e cabecalhos sanitizados
    public String extrairIpCliente(HttpServletRequest request) {
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr != null && !remoteAddr.isBlank()) ? remoteAddr : "unknown";
    }

    // constroi um novo bucket com base na politica configurada
    private Bucket criarBucket(RateLimitProperties.Policy policy) {
        Bandwidth limite = Bandwidth.builder()
                .capacity(policy.getCapacity())
                .refillGreedy(policy.getRefillTokens(), Duration.ofSeconds(policy.getRefillDurationInSeconds()))
                .build();

        return Bucket.builder()
                .addLimit(limite)
                .build();
    }

    // limpa o cache de buckets em memoria para fins de teste
    public void limparBuckets() {
        buckets.invalidateAll();
    }
}
