package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.vehicle.VehicleRegister;
import com.commercial_management_system.backend.dto.vehicle.VehicleResponse;
import com.commercial_management_system.backend.service.VehicleService;
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

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> save(@Valid @RequestBody VehicleRegister vehicleRegister) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.save(vehicleRegister));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<VehicleResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @GetMapping
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> updateVehicleBrand(
            @PathVariable("id") Long id,
            @Valid @RequestBody VehicleRegister vehicleRegister) {
        return ResponseEntity.ok(vehicleService.updateVehicleBrand(id, vehicleRegister));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable("id") Long id,
            @Valid @RequestBody VehicleRegister vehicleRegister) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleRegister));
    }
}
