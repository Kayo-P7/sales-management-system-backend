package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EquipeRepository extends JpaRepository<Equipe,Long> {


    Optional<Equipe> findBySupervisor_IdAndVendedores_Id(Long supervisorId, Long vendedorId);


    Optional<Equipe> findAllBySupervisorId(Long supervisor);


    boolean existsBySupervisorId(Long id);
}
