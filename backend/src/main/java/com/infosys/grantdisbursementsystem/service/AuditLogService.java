package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.AuditLog;
import com.infosys.grantdisbursementsystem.entity.User;
import com.infosys.grantdisbursementsystem.repository.AuditLogRepository;
import com.infosys.grantdisbursementsystem.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void log(
            String actionType,
            String entityType,
            Long entityId,
            String oldValue,
            String newValue
    ) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        AuditLog auditLog = new AuditLog();

        auditLog.setActorUserId(user.getUserId());

        auditLog.setActorRole(
                user.getRole().name()
        );

        // Mandatory database column
        auditLog.setEntityId(entityId);

        // Mandatory database column
        auditLog.setAction(actionType);

        auditLog.setActionType(actionType);

        auditLog.setPerformedBy(
                user.getFullName()
        );

        auditLog.setEntityType(entityType);

        auditLog.setEntityAffected(
                entityType + ":" + entityId
        );

        auditLog.setDetails(
                "Old Value: " + oldValue
                        + ", New Value: " + newValue
        );

        auditLog.setTimestamp(
                LocalDateTime.now()
        );

        auditLogRepository.save(auditLog);
    }

    // Get audit logs with optional filters
    public List<AuditLog> getAuditLogs(
            String entityType,
            Long entityId
    ) {

        // Both filters provided
        if (entityType != null && entityId != null) {

            return auditLogRepository
                    .findByEntityTypeAndEntityId(
                            entityType,
                            entityId
                    );
        }

        // Only entityType provided
        if (entityType != null) {

            return auditLogRepository
                    .findByEntityType(entityType);
        }

        // Only entityId provided
        if (entityId != null) {

            return auditLogRepository
                    .findByEntityId(entityId);
        }

        // No filters provided
        return auditLogRepository.findAll();
    }
}