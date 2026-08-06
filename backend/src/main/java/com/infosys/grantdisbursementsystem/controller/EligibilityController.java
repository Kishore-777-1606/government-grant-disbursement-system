package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.dto.EligibilityView;
import com.infosys.grantdisbursementsystem.service.EligibilityService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;



@RestController
@RequestMapping("/eligibility")
public class EligibilityController {



    private final EligibilityService eligibilityService;




    public EligibilityController(
            EligibilityService eligibilityService
    ) {

        this.eligibilityService = eligibilityService;

    }







    @GetMapping
    public List<EligibilityView> getAllEligibilityRecords() {

        return eligibilityService.getAllEligibilityRecords();

    }







    @GetMapping("/{applicationId}")
    public EligibilityView getEligibilityByApplicationId(
            @PathVariable Long applicationId
    ) {


        return eligibilityService.getEligibilityByApplicationId(
                Objects.requireNonNull(applicationId)
        );

    }


}