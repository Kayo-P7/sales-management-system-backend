package com.commercial_management_system.backend.config;

import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return true;
        }
        // nao aplica rate limit em rotas de documentacao e verificacao de integridade
        return !uri.startsWith("/api/")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || "/health".equals(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // ignora validacao caso a funcionalidade esteja desabilitada
        if (!rateLimitService.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        ConsumptionProbe probe = rateLimitService.tryConsume(request);

        if (probe.isConsumed()) {
            // adiciona cabecalho informativo com a quantidade de tokens restantes
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
            return;
        }

        // calcula o tempo de espera em segundos ate o proximo ciclo de tokens
        long segundosEspera = Math.max(1, (probe.getNanosToWaitForRefill() / 1_000_000_000L) + 1);

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(segundosEspera));
        response.setHeader("X-Rate-Limit-Remaining", "0");
        response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(segundosEspera));

        ErroResponse erroResponse = new ErroResponse(
                Instant.now(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too Many Requests",
                "limite de requisicoes excedido. tente novamente em " + segundosEspera + " segundos",
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getWriter(), erroResponse);
    }
}
