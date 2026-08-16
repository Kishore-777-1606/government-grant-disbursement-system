package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.AuditLog;
import com.infosys.grantdisbursementsystem.service.AuditLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DISTRICT_OFFICER')")
    @GetMapping
    public List<AuditLog> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId
    ) {

        return auditLogService.getAuditLogs(
                entityType,
                entityId
        );
    }
}