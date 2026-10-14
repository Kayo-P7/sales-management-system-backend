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
import org.apache.coyote.Response;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "vehicle", description = "gerenciamento de veiculos e marcas do sistema")
@RestController
@RequestMapping("/api/v1/vehicle")
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
    public ResponseEntity<VehicleResponse> save(@RequestBody VehicleRegister vehicleRegister) {

        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.save(vehicleRegister));
    }

    @GetMapping("/{id}")
    @Operation(summary = "buscar vehicle pelo id", description = "buscar vehicle pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Veiculo encontrado",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisição invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<VehicleResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(vehicleService.findById(id));
    }


    @GetMapping
    @Operation(summary = "buscar veiculos", description = "buscar vehicle pelo pelo name, color, price ou nome da marca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Veiculo encontrado",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "dados da requisição invalidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão para acessar esse endpoint",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<VehicleResponse>> findVehicle(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) String nameBrand,
            Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(vehicleService.findVehicle(
                        name, color, price, nameBrand, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleterVehicleById(@PathVariable("id") Long id) {

        vehicleService.deleteVehicleById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicleByName(@PathVariable("name") String name) {

        vehicleService.deleteVehicleByName(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atualização feita"),
            @ApiResponse(responseCode = "401", description = "falta de authenticação para a requisição "),
            @ApiResponse(responseCode = "403", description = "Você não tem permissão para executar essa requisição"),
            @ApiResponse(responseCode = "400", description = "dados invalidos")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> updateVehicleBrand(
            @PathVariable("id") Long id,
            @RequestBody VehicleRegister vehicleRegister) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.updateVehicleBrand(id, vehicleRegister));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veiculo atualizado",
            content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "302", description = "Veiculo encontrado"),
            @ApiResponse(responseCode = "400", description = "dados do veiculo invalido",
            content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "você não tem permissão de acesso a esse endpoint ",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable("id") Long id,
            @RequestBody VehicleRegister vehicleRegister) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleRegister));
    }
}
