package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.dto.CreatePlanRequest;
import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.DisbursementInstallment;
import com.infosys.grantdisbursementsystem.entity.DisbursementPlan;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.service.DisbursementPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.infosys.grantdisbursementsystem.repository.DisbursementInstallmentRepository;
import java.util.List;
import com.infosys.grantdisbursementsystem.repository.DisbursementPlanRepository;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/disbursement-plans")
public class DisbursementPlanController {

    @Autowired
    private DisbursementPlanRepository planRepository;

    @Autowired
    private DisbursementPlanService planService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private DisbursementInstallmentRepository installmentRepository;

    @PostMapping
    public DisbursementPlan createPlan(@RequestBody CreatePlanRequest request) {

        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        return planService.createPlan(
                application,
                request.getTotalAmount(),
                request.getNumberOfInstallments()
        );
    }

    @PostMapping("/release/{installmentId}")
    public DisbursementInstallment releaseInstallment(
            @PathVariable Long installmentId) {

        return planService.releaseInstallmentIfMilestoneComplete(installmentId);
    }

    @GetMapping("/{planId}/installments")
    public List<DisbursementInstallment> getInstallments(@PathVariable Long planId) {
        return installmentRepository.findByDisbursementPlanPlanId(planId);
    }

    @GetMapping("/{id}")
    public DisbursementPlan getPlan(@PathVariable Long id) {

        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
    }
}