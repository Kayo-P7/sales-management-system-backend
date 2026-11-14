package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioRequest;
import com.commercial_management_system.backend.dto.registroDiario.RegistroDiarioResponse;
import com.commercial_management_system.backend.service.RegistroDiarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/registros-diarios")
@RequiredArgsConstructor
public class RegistroDiarioController {

    private final RegistroDiarioService registroDiarioService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_VENDEDOR')")
    public ResponseEntity<RegistroDiarioResponse> criarRegistro(
            @Valid @RequestBody RegistroDiarioRequest request) {

        RegistroDiarioResponse response = registroDiarioService.criarRegistro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('ROLE_VENDEDOR', 'ROLE_SUPERVISOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<RegistroDiarioResponse>> buscarRegistros() {
        return ResponseEntity.ok(registroDiarioService.buscarRegistros());
    }

    @GetMapping("/meus/{data}")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<RegistroDiarioResponse> buscarMeuRegistroPorData(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data) {

        return ResponseEntity.ok(registroDiarioService.buscarMeuRegistroPorData(data));
    }

    @DeleteMapping("/{idTarefa}")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<Void> deleteRegistroDiario(@PathVariable Long idTarefa) {
        registroDiarioService.deleteRegistroDiario(idTarefa);
        return ResponseEntity.noContent().build();
    }
}
