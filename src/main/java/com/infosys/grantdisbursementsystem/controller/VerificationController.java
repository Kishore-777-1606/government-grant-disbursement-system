package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.Verification;
import com.infosys.grantdisbursementsystem.service.VerificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/verifications")
public class VerificationController {


    @Autowired
    private VerificationService verificationService;



    // Create Verification
    @PostMapping("/create")
    public ResponseEntity<Verification> createVerification(
            @RequestParam Long applicationId,
            @RequestParam String officerRole) {


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
    @GetMapping("/{id}")
    public ResponseEntity<Verification> getVerificationById(
            @PathVariable Long id) {


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




    // Approve Verification
    // FIELD_OFFICER and DISTRICT_OFFICER only
    @PutMapping("/{id}/approve")
    public ResponseEntity<Verification> approveVerification(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role) {


        if(!role.equalsIgnoreCase("FIELD_OFFICER") &&
                !role.equalsIgnoreCase("DISTRICT_OFFICER")) {


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
    // FIELD_OFFICER and DISTRICT_OFFICER only
    @PutMapping("/{id}/reject")
    public ResponseEntity<Verification> rejectVerification(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role) {


        if(!role.equalsIgnoreCase("FIELD_OFFICER") &&
                !role.equalsIgnoreCase("DISTRICT_OFFICER")) {


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
    @PutMapping("/{id}/reverify")
    public ResponseEntity<Verification> reVerify(
            @PathVariable Long id,
            @RequestParam String remarks) {


        return ResponseEntity.ok(
                verificationService.sendForReVerification(
                        id,
                        remarks
                )
        );
    }





    // Escalation API
    @PutMapping("/{id}/escalate")
    public ResponseEntity<String> escalate(
            @PathVariable Long id) {


        verificationService.checkEscalation(id);


        return ResponseEntity.ok(
                "Verification escalated successfully"
        );
    }

}