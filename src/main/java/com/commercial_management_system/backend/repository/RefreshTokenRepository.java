package com.commercial_management_system.backend.repository;

import com.commercial_management_system.backend.model.RefreshToken;
import com.commercial_management_system.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // busca refresh token pelo valor textual
    Optional<RefreshToken> findByToken(String token);

    // remove todos os tokens vinculados a um usuario
    void deleteByUser(User user);
}
