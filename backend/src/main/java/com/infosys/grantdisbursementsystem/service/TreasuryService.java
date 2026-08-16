package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.dto.TreasuryReleaseRequest;
import com.infosys.grantdisbursementsystem.dto.TreasuryReleaseResponse;
import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class TreasuryService {

    private final BeneficiaryService beneficiaryService;

    public TreasuryService(
            BeneficiaryService beneficiaryService
    ) {
        this.beneficiaryService = beneficiaryService;
    }

    public TreasuryReleaseResponse releaseFunds(
            TreasuryReleaseRequest request
    ) {

        // ------------------------------------------------------------
        // 1. Validate beneficiary
        // ------------------------------------------------------------

        if (request == null) {
            throw new IllegalArgumentException(
                    "Treasury release request cannot be null"
            );
        }

        if (request.getBeneficiaryId() == null) {
            throw new IllegalArgumentException(
                    "Beneficiary ID is required"
            );
        }

        // Verify beneficiary exists before releasing funds.
        Beneficiary beneficiary =
                beneficiaryService.getBeneficiaryById(
                        request.getBeneficiaryId()
                );

        // ------------------------------------------------------------
        // 2. Validate amount
        // ------------------------------------------------------------

        if (request.getAmount() == null
                || request.getAmount().signum() <= 0) {

            throw new IllegalArgumentException(
                    "Release amount must be greater than zero"
            );
        }

        // ------------------------------------------------------------
        // 3. Validate reference ID
        // ------------------------------------------------------------

        if (request.getReferenceId() == null
                || request.getReferenceId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Reference ID is required"
            );
        }

        // ------------------------------------------------------------
        // 4. Mock Treasury integration
        // ------------------------------------------------------------
        //
        // In production this section would call the external
        // Government Treasury API.
        //
        // For this milestone we simulate a successful Treasury
        // response after validating the beneficiary and request.
        // ------------------------------------------------------------

        return new TreasuryReleaseResponse(
                true,
                beneficiary.getId(),
                request.getAmount(),
                request.getReferenceId(),
                "Funds released successfully through mock Treasury service"
        );
    }
}