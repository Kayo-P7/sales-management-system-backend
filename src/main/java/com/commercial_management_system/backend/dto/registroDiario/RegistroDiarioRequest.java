package com.commercial_management_system.backend.dto.registroDiario;

import jakarta.validation.constraints.Min;

public record RegistroDiarioRequest(

        @Min(0)
        Integer pendenciasNovas,

        @Min(0)
        Integer ligacoes,

        @Min(0)
        Integer negociacoes,

        @Min(0)
        Integer agendamentos,

        @Min(0)
        Integer chamadasVideo,

        @Min(0)
        Integer clientesEmLoja,

        @Min(0)
        Integer fechamentos,

        @Min(0)
        Integer anuncios

) {
}