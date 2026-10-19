package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "clientes", description = "gerenciamento e cadastro de clientes")
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "cadastrar cliente", description = "cadastra um novo cliente vinculado ao usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "cliente cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados de requisicao invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @PostMapping
    public ResponseEntity<ClienteResponse> save(@Valid @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(clienteRequest));
    }

    @Operation(summary = "buscar cliente por id", description = "retorna os detalhes de um cliente especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cliente encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "cliente nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @Operation(summary = "excluir cliente por id", description = "remove um cliente do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "cliente excluido com sucesso"),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "cliente nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClienteById(@PathVariable("id") Long id) {
        clienteService.deleteClienteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "atualizar cliente", description = "atualiza os dados de um cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cliente atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados do cliente invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "cliente nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateCliente(
            @PathVariable("id") Long id,
            @Valid @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.ok(clienteService.updateCliente(id, clienteRequest));
    }

    @Operation(summary = "atualizar cliente parcialmente", description = "atualiza campos especificos de um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "atualizacao parcial realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "cliente nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateClienteParcial(
            @PathVariable("id") Long id,
            @Valid @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.ok(clienteService.updateClienteParcial(id, clienteRequest));
    }

    @Operation(summary = "buscar cliente por telefone", description = "retorna cliente associado ao telefone informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cliente encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisicao invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "cliente nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'SUPERVISOR')")
    @GetMapping(params = "telephone")
    public ResponseEntity<ClienteResponse> findByTelephone(@RequestParam String telephone) {
        return ResponseEntity.ok(clienteService.findByTelephone(telephone));
    }

    @Operation(summary = "listar clientes", description = "retorna pagina de clientes vinculados ao usuario autenticado com filtro opcional por nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "clientes listados com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'SUPERVISOR')")
    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listarClientes(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(clienteService.findByName(name, pageable));
        }
        return ResponseEntity.ok(clienteService.listarClientes(pageable));
    }
}
