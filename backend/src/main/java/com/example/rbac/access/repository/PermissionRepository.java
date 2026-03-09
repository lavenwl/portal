package com.example.rbac.access.repository;

import com.example.rbac.access.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<PermissionEntity> findByIdIn(Collection<Long> ids);

    Optional<PermissionEntity> findByCode(String code);
}
