package com.commercial_management_system.backend.controller;

import com.commercial_management_system.backend.dto.user.UserRequest;
import com.commercial_management_system.backend.dto.user.UserResponse;
import com.commercial_management_system.backend.dto.user.UserUpdateRequest;
import com.commercial_management_system.backend.service.UserService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // cadastra novo usuario no sistema e retorna 201 created
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> cadastrar(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // atualiza dados cadastrais de usuario existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse response = userService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    // lista usuarios de forma paginada com suporte a filtro opcional por nome
    @GetMapping
    public ResponseEntity<Page<UserResponse>> listar(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        Page<UserResponse> page = userService.listar(name, pageable);
        return ResponseEntity.ok(page);
    }

    // busca usuario especifico pelo identificador unico
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.buscarPorId(id));
    }

    // inativa o usuario no sistema sem excluir o registro fisico
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        userService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    // reativa o acesso do usuario no sistema
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        userService.ativar(id);
        return ResponseEntity.noContent().build();
    }
}
