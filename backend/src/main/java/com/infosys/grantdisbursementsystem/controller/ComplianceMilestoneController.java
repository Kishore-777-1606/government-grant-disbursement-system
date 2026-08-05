package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;
import com.infosys.grantdisbursementsystem.service.ComplianceMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/milestones")
public class ComplianceMilestoneController {

    @Autowired
    private ComplianceMilestoneService milestoneService;

    @GetMapping("/check-overdue")
    public String checkOverdue() {
        milestoneService.flagOverdueMilestones();
        return "Overdue check completed";
    }

    @GetMapping("/reminders")
    public List<ComplianceMilestone> getReminders() {
        return milestoneService.getUpcomingReminders();
    }
}