package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.dto.EligibilityView;
import com.infosys.grantdisbursementsystem.service.EligibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eligibility")
public class EligibilityController {

    @Autowired
    private EligibilityService eligibilityService;

    @GetMapping
    public List<EligibilityView> getAllEligibilityRecords() {
        return eligibilityService.getAllEligibilityRecords();
    }

    @GetMapping("/{applicationId}")
    public EligibilityView getEligibilityByApplicationId(@PathVariable Long applicationId) {
        return eligibilityService.getEligibilityByApplicationId(applicationId);
    }
}
