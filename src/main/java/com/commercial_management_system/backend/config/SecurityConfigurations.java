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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
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

        // Identifica especificamente a requisicao de cadastro de usuario.
        RequestMatcher cadastroPublico = request ->
                HttpMethod.POST.matches(request.getMethod())
                        && "/api/v1/users".equals(request.getServletPath());
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
                        .requestMatchers(cadastroPublico).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/**", "/api/v1/vehicles", "/api/v1/vehicle/**", "/api/v1/vehicle").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // restringe endpoints operacionais e metricas do actuator para administradores
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // exige autenticacao para qualquer outro endpoint
                        .anyRequest().authenticated()
                )
                // trata falhas de autenticacao com 401 e autorizacao com 403
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "nao autorizado"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "acesso negado"))
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
