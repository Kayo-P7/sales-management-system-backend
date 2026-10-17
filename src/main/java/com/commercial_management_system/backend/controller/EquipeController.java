package com.commercial_management_system.backend.controller;


import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.service.EquipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "equipe", description = "cadastrar equipe")
@RestController
@RequestMapping("/api/v1/equipe")

public class EquipeController {

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }
    @Operation(
            summary = "cadastrar equipe",
            description = "cadastrar uma equipe e associar vendedores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "equipe cadastrada com sucesso",
                    content = @Content(schema = @Schema(implementation = EquipeResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisição inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<EquipeResponse> save(@RequestBody EquipeRequest equipeRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(equipeService.save(equipeRequest));
    }

    @Operation(
            summary = "buscar equipe pelo vendedor",
            description = "buscar a equipe à qual o vendedor informado pertence"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "equipe encontrada",
                    content = @Content(schema = @Schema(implementation = EquipeResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisição inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "equipe não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/vendedores/{id}")
    public ResponseEntity<EquipeResponse> findBySupervisorIdVendedoresId(@PathVariable("id") Long vendedorId) {

        return ResponseEntity
                .ok()
                .body(equipeService.findBySupervisorIdVendedoresId(vendedorId));
    }


    @Operation(
            summary = "buscar equipe do supervisor",
            description = "buscar a equipe associada ao supervisor autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "equipe encontrada",
                    content = @Content(schema = @Schema(implementation = EquipeResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "equipe do supervisor não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<EquipeResponse> findAllBySupervisorId() {
        return ResponseEntity.ok().body(equipeService.findAllBySupervisorId());
    }


    @Operation(
            summary = "remover vendedor da equipe",
            description = "remover o vendedor da equipe do supervisor autenticado sem excluir sua conta"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "vendedor removido da equipe com sucesso"),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "vendedor não encontrado ou não pertence à equipe",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasRole('SUPERVISOR')")
    @DeleteMapping("/vendedores/{id}")
    public ResponseEntity<Void> deleteByVendedor(@PathVariable("id") Long id) {

        equipeService.deleteByVendedor(id);
        return ResponseEntity.noContent().build();
    }
}
