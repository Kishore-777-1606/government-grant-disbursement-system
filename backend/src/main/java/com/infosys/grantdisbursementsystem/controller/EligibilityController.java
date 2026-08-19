package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.dto.EligibilityView;
import com.infosys.grantdisbursementsystem.service.EligibilityService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;



@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {



    private final EligibilityService eligibilityService;




    public EligibilityController(
            EligibilityService eligibilityService
    ) {

        this.eligibilityService = eligibilityService;

    }


    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping
    public List<EligibilityView> getAllEligibilityRecords() {

        return eligibilityService.getAllEligibilityRecords();

    }


    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'FINANCE_APPROVER', 'ADMIN')")
    @GetMapping("/{applicationId}")
    public EligibilityView getEligibilityByApplicationId(
            @PathVariable Long applicationId
    ) {


        return eligibilityService.getEligibilityByApplicationId(
                Objects.requireNonNull(applicationId)
        );

    }


}