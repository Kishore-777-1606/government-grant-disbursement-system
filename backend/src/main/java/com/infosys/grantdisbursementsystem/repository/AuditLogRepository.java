package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}