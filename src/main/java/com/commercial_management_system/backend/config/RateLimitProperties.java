package com.commercial_management_system.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "api.rate-limit")
public class RateLimitProperties {

    // habilita ou desabilita o mecanismo de rate limit globalmente
    private boolean enabled = true;

    // politica de rate limit para rotas de autenticacao
    private Policy auth = new Policy(10, 10, 60);

    // politica de rate limit para rotas gerais da api
    private Policy general = new Policy(60, 60, 60);

    @Getter
    @Setter
    public static class Policy {
        // capacidade maxima de tokens no bucket
        private long capacity;

        // quantidade de tokens repostos por ciclo
        private long refillTokens;

        // duracao em segundos do ciclo de reposicao
        private long refillDurationInSeconds;

        public Policy() {
        }

        public Policy(long capacity, long refillTokens, long refillDurationInSeconds) {
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.refillDurationInSeconds = refillDurationInSeconds;
        }
    }
}
