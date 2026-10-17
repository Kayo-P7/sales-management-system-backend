package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    Optional<Cliente> findByIdAndCreatedBy(Long id, Long CreatedBy);
    Optional<Cliente> findByCreatedByAndTelephoneContaining(Long createdBy, String telephone);
    Page<Cliente> findAllByCreatedBy(Long createdBy, Pageable pageable);

    Page<Cliente> findByNameAndCreatedByContainingIgnoreCase
            ( Long createdBy, String name, Pageable pageable);


}
