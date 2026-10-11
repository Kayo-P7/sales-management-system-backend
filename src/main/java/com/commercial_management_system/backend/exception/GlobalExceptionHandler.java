package com.commercial_management_system.backend.exception;

import com.commercial_management_system.backend.dto.erro.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // trata recurso nao encontrado no banco de dados
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErroResponse> handleNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // trata rotas inexistentes ou recursos estaticos nao encontrados
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.NOT_FOUND,
                "recurso nao encontrado: " + ex.getResourcePath(),
                request.getRequestURI()
        );
    }

    // trata erros de validacao de campos de requisicao
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return build(
                HttpStatus.BAD_REQUEST,
                mensagem,
                request.getRequestURI()
        );
    }

    // trata conflitos de integridade e estado do usuario
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            UserAlreadyActivedException.class,
            UserAlreadyDeactivedException.class
    })
    public ResponseEntity<ErroResponse> handleConflict(
            RuntimeException ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // captura credenciais incorretas e retorna 401 unauthorized
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "credenciais invalidas",
                request.getRequestURI()
        );
    }

    // captura refresh token invalido, expirado ou revogado e retorna 401 unauthorized
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErroResponse> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // captura falhas gerais de autenticacao e retorna 401 unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "falha na autenticacao: " + ex.getMessage(),
                request.getRequestURI()
        );
    }

    // captura tentativas de acesso sem permissao e retorna 403 forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.FORBIDDEN,
                "acesso negado para este recurso",
                request.getRequestURI()
        );
    }

    // captura erros de integridade do banco de dados
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.CONFLICT,
                "violacao de integridade dos dados",
                request.getRequestURI()
        );
    }

    // captura excecoes genericas nao mapeadas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "erro interno no servidor: " + ex.getMessage(),
                request.getRequestURI()
        );
    }

    private ResponseEntity<ErroResponse> build(
            HttpStatus status,
            String mensagem,
            String caminho) {
        ErroResponse body = new ErroResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                caminho
        );
        return ResponseEntity.status(status).body(body);
    }
}
