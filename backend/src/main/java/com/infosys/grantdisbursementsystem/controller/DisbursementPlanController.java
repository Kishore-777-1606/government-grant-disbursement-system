package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.dto.CreatePlanRequest;
import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.DisbursementInstallment;
import com.infosys.grantdisbursementsystem.entity.DisbursementPlan;

import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;

import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.DisbursementInstallmentRepository;
import com.infosys.grantdisbursementsystem.repository.DisbursementPlanRepository;

import com.infosys.grantdisbursementsystem.service.DisbursementPlanService;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/disbursement-plans")
public class DisbursementPlanController {

    private final DisbursementPlanRepository planRepository;

    private final DisbursementPlanService planService;

    private final ApplicationRepository applicationRepository;

    private final DisbursementInstallmentRepository installmentRepository;




    public DisbursementPlanController(
            DisbursementPlanRepository planRepository,
            DisbursementPlanService planService,
            ApplicationRepository applicationRepository,
            DisbursementInstallmentRepository installmentRepository
    ) {

        this.planRepository = planRepository;
        this.planService = planService;
        this.applicationRepository = applicationRepository;
        this.installmentRepository = installmentRepository;

    }




  @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'ADMIN')")
    @PostMapping
    public DisbursementPlan createPlan(
            @RequestBody CreatePlanRequest request
    ) {

        if(request.getApplicationId() == null){

            throw new IllegalArgumentException(
                    "Application ID cannot be null"
            );

        }

        Application application =
                applicationRepository.findById(
                        Objects.requireNonNull(
                                request.getApplicationId()
                        )
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found"
                        )
                );

        return planService.createPlan(
                application,
                request.getTotalAmount(),
                request.getNumberOfInstallments()
        );

    }



    @PreAuthorize("hasAnyRole('FINANCE_APPROVER', 'ADMIN')")
    @PostMapping("/release/{installmentId}")
    public DisbursementInstallment releaseInstallment(
            @PathVariable @NonNull Long installmentId
    ) {

        return planService.releaseInstallmentIfMilestoneComplete(
                installmentId
        );

    }




    // Get All Plans (list view for the Disbursement page)
    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping
    public List<DisbursementPlan> getAllPlans() {

        return planRepository.findAll();

    }




    // Get All Installments across every plan (flat table for the Disbursement page,
    // so a finance/field user can see and act on every pending release in one place)
    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/installments/all")
    public List<DisbursementInstallment> getAllInstallments() {

        return installmentRepository.findAll();

    }




    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/{planId:[0-9]+}/installments")
    public List<DisbursementInstallment> getInstallments(
            @PathVariable @NonNull Long planId
    ) {

        return installmentRepository
                .findByDisbursementPlanPlanId(
                        planId
                );

    }




    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/{id:[0-9]+}")
    public DisbursementPlan getPlan(
            @PathVariable @NonNull Long id
    ) {

        return planRepository.findById(
                id
        )
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Plan not found"
                )
        );

    }


}