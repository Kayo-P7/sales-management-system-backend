package com.commercial_management_system.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(
        name = "registro_diario",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_registro_vendedor_data",
                        columnNames = {"vendedor_id", "data"}
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private User vendedor;

    @Column(nullable = false)
    private LocalDate data;

    @Min(0)
    private Integer pendenciasNovas;

    @Min(0)
    private Integer ligacoes;

    @Min(0)
    private Integer negociacoes;

    @Min(0)
    private Integer agendamentos;

    @Min(0)
    private Integer chamadasVideo;

    @Min(0)
    private Integer clientesEmLoja;

    @Min(0)
    private Integer fechamentos;

    @Min(0)
    private Integer anuncios;

    @PrePersist
    public void prePersist() {
        if (data == null) {
            data = LocalDate.now();
        }
    }
}