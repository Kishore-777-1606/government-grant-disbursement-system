package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.dto.BeneficiaryVerificationResponse;
import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryDbService {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryDbService(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    public BeneficiaryVerificationResponse verifyBeneficiary(Long id) {

        try {
            Beneficiary beneficiary =
                    beneficiaryService.getBeneficiaryById(id);

            return new BeneficiaryVerificationResponse(
                    beneficiary.getId(),
                    true,
                    "Beneficiary verified successfully"
            );

        } catch (ResourceNotFoundException e) {

            return new BeneficiaryVerificationResponse(
                    id,
                    false,
                    "Beneficiary not found"
            );
        }
    }
}