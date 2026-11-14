package com.commercial_management_system.backend.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "o nome e obrigatorio")
        @Size(max = 255, message = "o nome deve ter no maximo 255 caracteres")
        String name,

        @NotBlank(message = "o telefone e obrigatorio")
        @Size(max = 30, message = "o telefone deve ter no maximo 30 caracteres")
        String telephone,

        @Email(message = "formato de e-mail invalido")
        String email,

        String notes
) {
}
