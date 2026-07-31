package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.*;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DisbursementPlanService {

    @Autowired private DisbursementPlanRepository planRepository;
    @Autowired private DisbursementInstallmentRepository installmentRepository;
    @Autowired private ComplianceMilestoneRepository milestoneRepository; // Ravi's repository

    // Creates a plan AND its installments AND a linked milestone for each installment
    public DisbursementPlan createPlan(Application application, Double totalAmount, Integer numInstallments) {

        DisbursementPlan plan = new DisbursementPlan();
        plan.setApplication(application);
        plan.setTotalGrantAmount(totalAmount);
        plan.setNumberOfInstallments(numInstallments);
        plan.setCreatedDate(LocalDate.now());
        plan.setStatus("Active");
        plan = planRepository.save(plan);

        // Split amount evenly, but the LAST installment absorbs any rounding difference
        // so the total always adds up exactly to totalAmount.
        double baseAmount = Math.floor((totalAmount / numInstallments) * 100) / 100.0;
        double allocatedSoFar = 0;

        for (int i = 1; i <= numInstallments; i++) {

            double thisInstallmentAmount;
            if (i == numInstallments) {
                // last installment gets whatever remains, avoiding rounding loss
                thisInstallmentAmount = totalAmount - allocatedSoFar;
            } else {
                thisInstallmentAmount = baseAmount;
                allocatedSoFar += baseAmount;
            }

            // Create the linked compliance milestone for this installment
            ComplianceMilestone milestone = new ComplianceMilestone();
            milestone.setApplication(application);
            milestone.setMilestoneType(milestoneTypeForInstallment(i));
            milestone.setStatus("Pending");
            milestone.setDueDate(LocalDate.now().plusDays(30L * i));
            milestone = milestoneRepository.save(milestone);

            DisbursementInstallment installment = new DisbursementInstallment();
            installment.setDisbursementPlan(plan);
            installment.setMilestone(milestone);
            installment.setInstallmentNumber(i);
            installment.setInstallmentAmount(thisInstallmentAmount);
            installment.setScheduledDate(LocalDate.now().plusDays(30L * i));
            installment.setStatus("Scheduled");
            installmentRepository.save(installment);
        }

        return plan;
    }

    // Simple rule: first installment tied to Documentation, second to Ground
    // Verification, third onward to Utilization Proof. Adjust if your team
    // decides on a different mapping.
    private String milestoneTypeForInstallment(int installmentNumber) {
        if (installmentNumber == 1) return "Documentation";
        if (installmentNumber == 2) return "Ground Verification";
        return "Utilization Proof";
    }

    // Releases an installment ONLY if its linked milestone is completed.
    // Also updates the parent plan to "Completed" if this was the last one.
    public DisbursementInstallment releaseInstallmentIfMilestoneComplete(Long installmentId) {

        DisbursementInstallment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found with ID: " + installmentId));

        if (installment.getMilestone() == null) {
            throw new IllegalStateException("Installment has no linked milestone — cannot check compliance");
        }

        if (!"Completed".equalsIgnoreCase(installment.getMilestone().getStatus())) {
            throw new IllegalStateException("Milestone not yet completed — cannot release funds");
        }

        installment.setStatus("Released");
        installment.setActualReleaseDate(LocalDate.now());
        installmentRepository.save(installment);

        checkAndUpdatePlanCompletion(installment.getDisbursementPlan().getPlanId());

        return installment;
    }

    private void checkAndUpdatePlanCompletion(Long planId) {
        DisbursementPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + planId));

        var installments = installmentRepository.findByDisbursementPlanPlanId(planId);
        boolean allReleased = installments.stream()
                .allMatch(inst -> "Released".equalsIgnoreCase(inst.getStatus()));

        if (allReleased) {
            plan.setStatus("Completed");
            planRepository.save(plan);
        }
    }
}
