package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.*;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
            Integer numInstallments
    ) {

        Objects.requireNonNull(application);
        Objects.requireNonNull(totalAmount);
        Objects.requireNonNull(numInstallments);

        DisbursementPlan plan = new DisbursementPlan();

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

        for(int i = 1; i <= numInstallments; i++) {

            double installmentAmount;

            if(i == numInstallments) {

                installmentAmount =
                        totalAmount - allocatedSoFar;

            }
            else {

                installmentAmount = baseAmount;

                allocatedSoFar += baseAmount;
            }

            // Delegates to ComplianceMilestoneService so there is a single,
            // type-aware due-date policy.
            ComplianceMilestone milestone =
                    milestoneService.createMilestone(
                            application,
                            milestoneTypeForInstallment(i)
                    );

            DisbursementInstallment installment =
                    new DisbursementInstallment();

            installment.setDisbursementPlan(plan);

            installment.setMilestone(milestone);

            installment.setInstallmentNumber(i);

            installment.setInstallmentAmount(
                    installmentAmount
            );

            // The scheduled release date follows the milestone's own due
            // date.
            installment.setScheduledDate(
                    milestone.getDueDate()
            );

            installment.setStatus("Scheduled");

            installmentRepository.save(installment);
        }

        application.setStatus(
                "Disbursement In Progress"
        );

        applicationRepository.save(application);

        return plan;
    }

    private String milestoneTypeForInstallment(
            int installmentNumber
    ) {

        if(installmentNumber == 1)
            return "Documentation";

        if(installmentNumber == 2)
            return "Ground Verification";

        return "Utilization Proof";
    }

    public DisbursementInstallment releaseInstallmentIfMilestoneComplete(
            Long installmentId
    ) {

        Long id = Objects.requireNonNull(installmentId);

        DisbursementInstallment installment =
                installmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Installment not found with ID: "
                                                + id
                                )
                        );

        if(installment.getMilestone() == null) {

            throw new IllegalStateException(
                    "Installment has no linked milestone"
            );
        }

        if(!"Completed".equalsIgnoreCase(
                installment.getMilestone().getStatus()
        )) {

            throw new IllegalStateException(
                    "Milestone not yet completed"
            );
        }

        // Store old status before changing it
        String oldStatus =
                installment.getStatus();

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

        checkAndUpdatePlanCompletion(
                installment.getDisbursementPlan()
                        .getPlanId()
        );

        return installment;
    }

    private void checkAndUpdatePlanCompletion(
            Long planId
    ) {

        Long id = Objects.requireNonNull(planId);

        DisbursementPlan plan =
                planRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Plan not found with ID: "
                                                + id
                                )
                        );

        var installments =
                installmentRepository
                        .findByDisbursementPlanPlanId(id);

        boolean allReleased =
                installments.stream()
                        .allMatch(inst ->
                                "Released"
                                        .equalsIgnoreCase(
                                                inst.getStatus()
                                        )
                        );

        if(allReleased) {

            plan.setStatus("Completed");

            planRepository.save(plan);
        }
    }
}