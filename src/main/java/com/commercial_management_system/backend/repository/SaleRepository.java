package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.enums.SaleStatus;
import com.commercial_management_system.backend.model.Sale;
import com.commercial_management_system.backend.projection.SalesEvolutionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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


    @Query("""
    SELECT
        FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM') AS period,
        SUM(s.salePrice) AS totalRevenue,
        COUNT(s) AS totalSales
    FROM Sale s
    WHERE s.status = :status
      AND s.saleDate >= :start
      AND s.saleDate < :end
    GROUP BY FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM')
    ORDER BY FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM')
    """)
    List<SalesEvolutionProjection> findEvolutionForAdmin(
            @Param("status") SaleStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT
        FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM') AS period,
        SUM(s.salePrice) AS totalRevenue,
        COUNT(s) AS totalSales
    FROM Sale s
    WHERE s.status = :status
      AND s.saleDate >= :start
      AND s.saleDate < :end
      AND s.seller.id = :sellerId
    GROUP BY FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM')
    ORDER BY FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM')
    """)
    List<SalesEvolutionProjection> findEvolutionForSeller(
            @Param("status") SaleStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("sellerId") Long sellerId
    );

    @Query("""
    SELECT
        FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM') AS period,
        SUM(s.salePrice) AS totalRevenue,
        COUNT(s) AS totalSales
    FROM Sale s
    WHERE s.status = :status
      AND s.saleDate >= :start
      AND s.saleDate < :end
      AND s.seller.equipe.supervisor.id = :supervisorId
    GROUP BY FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM')
    ORDER BY FUNCTION('TO_CHAR', s.saleDate, 'YYYY-MM')
    """)
    List<SalesEvolutionProjection> findEvolutionForSupervisor(
            @Param("status") SaleStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("supervisorId") Long supervisorId
    );
}
