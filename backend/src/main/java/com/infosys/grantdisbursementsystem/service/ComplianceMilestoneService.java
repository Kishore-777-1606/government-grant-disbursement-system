package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.ComplianceMilestoneRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComplianceMilestoneService {

    private final ComplianceMilestoneRepository milestoneRepository;

    public ComplianceMilestoneService(
            ComplianceMilestoneRepository milestoneRepository) {

        this.milestoneRepository = milestoneRepository;
    }

    public ComplianceMilestone createMilestone(
            Application application,
            String milestoneType) {

        if (application == null) {
            throw new IllegalArgumentException(
                    "Application cannot be null"
            );
        }

        if (milestoneType == null || milestoneType.isBlank()) {
            throw new IllegalArgumentException(
                    "Milestone type is required"
            );
        }

        ComplianceMilestone milestone =
                new ComplianceMilestone();

        milestone.setApplication(application);
        milestone.setMilestoneType(
                milestoneType.toUpperCase()
        );
        milestone.setStatus("PENDING");

        int daysToAdd;

        if ("DOCUMENT_VERIFICATION".equalsIgnoreCase(
                milestoneType)) {

            daysToAdd = 7;

        } else if ("FIELD_VERIFICATION".equalsIgnoreCase(
                milestoneType)) {

            daysToAdd = 15;

        } else {

            daysToAdd = 30;
        }

        milestone.setDueDate(
                LocalDate.now().plusDays(daysToAdd)
        );

        milestone.setCompletedDate(null);
        milestone.setRemarks(null);

        return milestoneRepository.save(milestone);
    }

    public void flagOverdueMilestones() {

        List<ComplianceMilestone> pending =
                milestoneRepository.findByStatusIgnoreCase(
                        "PENDING"
                );

        LocalDate today = LocalDate.now();

        for (ComplianceMilestone milestone : pending) {

            if (milestone.getDueDate() != null
                    && milestone.getDueDate().isBefore(today)) {

                milestone.setStatus("OVERDUE");

                milestone.setRemarks(
                        "Milestone missed deadline - flagged for review"
                );

                milestoneRepository.save(milestone);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledOverdueCheck() {

        flagOverdueMilestones();
    }

    public List<ComplianceMilestone> getUpcomingReminders() {

        List<ComplianceMilestone> pending =
                milestoneRepository.findByStatusIgnoreCase(
                        "PENDING"
                );

        List<ComplianceMilestone> reminders =
                new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (ComplianceMilestone milestone : pending) {

            if (milestone.getDueDate() == null) {
                continue;
            }

            long daysLeft =
                    ChronoUnit.DAYS.between(
                            today,
                            milestone.getDueDate()
                    );

            if (daysLeft >= 0 && daysLeft <= 3) {
                reminders.add(milestone);
            }
        }

        return reminders;
    }

    public List<ComplianceMilestone> getAllMilestones() {

        return milestoneRepository.findAll();
    }

    public ComplianceMilestone completeMilestone(
            Long milestoneId) {

        if (milestoneId == null) {
            throw new IllegalArgumentException(
                    "Milestone ID cannot be null"
            );
        }

        ComplianceMilestone milestone =
                milestoneRepository.findById(milestoneId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Milestone not found with ID: "
                                                + milestoneId
                                )
                        );

        if ("COMPLETED".equalsIgnoreCase(
                milestone.getStatus())) {

            return milestone;
        }

        milestone.setStatus("COMPLETED");

        milestone.setCompletedDate(
                LocalDate.now()
        );

        milestone.setRemarks(
                "Milestone completed successfully"
        );

        return milestoneRepository.save(milestone);
    }
}