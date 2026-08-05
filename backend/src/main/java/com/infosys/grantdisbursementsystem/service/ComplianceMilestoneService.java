package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;
import com.infosys.grantdisbursementsystem.repository.ComplianceMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComplianceMilestoneService {

    @Autowired
    private ComplianceMilestoneRepository milestoneRepository;

    public ComplianceMilestone createMilestone(Application application, String milestoneType) {

        ComplianceMilestone milestone = new ComplianceMilestone();

        milestone.setApplication(application);
        milestone.setMilestoneType(milestoneType);
        milestone.setStatus("Pending");

        // Due date calculation logic
        int daysToAdd;

        if (milestoneType.equalsIgnoreCase("Documentation")) {
            daysToAdd = 7;
        } else if (milestoneType.equalsIgnoreCase("Ground Verification")) {
            daysToAdd = 15;
        } else {
            daysToAdd = 30;
        }

        milestone.setDueDate(LocalDate.now().plusDays(daysToAdd));

        return milestoneRepository.save(milestone);
    }

    public void flagOverdueMilestones() {
        List<ComplianceMilestone> pending = milestoneRepository.findByStatus("Pending");

        for (ComplianceMilestone milestone : pending) {
            if (milestone.getDueDate().isBefore(LocalDate.now())) {
                milestone.setStatus("Overdue");
                milestone.setRemarks("Milestone missed deadline — flagged for review");
                milestoneRepository.save(milestone);
            }
        }
    }

    public List<ComplianceMilestone> getUpcomingReminders() {

        List<ComplianceMilestone> pending = milestoneRepository.findByStatus("Pending");
        List<ComplianceMilestone> reminders = new ArrayList<>();

        for (ComplianceMilestone milestone : pending) {
            long daysLeft = LocalDate.now().until(milestone.getDueDate()).getDays();

            if (daysLeft <= 3 && daysLeft >= 0) {
                reminders.add(milestone);
            }
        }

        return reminders;
    }
}