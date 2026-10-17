package com.commercial_management_system.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipe_tb")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Equipe {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "equipe")
    private List<User> vendedores = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "supervisor_id", nullable = false, unique = true)
    private User supervisor;
}
