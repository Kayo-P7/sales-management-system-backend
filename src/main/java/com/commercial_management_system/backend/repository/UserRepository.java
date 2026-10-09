package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // busca usuario por email
    Optional<User> findByEmail(String email);

    // busca usuarios por nome com suporte a paginacao
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // busca usuario por telefone
    Optional<User> findByTelephone(String telephone);

    // verifica existencia por email
    boolean existsByEmail(String email);

    // verifica existencia por telefone
    boolean existsByTelephone(String telephone);
}
