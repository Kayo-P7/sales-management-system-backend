package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.enums.UserStatus;
import com.commercial_management_system.backend.enums.UserType;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRefreshIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String email = "usuario.refresh@teste.com";
    private final String senha = "senhaSegura123";

    @BeforeEach
    void setUp() {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(new User(
                    "Usuario Refresh",
                    email,
                    "11977776666",
                    passwordEncoder.encode(senha),
                    UserType.ROLE_USER,
                    UserStatus.ACTIVE
            ));
        }
    }

    // valida fluxo completo de login, renovacao com rotacao e logout
    @Test
    @DisplayName("deve executar ciclo completo de login, renovacao com rotacao e logout")
    void deveExecutarCicloCompletoDeRefreshELogout() throws Exception {
        // 1. realiza login
        String loginPayload = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, senha);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String initialToken = loginJson.get("token").asText();
        String initialRefreshToken = loginJson.get("refreshToken").asText();

        assertNotNull(initialToken);
        assertNotNull(initialRefreshToken);

        // 2. renova token com refresh token obtido
        String refreshPayload = """
                {
                    "refreshToken": "%s"
                }
                """.formatted(initialRefreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        JsonNode refreshJson = objectMapper.readTree(refreshResult.getResponse().getContentAsString());
        String newRefreshToken = refreshJson.get("refreshToken").asText();

        // valida que o refresh token foi rotacionado
        assertNotEquals(initialRefreshToken, newRefreshToken);

        // 3. tenta reusar o refresh token antigo (deve falhar com 401)
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshPayload))
                .andExpect(status().isUnauthorized());

        // 4. realiza logout com o novo refresh token
        String logoutPayload = """
                {
                    "refreshToken": "%s"
                }
                """.formatted(newRefreshToken);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logoutPayload))
                .andExpect(status().isNoContent());

        // 5. tenta renovar com o token revogado pelo logout (deve falhar com 401)
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logoutPayload))
                .andExpect(status().isUnauthorized());
    }
}
