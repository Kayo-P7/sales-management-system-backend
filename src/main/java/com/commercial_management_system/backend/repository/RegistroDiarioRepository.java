package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.RegistroDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface RegistroDiarioRepository
        extends JpaRepository<RegistroDiario, Long> {

    List<RegistroDiario> findAllByVendedorId(Long vendedorId);

    Optional<RegistroDiario> findByVendedorIdAndData(
            Long vendedorId,
            LocalDate data
    );

    @Query("""
            SELECT r
            FROM RegistroDiario r
            JOIN r.vendedor v
            JOIN v.equipe e
            WHERE e.supervisor.id = :supervisorId
            """)
    List<RegistroDiario> findAllBySupervisorId(
            @Param("supervisorId") Long supervisorId
    );

   Optional<RegistroDiario> findByVendedor_IdAndId(Long idVendedor, Long idTarefa);
}