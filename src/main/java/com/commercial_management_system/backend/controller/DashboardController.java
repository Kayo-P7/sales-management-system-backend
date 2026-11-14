
package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.dashboard.DashboardResponse;
import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(
        name = "Dashboard",
        description = "Indicadores consolidados do painel principal"
)
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Consultar métricas do dashboard",
            description = "Retorna indicadores de estoque, clientes, desempenho comercial e evolução mensal de vendas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Métricas retornadas com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = DashboardResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            schema = @Schema(implementation = ErroResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado",
                    content = @Content(
                            schema = @Schema(implementation = ErroResponse.class)
                    )
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<DashboardResponse> obterMetricas() {
        return ResponseEntity.ok(dashboardService.obterMetricas());
    }
}
