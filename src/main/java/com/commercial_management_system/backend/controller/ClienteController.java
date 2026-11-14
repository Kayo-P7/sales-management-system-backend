package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.cliente.ClienteRequest;
import com.commercial_management_system.backend.dto.cliente.ClienteResponse;
import com.commercial_management_system.backend.service.ClienteService;
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

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @PostMapping
    public ResponseEntity<ClienteResponse> save(@Valid @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(clienteRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClienteById(@PathVariable("id") Long id) {
        clienteService.deleteClienteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateCliente(
            @PathVariable("id") Long id,
            @Valid @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.ok(clienteService.updateCliente(id, clienteRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VENDEDOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateClienteParcial(
            @PathVariable("id") Long id,
            @Valid @RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.ok(clienteService.updateClienteParcial(id, clienteRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'SUPERVISOR')")
    @GetMapping(params = "telephone")
    public ResponseEntity<ClienteResponse> findByTelephone(@RequestParam String telephone) {
        return ResponseEntity.ok(clienteService.findByTelephone(telephone));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'SUPERVISOR')")
    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listarClientes(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(clienteService.findByName(name, pageable));
        }
        return ResponseEntity.ok(clienteService.listarClientes(pageable));
    }
}
