package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.AuthenticationRequest;
import com.commercial_management_system.backend.dto.RegisterRequest;
import com.commercial_management_system.backend.dto.TokenResponse;
import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.model.User;
import com.commercial_management_system.backend.service.AuthService;
import com.commercial_management_system.backend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "autenticação", description = "endpoints para login e registro de usuários no sistema")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthService authService;

    // autentica credenciais e retorna token jwt
    @Operation(summary = "realizar login", description = "autentica o usuario atraves de email e senha retornando o token jwt de acesso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados de entrada invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "credenciais invalidas",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthenticationRequest authRequest) {
        // cria token de autenticacao com email e senha informados
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                authRequest.email(), authRequest.password());

        // valida credenciais atraves do authentication manager
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);

        // gera token jwt para o usuario autenticado
        String jwt = this.tokenService.gerarToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new TokenResponse(jwt));
    }

    // registra um novo usuario no sistema
    @Operation(summary = "registrar usuário", description = "cria um novo usuario no banco de dados e retorna status 201")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuario criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados de requisicao invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "usuario ja existente com email ou telefone informado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        this.authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
