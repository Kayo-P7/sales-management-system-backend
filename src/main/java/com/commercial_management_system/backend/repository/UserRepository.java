package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByNameContainingIgnoreCase(String name);
    Optional<User> findByTelephone(String telephone);
}
