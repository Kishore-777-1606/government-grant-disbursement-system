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

    // Audit Log Service
    private final AuditLogService auditLogService;

    public ComplianceMilestoneService(
            ComplianceMilestoneRepository milestoneRepository,
            AuditLogService auditLogService
    ) {
        this.milestoneRepository = milestoneRepository;
        this.auditLogService = auditLogService;
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
        milestone.setMilestoneType(milestoneType);
        milestone.setStatus("Pending");

        int daysToAdd;

        if (milestoneType.equalsIgnoreCase("Documentation")) {

            daysToAdd = 7;

        } else if (milestoneType.equalsIgnoreCase("Ground Verification")) {

            daysToAdd = 15;

        } else {

            daysToAdd = 30;
        }

        milestone.setDueDate(
                LocalDate.now().plusDays(daysToAdd)
        );

        return milestoneRepository.save(milestone);
    }

    public void flagOverdueMilestones() {

        List<ComplianceMilestone> pending =
                milestoneRepository.findByStatus("Pending");

        LocalDate today = LocalDate.now();

        for (ComplianceMilestone milestone : pending) {

            if (milestone.getDueDate() != null
                    && milestone.getDueDate().isBefore(today)) {

                milestone.setStatus("Overdue");

                milestone.setRemarks(
                        "Milestone missed deadline — flagged for review"
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
                milestoneRepository.findByStatus("Pending");

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

    public ComplianceMilestone completeMilestone(Long milestoneId) {

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

        // Check if milestone is already completed
        if ("Completed".equalsIgnoreCase(
                milestone.getStatus()
        )) {

            throw new IllegalStateException(
                    "Milestone is already completed"
            );
        }

        // Store old status before changing it
        String oldStatus =
                milestone.getStatus();

        milestone.setStatus("Completed");

        milestone.setCompletedDate(
                LocalDate.now()
        );

        milestone.setRemarks(
                "Milestone completed successfully"
        );

        ComplianceMilestone savedMilestone =
                milestoneRepository.save(milestone);

        // =========================
        // AUDIT LOG - COMPLETE
        // =========================
        auditLogService.log(
                "COMPLETE",
                "COMPLIANCE_MILESTONE",
                milestoneId,
                oldStatus,
                "Completed"
        );

        return savedMilestone;
    }

    /**
     * Marks a milestone as actively being worked on — e.g. the beneficiary/officer
     * has started the required action but hasn't yet submitted proof for review.
     * Only valid from "Pending"; a milestone that's already Overdue, Completed, or
     * Non-Compliant shouldn't be silently reset to In Progress.
     */
    public ComplianceMilestone markInProgress(Long milestoneId) {

        ComplianceMilestone milestone =
                milestoneRepository.findById(milestoneId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Milestone not found with ID: "
                                                + milestoneId
                                )
                        );

        if (!"Pending".equalsIgnoreCase(milestone.getStatus())) {

            throw new IllegalStateException(
                    "Only a Pending milestone can be marked In Progress (current status: "
                            + milestone.getStatus() + ")"
            );
        }

        String oldStatus = milestone.getStatus();

        milestone.setStatus("In Progress");

        milestone.setRemarks(
                "Work started on milestone"
        );

        ComplianceMilestone savedMilestone =
                milestoneRepository.save(milestone);

        auditLogService.log(
                "MARK_IN_PROGRESS",
                "COMPLIANCE_MILESTONE",
                milestoneId,
                oldStatus,
                "In Progress"
        );

        return savedMilestone;
    }

    /**
     * Marks a milestone as non-compliant — used when the responsible officer
     * reviews the submitted proof/action and finds it does NOT satisfy the
     * milestone's requirement. This is a quality/compliance judgement, distinct
     * from "Overdue" which is purely time-based.
     */
    public ComplianceMilestone markNonCompliant(Long milestoneId, String reason) {

        ComplianceMilestone milestone =
                milestoneRepository.findById(milestoneId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Milestone not found with ID: "
                                                + milestoneId
                                )
                        );

        if ("Completed".equalsIgnoreCase(milestone.getStatus())) {

            throw new IllegalStateException(
                    "A completed milestone cannot be marked non-compliant"
            );
        }

        String oldStatus = milestone.getStatus();

        milestone.setStatus("Non-Compliant");

        milestone.setRemarks(
                (reason == null || reason.isBlank())
                        ? "Marked non-compliant"
                        : reason
        );

        ComplianceMilestone savedMilestone =
                milestoneRepository.save(milestone);

        auditLogService.log(
                "MARK_NON_COMPLIANT",
                "COMPLIANCE_MILESTONE",
                milestoneId,
                oldStatus,
                "Non-Compliant"
        );

        return savedMilestone;
    }
}