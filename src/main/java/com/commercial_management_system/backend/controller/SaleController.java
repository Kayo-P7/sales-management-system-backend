package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.sale.SaleCancelRequest;
import com.commercial_management_system.backend.dto.sale.SaleRequest;
import com.commercial_management_system.backend.dto.sale.SaleResponse;
import com.commercial_management_system.backend.enums.SaleStatus;
import com.commercial_management_system.backend.service.SaleService;
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

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    // registra uma nova venda de veiculo
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<SaleResponse> registrar(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.save(request));
    }

    // lista vendas de forma paginada com filtro opcional por status
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<Page<SaleResponse>> listar(
            @RequestParam(required = false) SaleStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(saleService.listar(status, pageable));
    }

    // busca os detalhes de uma venda especifica
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<SaleResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findById(id));
    }

    // cancela uma venda ativa
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @Valid @RequestBody SaleCancelRequest request
    ) {
        saleService.cancelar(id, request.reason());
        return ResponseEntity.noContent().build();
    }
}
