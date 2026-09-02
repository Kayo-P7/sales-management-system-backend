package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.request.AuthenticationRequest;
import com.commercial_management_system.backend.dto.request.RegisterRequest;
import com.commercial_management_system.backend.dto.response.AuthenticationResponse;
import com.commercial_management_system.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "endpoints pra gerenciamento de login e registro de usuários")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthService AUTH_SERVICE;

    @Operation(summary = "realizar login", description = "autentica um usuário no sistema e retorna o token JWT de acesso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Credenciais inválidas ou conta inativa")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        AuthenticationResponse response = AUTH_SERVICE.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "registrar usuário", description = "cria um novo usuário e o salva no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados de requisição inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        boolean success = AUTH_SERVICE.register(request);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
