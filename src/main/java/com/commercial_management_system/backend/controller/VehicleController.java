package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.erro.ErroResponse;
import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.service.VehicleService;
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

import java.math.BigDecimal;

@Tag(name = "vehicle", description = "gerenciamento de veiculos e marcas do sistema")
@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "cadastrar veiculo", description = "cria um novo veiculo no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veiculo cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisição invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "não autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    }
    )
    public ResponseEntity<VehicleResponse> save(@Valid @RequestBody VehicleRegister vehicleRegister) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.save(vehicleRegister));
    }

    @GetMapping("/{id}")
    @Operation(summary = "buscar veiculo pelo id", description = "retorna os dados de um veiculo especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "veiculo encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisicao invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "veiculo nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<VehicleResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @GetMapping
    @Operation(summary = "buscar veiculos", description = "lista veiculos com filtros opcionais por nome, cor, preco ou marca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "veiculos listados com sucesso",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "parametros de busca invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'SUPERVISOR')")
    public ResponseEntity<Page<VehicleResponse>> findVehicle(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) String nameBrand,
            Pageable pageable
    ) {
        return ResponseEntity.ok(vehicleService.findVehicle(name, color, price, nameBrand, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicleById(@PathVariable("id") Long id) {
        vehicleService.deleteVehicleById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "marca do veiculo atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "nao autenticado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "veiculo nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> updateVehicleBrand(
            @PathVariable("id") Long id,
            @Valid @RequestBody VehicleRegister vehicleRegister) {
        return ResponseEntity.ok(vehicleService.updateVehicleBrand(id, vehicleRegister));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "veiculo atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados do veiculo invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "veiculo nao encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable("id") Long id,
            @Valid @RequestBody VehicleRegister vehicleRegister) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleRegister));
    }
}
