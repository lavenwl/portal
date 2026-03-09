package com.example.rbac.access.repository;

import com.example.rbac.access.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

    List<UserRoleEntity> findByUserId(Long userId);

    int deleteByUserId(Long userId);

    Optional<UserRoleEntity> findByUserIdAndRoleId(Long userId, Long roleId);
}
