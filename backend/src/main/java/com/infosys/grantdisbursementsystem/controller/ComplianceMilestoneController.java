package com.infosys.grantdisbursementsystem.controller;


import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;
import com.infosys.grantdisbursementsystem.service.ComplianceMilestoneService;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/milestones")
public class ComplianceMilestoneController {


    private final ComplianceMilestoneService milestoneService;


    public ComplianceMilestoneController(
            ComplianceMilestoneService milestoneService){

        this.milestoneService = milestoneService;
    }



    @GetMapping("/check-overdue")
    public String checkOverdue() {

        milestoneService.flagOverdueMilestones();

        return "Overdue check completed";
    }



    @GetMapping("/reminders")
    public List<ComplianceMilestone> getReminders(){

        return milestoneService.getUpcomingReminders();
    }

}