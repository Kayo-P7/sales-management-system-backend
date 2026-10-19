package com.commercial_management_system.backend.dto.equipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record EquipeRequest(
        @NotBlank(message = "o nome da equipe e obrigatorio")
        @Size(min = 2, max = 100, message = "o nome da equipe deve ter entre 2 e 100 caracteres")
        String nome,

        @NotNull(message = "a lista de vendedores e obrigatoria")
        @NotEmpty(message = "a lista de vendedores nao pode ser vazia")
        List<Long> vendedores
) {
}
