package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.dto.TreasuryReleaseRequest;
import com.infosys.grantdisbursementsystem.dto.TreasuryReleaseResponse;
import com.infosys.grantdisbursementsystem.service.TreasuryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/treasury")
public class TreasuryController {

    private final TreasuryService treasuryService;

    public TreasuryController(TreasuryService treasuryService) {
        this.treasuryService = treasuryService;
    }

    @PreAuthorize("hasAnyRole('FINANCE_APPROVER', 'ADMIN')")
    @PostMapping("/release-funds")
    public TreasuryReleaseResponse releaseFunds(
            @Valid @RequestBody TreasuryReleaseRequest request
    ) {
        return treasuryService.releaseFunds(request);
    }
}