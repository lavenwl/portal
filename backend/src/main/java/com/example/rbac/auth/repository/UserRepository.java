package com.example.rbac.auth.repository;

import com.example.rbac.auth.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findByDeletedAndUsernameContainingIgnoreCaseOrDeletedAndEmailContainingIgnoreCase(
            Integer deletedA,
            String username,
            Integer deletedB,
            String email,
            Pageable pageable
    );

    Page<UserEntity> findByDeleted(Integer deleted, Pageable pageable);
}
