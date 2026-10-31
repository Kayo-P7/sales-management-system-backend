package com.commercial_management_system.backend.dto.registroDiario;

import java.time.LocalDate;

public record RegistroDiarioResponse(

        Long id,

        Long vendedorId,

        String nomeVendedor,

        LocalDate data,

        Integer pendenciasNovas,

        Integer ligacoes,

        Integer negociacoes,

        Integer agendamentos,

        Integer chamadasVideo,

        Integer clientesEmLoja,

        Integer fechamentos,

        Integer anuncios

) {
}