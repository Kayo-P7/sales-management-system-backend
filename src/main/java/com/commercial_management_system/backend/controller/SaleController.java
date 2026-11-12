package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.dto.sale.SaleCancelRequest;
import com.commercial_management_system.backend.dto.sale.SaleRequest;
import com.commercial_management_system.backend.dto.sale.SaleResponse;
import com.commercial_management_system.backend.enums.SaleStatus;
import com.commercial_management_system.backend.service.SaleService;
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

@Tag(name = "vendas", description = "registro, consulta e cancelamento de vendas de veiculos")
@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleService saleService;

    // registra uma nova venda de veiculo
    @Operation(summary = "registrar venda", description = "registra a comercializacao de um veiculo para um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "venda registrada com sucesso",
                    content = @Content(schema = @Schema(implementation = SaleResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisicao invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "veiculo ou cliente nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<SaleResponse> registrar(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.save(request));
    }

    // lista vendas de forma paginada com filtro opcional por status
    @Operation(summary = "listar vendas", description = "retorna o historico de vendas conforme o perfil autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "vendas listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<Page<SaleResponse>> listar(
            @Parameter(description = "filtro opcional por status da venda")
            @RequestParam(required = false) SaleStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(saleService.listar(status, pageable));
    }

    // busca os detalhes de uma venda especifica
    @Operation(summary = "buscar venda por id", description = "retorna os detalhes de uma venda especifica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "venda encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = SaleResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "venda nao encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<SaleResponse> buscarPorId(
            @Parameter(description = "identificador unico da venda", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(saleService.findById(id));
    }

    // cancela uma venda ativa
    @Operation(summary = "cancelar venda", description = "cancela uma venda registrada e armazena o motivo informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "venda cancelada com sucesso"),
            @ApiResponse(responseCode = "400", description = "motivo invalido ou venda ja cancelada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "venda nao encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<Void> cancelar(
            @Parameter(description = "identificador unico da venda", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SaleCancelRequest request
    ) {
        saleService.cancelar(id, request.reason());
        return ResponseEntity.noContent().build();
    }
}
