package com.example.rbac.log.repository;

import com.example.rbac.log.entity.LoginLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLogEntity, Long> {

    Page<LoginLogEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
