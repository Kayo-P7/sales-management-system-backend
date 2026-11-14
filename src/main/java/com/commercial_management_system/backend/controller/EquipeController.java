package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.equipe.AddVendedorRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeRequest;
import com.commercial_management_system.backend.dto.equipe.EquipeResponse;
import com.commercial_management_system.backend.dto.equipe.EquipeResponsePage;
import com.commercial_management_system.backend.service.EquipeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/equipes")
public class EquipeController {

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<EquipeResponse> save(@Valid @RequestBody EquipeRequest equipeRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(equipeService.save(equipeRequest));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/vendedores/{id}")
    public ResponseEntity<EquipeResponse> findBySupervisorIdVendedoresId(@PathVariable("id") Long vendedorId) {
        return ResponseEntity
                .ok()
                .body(equipeService.findBySupervisorIdVendedoresId(vendedorId));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/supervisor")
    public ResponseEntity<EquipeResponse> findAllBySupervisorId() {
        return ResponseEntity.ok().body(equipeService.findAllBySupervisorId());
    }

    @PreAuthorize("hasRole('SUPERVISOR')")
    @DeleteMapping("/vendedores/{id}")
    public ResponseEntity<Void> deleteByVendedor(@PathVariable("id") Long id) {
        equipeService.deleteByVendedor(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vendedores")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<EquipeResponse> addVendedorAEquipe(@Valid @RequestBody AddVendedorRequest request) {
        return ResponseEntity.ok(equipeService.addVendedorAEquipe(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Page<EquipeResponsePage>> findAllEquipes(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(equipeService.findAllEquipes(pageable));
    }
}
