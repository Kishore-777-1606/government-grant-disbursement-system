package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.Verification;
import com.infosys.grantdisbursementsystem.service.VerificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/verifications")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(
            VerificationService verificationService
    ) {
        this.verificationService = verificationService;
    }

    // ============================================================
    // CREATE VERIFICATION
    // ============================================================

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Verification> createVerification(
            @RequestParam Long applicationId,
            @RequestParam String officerRole
    ) {

        return ResponseEntity.ok(
                verificationService.createVerification(
                        applicationId,
                        officerRole
                )
        );
    }

    // ============================================================
    // GET ALL VERIFICATIONS
    // ============================================================

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<Verification>> getAllVerifications() {

        return ResponseEntity.ok(
                verificationService.getAllVerifications()
        );
    }

    // ============================================================
    // GET VERIFICATION BY ID
    // ============================================================

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Verification> getVerificationById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                verificationService.getVerificationById(id)
        );
    }

    // ============================================================
    // GET PENDING VERIFICATIONS
    // ============================================================

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<Verification>> getPendingVerifications() {

        return ResponseEntity.ok(
                verificationService.getPendingVerifications()
        );
    }

    // ============================================================
    // VERIFICATION HISTORY
    // ============================================================

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/application/{applicationId:[0-9]+}/history")
    public ResponseEntity<List<Verification>> getVerificationHistory(
            @PathVariable Long applicationId
    ) {

        return ResponseEntity.ok(
                verificationService.getVerificationHistory(applicationId)
        );
    }

    // ============================================================
    // APPROVE VERIFICATION
    // ============================================================

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id:[0-9]+}/approve")
    public ResponseEntity<Verification> approveVerification(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role
    ) {

        /*
         * Spring Security already verifies the authenticated user's role
         * through @PreAuthorize.
         *
         * The role parameter is retained for frontend compatibility.
         */
        if (!"FIELD_OFFICER".equalsIgnoreCase(role)
                && !"DISTRICT_OFFICER".equalsIgnoreCase(role)
                && !"ADMIN".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "Only Field Officer, District Officer or Admin can verify"
            );
        }

        return ResponseEntity.ok(
                verificationService.approveVerification(
                        id,
                        remarks
                )
        );
    }

    // ============================================================
    // REJECT VERIFICATION
    // ============================================================

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id:[0-9]+}/reject")
    public ResponseEntity<Verification> rejectVerification(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role
    ) {

        /*
         * Spring Security already verifies the authenticated user's role
         * through @PreAuthorize.
         *
         * The role parameter is retained for frontend compatibility.
         */
        if (!"FIELD_OFFICER".equalsIgnoreCase(role)
                && !"DISTRICT_OFFICER".equalsIgnoreCase(role)
                && !"ADMIN".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "Only Field Officer, District Officer or Admin can reject"
            );
        }

        return ResponseEntity.ok(
                verificationService.rejectVerification(
                        id,
                        remarks
                )
        );
    }

    // ============================================================
    // SEND FOR RE-VERIFICATION
    // ============================================================

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id:[0-9]+}/reverify")
    public ResponseEntity<Verification> reVerify(
            @PathVariable Long id,
            @RequestParam String remarks
    ) {

        return ResponseEntity.ok(
                verificationService.sendForReVerification(
                        id,
                        remarks
                )
        );
    }

    // ============================================================
    // ESCALATE VERIFICATION
    // ============================================================

    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id:[0-9]+}/escalate")
    public ResponseEntity<String> escalate(
            @PathVariable Long id
    ) {

        verificationService.checkEscalation(id);

        return ResponseEntity.ok(
                "Verification escalated successfully"
        );
    }
}