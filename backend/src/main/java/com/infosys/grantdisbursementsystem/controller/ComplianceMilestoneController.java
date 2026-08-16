package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;
import com.infosys.grantdisbursementsystem.service.ComplianceMilestoneService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/milestones")
public class ComplianceMilestoneController {

    private final ComplianceMilestoneService milestoneService;

    public ComplianceMilestoneController(
            ComplianceMilestoneService milestoneService) {

        this.milestoneService = milestoneService;
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id}/complete")
    public ComplianceMilestone completeMilestone(@PathVariable Long id) {

        return milestoneService.completeMilestone(id);
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id}/in-progress")
    public ComplianceMilestone markInProgress(@PathVariable Long id) {

        return milestoneService.markInProgress(id);
    }

    // Marking something non-compliant is a review judgement, not routine
    // fieldwork — restricted the same way escalation is elsewhere.
    @PreAuthorize("hasAnyRole('DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id}/non-compliant")
    public ComplianceMilestone markNonCompliant(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {

        return milestoneService.markNonCompliant(id, reason);
    }

    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @GetMapping("/reminders")
    public List<ComplianceMilestone> getReminders() {

        return milestoneService.getUpcomingReminders();
    }
}