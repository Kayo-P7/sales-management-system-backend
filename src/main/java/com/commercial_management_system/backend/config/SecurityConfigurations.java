package com.commercial_management_system.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigurations {

    private final SecurityFilter securityFilter;
    private final RateLimitFilter rateLimitFilter;
    private final CorrelationIdFilter correlationIdFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // desabilita csrf pois a autenticacao e baseada em token stateless
                .csrf(AbstractHttpConfigurer::disable)
                // habilita cors integrado as rotas da api
                .cors(Customizer.withDefaults())
                // define gerenciamento de sessao sem estado na aplicacao
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // define regras de autorizacao por rota
                .authorizeHttpRequests(req -> req
                        // libera login e rotas publicas
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // exige autenticacao para qualquer outro endpoint
                        .anyRequest().authenticated()
                )
                // propaga o identificador de correlacao no mdc e na resposta antes dos demais filtros
                .addFilterBefore(correlationIdFilter, CorsFilter.class)
                // aplica limitacao de taxa logo apos cors e antes da autenticacao
                .addFilterAfter(rateLimitFilter, CorsFilter.class)
                // intercepta as requisicoes com o filtro de token jwt antes do filtro padrao
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // exporta o authentication manager para processar login no controller
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // utiliza bcrypt para hash seguro de senhas
        return new BCryptPasswordEncoder();
    }
}
