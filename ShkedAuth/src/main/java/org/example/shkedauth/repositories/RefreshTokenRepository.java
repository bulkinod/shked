package org.example.shkedauth.repositories;

import org.example.shkedauth.entities.RefreshTokenEntity;
import org.example.shkedauth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByToken(String token);
    void deleteAllByUserId(String id);
}