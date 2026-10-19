package com.commercial_management_system.backend.dto.cliente;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "dados para criacao e atualizacao de cliente")
public record ClienteRequest(
        @Schema(description = "nome completo do cliente", example = "Maria Silva")
        @NotBlank(message = "o nome e obrigatorio")
        @Size(max = 255, message = "o nome deve ter no maximo 255 caracteres")
        String name,

        @Schema(description = "telefone de contato", example = "11999998888")
        @NotBlank(message = "o telefone e obrigatorio")
        @Size(max = 30, message = "o telefone deve ter no maximo 30 caracteres")
        String telephone,

        @Schema(description = "e-mail do cliente", example = "maria@example.com")
        @Email(message = "formato de e-mail invalido")
        String email,

        @Schema(description = "observacoes sobre o cliente", example = "cliente interessado em suv")
        String notes
) {
}
