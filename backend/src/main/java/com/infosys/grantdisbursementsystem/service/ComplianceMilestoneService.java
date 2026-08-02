package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;
import com.infosys.grantdisbursementsystem.repository.ComplianceMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
}