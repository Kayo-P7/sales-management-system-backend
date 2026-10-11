package com.commercial_management_system.backend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ActuatorSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    // valida que endpoint de health do actuator e publico para probes de orquestracao
    @Test
    @DisplayName("deve permitir acesso anonimo ao endpoint /actuator/health")
    void devePermitirAcessoAnonimoAoActuatorHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    // valida que metricas sao bloqueadas para requisicoes nao autenticadas
    @Test
    @DisplayName("deve bloquear acesso anonimo ao endpoint /actuator/metrics")
    void deveBloquearAcessoAnonimoAoActuatorMetrics() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isUnauthorized());
    }

    // valida que usuario com perfil comum nao tem permissao para visualizar metricas
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("deve retornar 403 forbidden ao acessar /actuator/metrics com perfil comum")
    void deveRetornarForbiddenParaUsuarioComumAoAcessarMetrics() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden());
    }

    // valida que usuario com role admin tem acesso completo as metricas operacionais
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("deve permitir acesso ao endpoint /actuator/metrics com perfil admin")
    void devePermitirAcessoAoActuatorMetricsParaAdmin() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray());
    }
}
