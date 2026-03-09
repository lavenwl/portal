package com.example.rbac.auth.repository;

import com.example.rbac.auth.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenAndRevoked(String token, Integer revoked);

    int deleteByUserId(Long userId);

    long countByUserIdAndRevokedAndExpiresAtAfter(Long userId, Integer revoked, LocalDateTime expiresAt);
}
