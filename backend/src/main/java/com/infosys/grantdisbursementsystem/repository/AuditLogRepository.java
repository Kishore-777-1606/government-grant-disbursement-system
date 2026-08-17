package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityType(String entityType);

    List<AuditLog> findByEntityId(Long entityId);

    List<AuditLog> findByEntityTypeAndEntityId(
            String entityType,
            Long entityId
    );
}