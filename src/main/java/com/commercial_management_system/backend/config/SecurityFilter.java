package com.commercial_management_system.backend.config;

import com.commercial_management_system.backend.repository.UserRepository;
import com.commercial_management_system.backend.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Authorization presente: "
                + (request.getHeader("Authorization") != null));
        // extrai o token jwt do cabecalho authorization
        String tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            // valida a assinatura e extrai o subject (email)
            String login = tokenService.getSubject(tokenJWT);
            if (login != null) {
                // carrega o usuario e suas permissoes do banco
                UserDetails usuario = userRepository.findByEmail(login).orElse(null);
                if (usuario != null && usuario.isEnabled()) {
                    // injeta a autenticacao no contexto de seguranca da requisicao atual
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        // continua a execucao da cadeia de filtros
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        // recupera o token bearer removendo o prefixo
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }
}
