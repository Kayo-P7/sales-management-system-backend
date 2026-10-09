package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.brand.BrandRequest;
import com.commercial_management_system.backend.dto.brand.BrandResponse;
import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.service.BrandService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "marcas", description = "gerenciamento de marcas de veiculos")
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BrandController {

    private final BrandService brandService;

    // cadastra uma nova marca de veiculo
    @Operation(summary = "cadastrar marca", description = "cadastra uma nova marca no catalogo (requer permissao de administrador)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "marca cadastrada com sucesso",
                    content = @Content(schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados de requisicao invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> cadastrar(@Valid @RequestBody BrandRequest request) {
        BrandResponse response = BrandResponse.from(this.brandService.salvar(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // lista todas as marcas
    @Operation(summary = "listar marcas", description = "retorna a lista de todas as marcas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "lista de marcas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<BrandResponse>> listarTodos() {
        return ResponseEntity.ok(this.brandService.listarTodos());
    }

    // busca marca por id
    @Operation(summary = "buscar marca por id", description = "retorna os detalhes de uma marca especifica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "marca encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "marca nao encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> buscarPorId(
            @Parameter(description = "identificador unico da marca", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(this.brandService.buscarPorId(id));
    }
}
