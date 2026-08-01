package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.dto.CreatePlanRequest;
import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.DisbursementInstallment;
import com.infosys.grantdisbursementsystem.entity.DisbursementPlan;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.service.DisbursementPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/disbursement-plans")
public class DisbursementPlanController {

    @Autowired
    private DisbursementPlanService planService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @PostMapping
    public DisbursementPlan createPlan(@RequestBody CreatePlanRequest request) {

        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

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
}