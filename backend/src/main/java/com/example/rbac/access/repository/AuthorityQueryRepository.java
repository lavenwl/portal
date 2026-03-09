package com.example.rbac.access.repository;

import com.example.rbac.access.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorityQueryRepository extends JpaRepository<PermissionEntity, Long> {

    @Query(value = "select distinct p.code from user_roles ur " +
            "join roles r on ur.role_id = r.id and r.status = 1 " +
            "join role_permissions rp on rp.role_id = r.id " +
            "join permissions p on p.id = rp.permission_id " +
            "where ur.user_id = :userId", nativeQuery = true)
    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);
}
