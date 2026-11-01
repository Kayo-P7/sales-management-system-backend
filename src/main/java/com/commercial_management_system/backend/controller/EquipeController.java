package com.commercial_management_system.backend.controller;


import com.commercial_management_system.backend.dto.equipe.AddVendedorRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.dto.equipe.EquipeResponsePage;
import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.service.EquipeService;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "equipe", description = "cadastrar equipe")
@RestController
@RequestMapping("/api/v1/equipes")

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
    public ResponseEntity<EquipeResponse> save(@Valid @RequestBody EquipeRequest equipeRequest) {

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
    @GetMapping("/supervisor")
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


    @Operation(
            summary = "Adicionar vendedor à equipe",
            description = "Adiciona um vendedor à equipe do supervisor autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vendedor adicionado à equipe com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "O usuário informado não é vendedor ou já pertence a uma equipe"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Apenas supervisores podem adicionar vendedores"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vendedor ou equipe não encontrado"
            )
    })
    @PostMapping("/vendedores")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<EquipeResponse> addVendedorAEquipe(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do vendedor que será adicionado à equipe",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AddVendedorRequest.class)
                    )
            )
            @RequestBody
            @Valid AddVendedorRequest request
    ) {
        return ResponseEntity.ok(
                equipeService.addVendedorAEquipe(request)
        );
    }



    @Operation(
            summary = "Listar equipes",
            description = "Retorna uma lista paginada de equipes cadastradas no sistema. "
                    + "Apenas usuários com perfil ADMIN ou SUPERVISOR podem acessar."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Equipes listadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário não possui permissão para acessar este recurso"
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<EquipeResponsePage>> findAllEquipes(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(equipeService.findAllEquipes(pageable));
    }
}
