package com.example.rbac.log.repository;

import com.example.rbac.log.entity.OperationLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationLogRepository extends JpaRepository<OperationLogEntity, Long> {

    Page<OperationLogEntity> findByModuleContainingIgnoreCase(String module, Pageable pageable);
}
