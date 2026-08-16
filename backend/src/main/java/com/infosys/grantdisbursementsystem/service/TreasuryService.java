package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.dto.TreasuryReleaseRequest;
import com.infosys.grantdisbursementsystem.dto.TreasuryReleaseResponse;
import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TreasuryService {

    private final BeneficiaryService beneficiaryService;

    public TreasuryService(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    public TreasuryReleaseResponse releaseFunds(
            TreasuryReleaseRequest request
    ) {

        // Verify that the beneficiary exists before releasing funds.
        Beneficiary beneficiary =
                beneficiaryService.getBeneficiaryById(
                        request.getBeneficiaryId()
                );

        // Mock Treasury integration.
        // In a real system, this would call the external Treasury API.
        return new TreasuryReleaseResponse(
                true,
                beneficiary.getId(),
                request.getAmount(),
                request.getReferenceId(),
                "Funds released successfully"
        );
    }
}