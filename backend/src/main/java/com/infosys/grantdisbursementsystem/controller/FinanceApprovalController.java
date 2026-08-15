package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.service.FinanceApprovalService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/finance")
public class FinanceApprovalController {



    private final FinanceApprovalService financeApprovalService;




    public FinanceApprovalController(
            FinanceApprovalService financeApprovalService
    ) {

        this.financeApprovalService = financeApprovalService;

    }






@PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'ADMIN')")
    @PostMapping("/create")
    public FinanceApproval createApproval(
            @RequestParam Long applicationId,
            @RequestParam String financeOfficer
    ) {


        return financeApprovalService.createApproval(
                Objects.requireNonNull(applicationId),
                financeOfficer
        );

    }







    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping
    public List<FinanceApproval> getAllApprovals() {

        return financeApprovalService.getAllApprovals();

    }







    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/id/{id}")
    public FinanceApproval getApprovalById(
            @PathVariable Long id
    ) {


        return financeApprovalService.getApprovalById(
                Objects.requireNonNull(id)
        );

    }






// @PreAuthorize now enforces this properly using the verified token role.
    // The "role" request param below is legacy and no longer trustworthy on its own
    // (a client could send any value) — kept only so the existing frontend call doesn't break.
    @PreAuthorize("hasAnyRole('FINANCE_APPROVER', 'ADMIN')")
@PutMapping("/{id}/approve")
public FinanceApproval approve(
        @PathVariable Long id,
        @RequestParam String remarks
) {

    return financeApprovalService.approve(
            Objects.requireNonNull(id),
            remarks
    );
}






   @PreAuthorize("hasAnyRole('FINANCE_APPROVER', 'ADMIN')")
@PutMapping("/{id}/reject")
public FinanceApproval reject(
        @PathVariable Long id,
        @RequestParam String remarks
) {

    return financeApprovalService.reject(
            Objects.requireNonNull(id),
            remarks
    );
}






    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/pending")
    public List<FinanceApproval> getPendingApprovals() {

        return financeApprovalService.getPendingApprovals();

    }







    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/approved")
    public List<FinanceApproval> getApprovedApprovals() {

        return financeApprovalService.getApprovedApprovals();

    }







    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/rejected")
    public List<FinanceApproval> getRejectedApprovals() {

        return financeApprovalService.getRejectedApprovals();

    }


}