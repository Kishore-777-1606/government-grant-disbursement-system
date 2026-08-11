package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.entity.Verification;
import com.infosys.grantdisbursementsystem.service.VerificationService;

import org.springframework.http.ResponseEntity;
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



    // Create Verification
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



    // Get All Verifications
    @GetMapping
    public ResponseEntity<List<Verification>> getAllVerifications() {

        return ResponseEntity.ok(
                verificationService.getAllVerifications()
        );

    }



    // Get Verification By ID
    // Only accepts numeric id values
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Verification> getVerificationById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                verificationService.getVerificationById(id)
        );

    }



    // Get Pending Verifications
    @GetMapping("/pending")
    public ResponseEntity<List<Verification>> getPendingVerifications() {

        return ResponseEntity.ok(
                verificationService.getPendingVerifications()
        );

    }



    // Full stage-by-stage audit trail for one application (oldest first).
    // Backs the "expand row to see history" action on the Verification page.
    @GetMapping("/application/{applicationId:[0-9]+}/history")
    public ResponseEntity<List<Verification>> getVerificationHistory(
            @PathVariable Long applicationId
    ) {

        return ResponseEntity.ok(
                verificationService.getVerificationHistory(applicationId)
        );

    }



    // Approve Verification
    @PutMapping("/{id:[0-9]+}/approve")
    public ResponseEntity<Verification> approveVerification(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role
    ) {


        if (!"FIELD_OFFICER".equalsIgnoreCase(role)
                &&
            !"DISTRICT_OFFICER".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "Only Field Officer or District Officer can verify"
            );

        }


        return ResponseEntity.ok(
                verificationService.approveVerification(
                        id,
                        remarks
                )
        );

    }



    // Reject Verification
    @PutMapping("/{id:[0-9]+}/reject")
    public ResponseEntity<Verification> rejectVerification(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role
    ) {


        if (!"FIELD_OFFICER".equalsIgnoreCase(role)
                &&
            !"DISTRICT_OFFICER".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "Only Field Officer or District Officer can reject"
            );

        }


        return ResponseEntity.ok(
                verificationService.rejectVerification(
                        id,
                        remarks
                )
        );

    }



    // Send For Re-Verification
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



    // Escalation API
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