package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.brand.BrandRequest;
import com.commercial_management_system.backend.dto.brand.BrandResponse;
import com.commercial_management_system.backend.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    // cadastra uma nova marca de veiculo
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> cadastrar(@Valid @RequestBody BrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.salvar(request));
    }

    // lista todas as marcas
    @GetMapping
    public ResponseEntity<List<BrandResponse>> listarTodos() {
        return ResponseEntity.ok(brandService.listarTodos());
    }

    // busca marca por id
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.buscarPorId(id));
    }
}
