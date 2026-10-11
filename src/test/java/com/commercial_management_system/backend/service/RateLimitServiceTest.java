package com.commercial_management_system.backend.service;

import com.commercial_management_system.backend.config.RateLimitProperties;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitServiceTest {

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties();
        // define capacidade de 2 para auth e 3 para rotas gerais para agilizar testes
        properties.setAuth(new RateLimitProperties.Policy(2, 2, 60));
        properties.setGeneral(new RateLimitProperties.Policy(3, 3, 60));
        rateLimitService = new RateLimitService(properties);
    }

    // valida consumo dentro da capacidade e rejeicao ao esgotar tokens para auth
    @Test
    @DisplayName("deve permitir requisicoes ate o limite e rejeitar quando esgotado")
    void deveLimitarRequisicoesAoAtingirCapacidade() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr("192.168.1.100");

        ConsumptionProbe p1 = rateLimitService.tryConsume(request);
        assertTrue(p1.isConsumed());
        assertEquals(1, p1.getRemainingTokens());

        ConsumptionProbe p2 = rateLimitService.tryConsume(request);
        assertTrue(p2.isConsumed());
        assertEquals(0, p2.getRemainingTokens());

        ConsumptionProbe p3 = rateLimitService.tryConsume(request);
        assertFalse(p3.isConsumed());
        assertTrue(p3.getNanosToWaitForRefill() > 0);
    }

    // valida que ips diferentes possuem contadores isolados
    @Test
    @DisplayName("deve isolar baldes de tokens para ips diferentes")
    void deveIsolarBaldesParaIpsDiferentes() {
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.setRequestURI("/api/v1/users");
        request1.setRemoteAddr("10.0.0.1");

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.setRequestURI("/api/v1/users");
        request2.setRemoteAddr("10.0.0.2");

        rateLimitService.tryConsume(request1);
        rateLimitService.tryConsume(request1);
        rateLimitService.tryConsume(request1);

        // ip 1 deve estar esgotado para o limite de 3
        ConsumptionProbe p1Excedido = rateLimitService.tryConsume(request1);
        assertFalse(p1Excedido.isConsumed());

        // ip 2 ainda deve possuir tokens disponiveis
        ConsumptionProbe p2Disponivel = rateLimitService.tryConsume(request2);
        assertTrue(p2Disponivel.isConsumed());
    }

    // valida extracao correta do ip prioritario no cabecalho x-forwarded-for
    @Test
    @DisplayName("deve extrair primeiro ip de proxy no cabecalho x-forwarded-for")
    void deveExtrairIpDoCabecalhoXForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178");
        request.setRemoteAddr("127.0.0.1");

        String ip = rateLimitService.extrairIpCliente(request);
        assertEquals("203.0.113.195", ip);
    }
}
