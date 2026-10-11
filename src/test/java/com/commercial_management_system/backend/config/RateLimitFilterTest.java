package com.commercial_management_system.backend.config;

import com.commercial_management_system.backend.controller.HealthController;
import com.commercial_management_system.backend.service.RateLimitService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RateLimitFilterTest {

    private MockMvc mockMvc;
    private RateLimitService rateLimitService;

    @RestController
    @RequestMapping("/api/v1/auth")
    static class FakeAuthController {
        @PostMapping("/login")
        public ResponseEntity<String> login() {
            return ResponseEntity.ok("ok");
        }
    }

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setAuth(new RateLimitProperties.Policy(2, 2, 60));
        properties.setGeneral(new RateLimitProperties.Policy(5, 5, 60));

        rateLimitService = new RateLimitService(properties, new SimpleMeterRegistry());
        RateLimitFilter filter = new RateLimitFilter(rateLimitService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new HealthController(), new FakeAuthController())
                .addFilters(filter)
                .build();
    }

    // valida que rota de health bypassa o filtro de taxa
    @Test
    @DisplayName("deve ignorar limitacao na rota de health check")
    void deveIgnorarRateLimitNaRotaDeHealth() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/health"))
                    .andExpect(status().isOk())
                    .andExpect(header().doesNotExist("X-Rate-Limit-Remaining"));
        }
    }

    // valida retorno 429 com cabecalhos corretos ao exceder limite de requisicoes
    @Test
    @DisplayName("deve retornar 429 too many requests ao exceder limite configurado")
    void deveRetornar429AoExcederCapacidade() throws Exception {
        // primeira requisicao permitida
        mockMvc.perform(post("/api/v1/auth/login"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Rate-Limit-Remaining", "1"));

        // segunda requisicao permitida
        mockMvc.perform(post("/api/v1/auth/login"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Rate-Limit-Remaining", "0"));

        // terceira requisicao deve ser bloqueada com status 429
        mockMvc.perform(post("/api/v1/auth/login"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("X-Rate-Limit-Remaining", "0"))
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.erro").value("Too Many Requests"))
                .andExpect(jsonPath("$.caminho").value("/api/v1/auth/login"));
    }
}
