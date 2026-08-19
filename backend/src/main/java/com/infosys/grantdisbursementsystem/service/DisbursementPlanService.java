package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.*;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;

import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.DisbursementInstallmentRepository;
import com.infosys.grantdisbursementsystem.repository.DisbursementPlanRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class DisbursementPlanService {

    private final DisbursementPlanRepository planRepository;

    private final DisbursementInstallmentRepository installmentRepository;

    private final ApplicationRepository applicationRepository;

    private final ComplianceMilestoneService milestoneService;

    // Audit Log Service
    private final AuditLogService auditLogService;

    public DisbursementPlanService(
            DisbursementPlanRepository planRepository,
            DisbursementInstallmentRepository installmentRepository,
            ApplicationRepository applicationRepository,
            ComplianceMilestoneService milestoneService,
            AuditLogService auditLogService
    ) {

        this.planRepository = planRepository;
        this.installmentRepository = installmentRepository;
        this.applicationRepository = applicationRepository;
        this.milestoneService = milestoneService;
        this.auditLogService = auditLogService;
    }

    public DisbursementPlan createPlan(
            Application application,
            Double totalAmount,
            Integer numInstallments) {

        Objects.requireNonNull(
                application,
                "Application cannot be null"
        );

        Objects.requireNonNull(
                totalAmount,
                "Total amount cannot be null"
        );

        Objects.requireNonNull(
                numInstallments,
                "Number of installments cannot be null"
        );

        if (totalAmount <= 0) {
            throw new IllegalArgumentException(
                    "Total amount must be greater than zero"
            );
        }

        if (numInstallments <= 0) {
            throw new IllegalArgumentException(
                    "Number of installments must be greater than zero"
            );
        }

        DisbursementPlan plan =
                new DisbursementPlan();

        plan.setApplication(application);
        plan.setTotalGrantAmount(totalAmount);
        plan.setNumberOfInstallments(numInstallments);
        plan.setCreatedDate(LocalDate.now());
        plan.setStatus("Active");

        plan = planRepository.save(plan);

        double baseAmount =
                Math.floor(
                        (totalAmount / numInstallments) * 100
                ) / 100.0;

        double allocatedSoFar = 0;

        for (int i = 1; i <= numInstallments; i++) {

            double installmentAmount;

            if (i == numInstallments) {

                installmentAmount =
                        Math.round(
                                (totalAmount - allocatedSoFar) * 100
                        ) / 100.0;

            } else {

                installmentAmount = baseAmount;

                allocatedSoFar += baseAmount;
            }

            // Create compliance milestone
            String milestoneType =
                    milestoneTypeForInstallment(i);

            ComplianceMilestone milestone =
                    milestoneService.createMilestone(
                            application,
                            milestoneType
                    );

            // Create installment
            DisbursementInstallment installment =
                    new DisbursementInstallment();

            installment.setDisbursementPlan(plan);

            installment.setMilestone(milestone);

            installment.setInstallmentNumber(i);

            installment.setInstallmentAmount(
                    installmentAmount
            );

            // Scheduled release date follows milestone due date
            installment.setScheduledDate(
                    milestone.getDueDate()
            );

            installment.setStatus("Scheduled");

            installmentRepository.save(installment);
        }

        // Update application status
        application.setStatus(
                "Disbursement In Progress"
        );

        applicationRepository.save(application);

        return plan;
    }

    private String milestoneTypeForInstallment(
            int installmentNumber) {

        if (installmentNumber == 1) {
            return "Documentation";
        }

        if (installmentNumber == 2) {
            return "Ground Verification";
        }

        return "Utilization Proof";
    }

    public DisbursementInstallment releaseInstallmentIfMilestoneComplete(
            Long installmentId
    ) {

        Long id =
                Objects.requireNonNull(
                        installmentId,
                        "Installment ID cannot be null"
                );

        DisbursementInstallment installment =
                installmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Installment not found with ID: "
                                                + id
                                )
                        );

        // Check milestone exists
        if (installment.getMilestone() == null) {

            throw new IllegalStateException(
                    "Installment has no linked milestone"
            );
        }

        // Check milestone is completed
        if (!"Completed".equalsIgnoreCase(
                installment.getMilestone().getStatus()
        )) {

            throw new IllegalStateException(
                    "Milestone not yet completed"
            );
        }

        // Prevent duplicate release
        if ("Released".equalsIgnoreCase(
                installment.getStatus()
        )) {

            throw new IllegalStateException(
                    "Installment is already released"
            );
        }

        // Store old status before changing it
        String oldStatus =
                installment.getStatus();

        // Release installment
        installment.setStatus("Released");

        installment.setActualReleaseDate(
                LocalDate.now()
        );

        installmentRepository.save(installment);

        // =========================
        // AUDIT LOG - RELEASE
        // =========================
        auditLogService.log(
                "RELEASE",
                "DISBURSEMENT_INSTALLMENT",
                id,
                oldStatus,
                "Released"
        );

        // Check whether all installments are released
        checkAndUpdatePlanCompletion(
                installment.getDisbursementPlan()
                        .getPlanId()
        );

        return installment;
    }

    private void checkAndUpdatePlanCompletion(
            Long planId
    ) {

        Long id =
                Objects.requireNonNull(
                        planId,
                        "Plan ID cannot be null"
                );

        DisbursementPlan plan =
                planRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Plan not found with ID: "
                                                + id
                                )
                        );

        List<DisbursementInstallment> installments =
                installmentRepository
                        .findByDisbursementPlanPlanId(id);

        boolean allReleased =
                !installments.isEmpty()
                        && installments.stream()
                        .allMatch(inst ->
                                "Released".equalsIgnoreCase(
                                        inst.getStatus()
                                )
                        );

        if (allReleased) {

            // =========================
            // COMPLETE DISBURSEMENT PLAN
            // =========================
            plan.setStatus("Completed");

            planRepository.save(plan);

            // =========================
            // COMPLETE APPLICATION
            // =========================
            Application application =
                    plan.getApplication();

            if (application != null) {

                application.setStatus(
                        "Completed"
                );

                applicationRepository.save(
                        application
                );
            }
        }
    }
}