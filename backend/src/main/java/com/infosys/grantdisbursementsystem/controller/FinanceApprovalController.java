package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.service.FinanceApprovalService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;



@RestController
@RequestMapping("/api/finance")
public class FinanceApprovalController {



    private final FinanceApprovalService financeApprovalService;




    public FinanceApprovalController(
            FinanceApprovalService financeApprovalService
    ) {

        this.financeApprovalService = financeApprovalService;

    }







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







    @GetMapping
    public List<FinanceApproval> getAllApprovals() {

        return financeApprovalService.getAllApprovals();

    }







    @GetMapping("/id/{id}")
    public FinanceApproval getApprovalById(
            @PathVariable Long id
    ) {


        return financeApprovalService.getApprovalById(
                Objects.requireNonNull(id)
        );

    }







    @PutMapping("/{id}/approve")
    public FinanceApproval approve(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role
    ) {



        if(!"FINANCE_OFFICER".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "Only Finance Officer can approve payment"
            );

        }



        return financeApprovalService.approve(
                Objects.requireNonNull(id),
                remarks
        );

    }







    @PutMapping("/{id}/reject")
    public FinanceApproval reject(
            @PathVariable Long id,
            @RequestParam String remarks,
            @RequestParam String role
    ) {



        if(!"FINANCE_OFFICER".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "Only Finance Officer can reject payment"
            );

        }



        return financeApprovalService.reject(
                Objects.requireNonNull(id),
                remarks
        );

    }







    @GetMapping("/pending")
    public List<FinanceApproval> getPendingApprovals() {

        return financeApprovalService.getPendingApprovals();

    }







    @GetMapping("/approved")
    public List<FinanceApproval> getApprovedApprovals() {

        return financeApprovalService.getApprovedApprovals();

    }







    @GetMapping("/rejected")
    public List<FinanceApproval> getRejectedApprovals() {

        return financeApprovalService.getRejectedApprovals();

    }


}