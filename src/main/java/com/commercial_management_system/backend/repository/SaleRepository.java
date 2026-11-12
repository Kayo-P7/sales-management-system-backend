package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.enums.SaleStatus;
import com.commercial_management_system.backend.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // verifica se o veiculo ja possui uma venda ativa
    boolean existsByVehicleIdAndStatus(Long vehicleId, SaleStatus status);

    // consultas paginadas para administradores
    Page<Sale> findByStatus(SaleStatus status, Pageable pageable);

    // consultas paginadas para vendedores
    Page<Sale> findBySellerId(Long sellerId, Pageable pageable);

    Page<Sale> findBySellerIdAndStatus(Long sellerId, SaleStatus status, Pageable pageable);

    // consultas paginadas para supervisores acompanharem as vendas da propria equipe
    Page<Sale> findBySellerEquipeSupervisorId(Long supervisorId, Pageable pageable);

    Page<Sale> findBySellerEquipeSupervisorIdAndStatus(
            Long supervisorId,
            SaleStatus status,
            Pageable pageable
    );
}
