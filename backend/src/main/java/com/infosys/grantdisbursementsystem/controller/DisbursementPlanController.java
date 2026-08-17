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
            DisbursementInstallmentRepository installmentRepository) {

        this.planRepository = planRepository;
        this.planService = planService;
        this.applicationRepository = applicationRepository;
        this.installmentRepository = installmentRepository;
    }

    @PostMapping
    public DisbursementPlan createPlan(
            @RequestBody CreatePlanRequest request) {

        if (request == null ||
                request.getApplicationId() == null) {

            throw new IllegalArgumentException(
                    "Application ID cannot be null"
            );
        }

        Application application =
                applicationRepository.findById(
                        request.getApplicationId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with ID: "
                                        + request.getApplicationId()
                        )
                );

        return planService.createPlan(
                application,
                request.getTotalAmount(),
                request.getNumberOfInstallments()
        );
    }

    @PreAuthorize(
            "hasAnyRole('FINANCE_APPROVER', 'ADMIN')"
    )
    @PostMapping("/release/{installmentId}")
    public DisbursementInstallment releaseInstallment(
            @PathVariable @NonNull Long installmentId) {

        return planService
                .releaseInstallmentIfMilestoneComplete(
                        installmentId
                );
    }

    @GetMapping
    public List<DisbursementPlan> getAllPlans() {

        return planRepository.findAll();
    }

    @GetMapping("/installments/all")
    public List<DisbursementInstallment>
    getAllInstallments() {

        return installmentRepository.findAll();
    }

    @GetMapping("/{planId:[0-9]+}/installments")
    public List<DisbursementInstallment>
    getInstallments(
            @PathVariable @NonNull Long planId) {

        return installmentRepository
                .findByDisbursementPlanPlanId(
                        planId
                );
    }

    @GetMapping("/{id:[0-9]+}")
    public DisbursementPlan getPlan(
            @PathVariable @NonNull Long id) {

        return planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plan not found with ID: "
                                        + id
                        )
                );
    }
}