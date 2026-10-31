package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioRequest;
import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioResponse;
import com.commercial_management_system.backend.service.RegistroDiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/registros-diarios")
@RequiredArgsConstructor
@Tag(
        name = "Registros Diários",
        description = "Gerenciamento e consulta dos registros diários de atividades dos vendedores."
)
public class RegistroDiarioController {

    private final RegistroDiarioService registroDiarioService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_VENDEDOR')")
    @Operation(
            summary = "Criar registro diário",
            description = """
                    Permite que um vendedor registre suas atividades diárias.
                    O vendedor autenticado e a data são definidos automaticamente pelo backend.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Registro diário criado com sucesso.",
                    content = @Content(
                            schema = @Schema(implementation = RegistroDiarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Apenas vendedores podem criar registros.",
                    content = @Content
            )
    })
    public ResponseEntity<RegistroDiarioResponse> criarRegistro(
            @Valid @RequestBody RegistroDiarioRequest request) {

        RegistroDiarioResponse response =
                registroDiarioService.criarRegistro(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('ROLE_VENDEDOR', 'ROLE_SUPERVISOR', 'ROLE_ADMIN')")
    @Operation(
            summary = "Consultar registros diários",
            description = """
                    Consulta os registros de acordo com o perfil do usuário autenticado:
                    - Vendedor: visualiza somente os próprios registros.
                    - Supervisor: visualiza somente os registros dos vendedores da sua equipe.
                    - Administrador: visualiza todos os registros.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registros encontrados.",
                    content = @Content(
                            schema = @Schema(implementation = RegistroDiarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão.",
                    content = @Content
            )
    })
    public ResponseEntity<List<RegistroDiarioResponse>> buscarRegistros() {

        return ResponseEntity.ok(
                registroDiarioService.buscarRegistros()
        );
    }

    @GetMapping("/meus/{data}")
    @PreAuthorize("hasRole('VENDEDOR')")
    @Operation(
            summary = "Consultar registro diário por data",
            description = "Busca o registro diário do vendedor autenticado para uma data específica."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro encontrado.",
                    content = @Content(
                            schema = @Schema(implementation = RegistroDiarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de data inválido.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro diário não encontrado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Apenas vendedores podem acessar este recurso.",
                    content = @Content
            )
    })
    public ResponseEntity<RegistroDiarioResponse> buscarMeuRegistroPorData(
            @Parameter(
                    description = "Data do registro no formato yyyy-MM-dd.",
                    example = "2026-10-31",
                    required = true
            )
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data) {

        return ResponseEntity.ok(
                registroDiarioService.buscarMeuRegistroPorData(data)
        );
    }
}