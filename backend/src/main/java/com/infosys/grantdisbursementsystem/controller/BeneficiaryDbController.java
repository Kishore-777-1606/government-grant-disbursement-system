package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.dto.BeneficiaryVerificationResponse;
import com.infosys.grantdisbursementsystem.service.BeneficiaryDbService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiary-db")
public class BeneficiaryDbController {

    private final BeneficiaryDbService beneficiaryDbService;

    public BeneficiaryDbController(
            BeneficiaryDbService beneficiaryDbService
    ) {
        this.beneficiaryDbService = beneficiaryDbService;
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @GetMapping("/verify/{id}")
    public BeneficiaryVerificationResponse verifyBeneficiary(
            @PathVariable Long id
    ) {

        return beneficiaryDbService.verifyBeneficiary(id);
    }
}