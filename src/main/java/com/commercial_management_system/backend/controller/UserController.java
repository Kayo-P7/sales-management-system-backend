package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.dto.user.UserRequest;
import com.commercial_management_system.backend.dto.user.UserResponse;
import com.commercial_management_system.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "usuários", description = "gerenciamento de contas e usuários do sistema")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // cadastra um novo usuario no sistema e retorna 201 created
    @Operation(summary = "cadastrar usuário", description = "cria um novo usuario no sistema (requer perfil ROLE_ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuario cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados de requisicao invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "usuario ja cadastrado no sistema",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> cadastrar(@Valid @RequestBody UserRequest userRequest) {
        UserResponse response = UserResponse.from(userService.salvar(userRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // lista usuarios de forma paginada com suporte a filtro opcional por nome
    @Operation(summary = "listar usuários", description = "retorna pagina de usuarios com filtro opcional por nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "pagina de usuarios retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<UserResponse>> listar(
            @Parameter(description = "filtro opcional por nome do usuario")
            @RequestParam(required = false) String name,
            @Parameter(description = "parametros de paginacao (page, size, sort)")
            Pageable pageable
    ) {
        Page<UserResponse> page = userService.listar(name, pageable);
        return ResponseEntity.ok(page);
    }

    // busca usuario especifico pelo identificador unico
    @Operation(summary = "buscar usuário por id", description = "retorna os detalhes de um usuario especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuario encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "usuario nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> buscarPorId(
            @Parameter(description = "identificador unico do usuario", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.buscarPorId(id));
    }

    // inativa o usuario no sistema sem excluir o registro fisico
    @Operation(summary = "desativar usuário", description = "altera o status do usuario para inativo (requer perfil ROLE_ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "usuario desativado com sucesso"),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "usuario nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "usuario ja se encontra desativado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desativar(
            @Parameter(description = "identificador do usuario a desativar", example = "1")
            @PathVariable Long id
    ) {
        userService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    // reativa o acesso do usuario no sistema
    @Operation(summary = "ativar usuário", description = "altera o status do usuario para ativo (requer perfil ROLE_ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "usuario ativado com sucesso"),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "usuario nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "usuario ja se encontra ativo",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> ativar(
            @Parameter(description = "identificador do usuario a ativar", example = "1")
            @PathVariable Long id
    ) {
        userService.ativar(id);
        return ResponseEntity.noContent().build();
    }
}