package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.request.UserRequest;
import com.commercial_management_system.backend.dto.response.UserResponse;
import com.commercial_management_system.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "usuários", description = "gerenciamento de contas e usuários do sistema")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService USER_SERVICE;

    @Operation(summary = "cadastrar usuário", description = "cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados de requisição inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse cadastrar(@Valid @RequestBody UserRequest userRequest) {
        return UserResponse.from(USER_SERVICE.cadastrar(userRequest));
    }

    @Operation(summary = "listar todos os usuários", description = "retorna uma lista com todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "lista retornada com sucesso")
    @GetMapping
    public List<UserResponse> listarTodos() {
        return USER_SERVICE.listarTodos().stream().map(UserResponse::from).toList();
    }

    @Operation(summary = "buscar usuário por nome", description = "Retorna os detalhes de um usuário buscando pelo seu nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/nome/{name}") // Alterado para evitar conflito com a rota /{id}
    public UserResponse buscarPorNome(@PathVariable String name) {
        return UserResponse.from(USER_SERVICE.buscarPorNome(name));
    }

    @Operation(summary = "buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "usuário não encontrado")
    })
    @GetMapping("/{id}")
    public UserResponse buscarPorId(@PathVariable Long id) {
        return UserResponse.from(USER_SERVICE.buscarPorId(id));
    }
}