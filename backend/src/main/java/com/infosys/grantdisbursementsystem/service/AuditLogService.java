package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.AuditLog;
import com.infosys.grantdisbursementsystem.entity.User;
import com.infosys.grantdisbursementsystem.repository.AuditLogRepository;
import com.infosys.grantdisbursementsystem.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository,
                           UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void log(String actionType,
                    String entityType,
                    Long entityId,
                    String oldValue,
                    String newValue) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuditLog auditLog = new AuditLog();

        auditLog.setActionType(actionType);
        auditLog.setPerformedBy(user.getFullName());
        auditLog.setEntityAffected(entityType + ":" + entityId);
        auditLog.setDetails("Old Value: " + oldValue + ", New Value: " + newValue);
        auditLog.setTimestamp(java.time.LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}