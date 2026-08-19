package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.Verification;
import com.infosys.grantdisbursementsystem.service.AuditLogService;
import com.infosys.grantdisbursementsystem.service.VerificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verifications")
public class VerificationController {

    private final VerificationService verificationService;
    private final AuditLogService auditLogService;

    public VerificationController(
            VerificationService verificationService,
            AuditLogService auditLogService
    ) {
        this.verificationService = verificationService;
        this.auditLogService = auditLogService;
    }

    // Create Verification
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Verification> createVerification(
            @RequestParam Long applicationId
    ) {

        Verification verification = verificationService.createVerification(
                applicationId,
                null
        );

        auditLogService.log(
                "CREATE",
                "VERIFICATION",
                verification.getVerificationId(),
                null,
                "Verification created for application: " + applicationId
        );

        return ResponseEntity.ok(verification);
    }

    // Get All Verifications
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<Verification>> getAllVerifications() {

        return ResponseEntity.ok(
                verificationService.getAllVerifications()
        );
    }

    // Get Verification By ID
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Verification> getVerificationById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                verificationService.getVerificationById(id)
        );
    }

    // Get Pending Verifications
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<Verification>> getPendingVerifications() {

        return ResponseEntity.ok(
                verificationService.getPendingVerifications()
        );
    }

    // Full stage-by-stage audit trail for one application
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/application/{applicationId:[0-9]+}/history")
    public ResponseEntity<List<Verification>> getVerificationHistory(
            @PathVariable Long applicationId
    ) {

        return ResponseEntity.ok(
                verificationService.getVerificationHistory(applicationId)
        );
    }

    // Approve Verification
    // Real stage-authority enforcement lives in VerificationService,
    // checked against the caller's actual authenticated role — not a
    // client-supplied param, which could never be trusted for this anyway.
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id:[0-9]+}/approve")
    public ResponseEntity<Verification> approveVerification(
            @PathVariable Long id,
            @RequestParam String remarks
    ) {

        Verification verification = verificationService.approveVerification(
                id,
                remarks
        );

        auditLogService.log(
                "APPROVE",
                "VERIFICATION",
                id,
                "PENDING",
                "APPROVED"
        );

        return ResponseEntity.ok(verification);
    }

    // Reject Verification
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id:[0-9]+}/reject")
    public ResponseEntity<Verification> rejectVerification(
            @PathVariable Long id,
            @RequestParam String remarks
    ) {

        Verification verification = verificationService.rejectVerification(
                id,
                remarks
        );

        auditLogService.log(
                "REJECT",
                "VERIFICATION",
                id,
                "PENDING",
                "REJECTED"
        );

        return ResponseEntity.ok(verification);
    }

    // Send For Re-Verification
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id:[0-9]+}/reverify")
    public ResponseEntity<Verification> reVerify(
            @PathVariable Long id,
            @RequestParam String remarks
    ) {

        Verification verification =
                verificationService.sendForReVerification(
                        id,
                        remarks
                );

        auditLogService.log(
                "REVERIFY",
                "VERIFICATION",
                id,
                "REJECTED",
                "REVERIFICATION_REQUESTED"
        );

        return ResponseEntity.ok(verification);
    }

    // Escalation API
    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id:[0-9]+}/escalate")
    public ResponseEntity<String> escalate(
            @PathVariable Long id
    ) {

        verificationService.checkEscalation(id);

        auditLogService.log(
                "ESCALATE",
                "VERIFICATION",
                id,
                null,
                "ESCALATED"
        );

        return ResponseEntity.ok(
                "Verification escalated successfully"
        );
    }
}