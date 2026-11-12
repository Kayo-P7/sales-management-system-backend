package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByName(String name);

    // soma o valor de todos os veiculos em estoque para o painel principal
    @Query("select sum(v.price) from Vehicle v")
    BigDecimal sumInventoryValue();
}
