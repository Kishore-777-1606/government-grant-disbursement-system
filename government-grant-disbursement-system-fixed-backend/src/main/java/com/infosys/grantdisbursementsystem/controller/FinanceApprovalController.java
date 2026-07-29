package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.service.FinanceApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance")
public class FinanceApprovalController {

    @Autowired
    private FinanceApprovalService financeApprovalService;

    // Create Finance Approval
    @PostMapping("/create")
    public FinanceApproval createApproval(
            @RequestParam Long applicationId,
            @RequestParam String financeOfficer) {

        return financeApprovalService.createApproval(applicationId, financeOfficer);
    }

    // Get All Approvals
    @GetMapping
    public List<FinanceApproval> getAllApprovals() {
        return financeApprovalService.getAllApprovals();
    }

    // Get Approval By ID
    @GetMapping("/id/{id}")
    public FinanceApproval getApprovalById(@PathVariable Long id) {
        return financeApprovalService.getApprovalById(id);
    }

    @PutMapping("/{id}/approve")
    public FinanceApproval approve(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role) {


        if(!role.equalsIgnoreCase("FINANCE_OFFICER")) {

            throw new RuntimeException(
                    "Only Finance Officer can approve payment"
            );
        }


        return financeApprovalService.approve(id, remarks);
    }

    @PutMapping("/{id}/reject")
    public FinanceApproval reject(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role) {


        if(!role.equalsIgnoreCase("FINANCE_OFFICER")) {

            throw new RuntimeException(
                    "Only Finance Officer can reject payment"
            );
        }


        return financeApprovalService.reject(id, remarks);
    }
    // Get Pending Approvals
    @GetMapping("/pending")
    public List<FinanceApproval> getPendingApprovals() {
        return financeApprovalService.getPendingApprovals();
    }

    // Get Approved Approvals
    @GetMapping("/approved")
    public List<FinanceApproval> getApprovedApprovals() {
        return financeApprovalService.getApprovedApprovals();
    }

    // Get Rejected Approvals
    @GetMapping("/rejected")
    public List<FinanceApproval> getRejectedApprovals() {
        return financeApprovalService.getRejectedApprovals();
    }

}