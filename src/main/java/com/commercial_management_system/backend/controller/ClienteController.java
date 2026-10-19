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

@Tag(name="cliente", description = "cadastrar cliente")
@RestController
@RequestMapping("/api/v1/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }


    @Operation(summary = "cadastrar cliente", description = "cadastra um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "cliente cadastrada com sucesso",
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
    public ResponseEntity<ClienteResponse> save(@RequestBody ClienteRequest clienteRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(clienteRequest));
    }

    @Operation(summary = "buscar cliente por id", description = "retorna os detalhes de uma cliente especifica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cliente encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation =    ClienteResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "cliente nao encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable("id") Long id) {

        return ResponseEntity.status(HttpStatus.FOUND).body(clienteService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClienteById(@PathVariable("id") Long id) {

        clienteService.deleteClienteById(id);
        return ResponseEntity.noContent().build();
    }



    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "cliente atualizado",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "302", description = "cliente encontrado"),
            @ApiResponse(responseCode = "400", description = "dados do veiculo invalido",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão de acesso a esse endpoint ",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateCliente(@PathVariable("id") Long id, @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.updateCliente(id, clienteRequest));

    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atualização feita"),
            @ApiResponse(responseCode = "401", description = "falta de authenticação para a requisição "),
            @ApiResponse(responseCode = "403", description = "Você não tem permissão para executar essa requisição"),
            @ApiResponse(responseCode = "400", description = "dados invalidos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateClienteParcial(@PathVariable("id") Long id, @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.updateClienteParcial(id, clienteRequest));

    }

    @Operation(summary = "buscar cliente", description = "faz a busca do cliente pelo nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Veiculo encontrado",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisição invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'SUPERVISOR')")
    @GetMapping("/buscar/name")
    public ResponseEntity<Page<ClienteResponse>> findByName(@RequestParam String name, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.FOUND).body(clienteService.findByName(name, pageable));
    }


    @Operation(summary = "buscar telephone", description = "buscar número de telephone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "telephone encontrado",
                    content = @Content(schema = @Schema(implementation = ClienteResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisição invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'SUPERVISOR')")
    @GetMapping("/buscar/telephone")
    public ResponseEntity<ClienteResponse> findByTelephone(@RequestParam String telephone) {
        return ResponseEntity.status(HttpStatus.FOUND).body(clienteService.findByTelephone(telephone));
    }



    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'SUPERVISOR')")
    public ResponseEntity<Page<ClienteResponse>> listarClientes(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(clienteService.listarClientes(pageable));
    }

}
