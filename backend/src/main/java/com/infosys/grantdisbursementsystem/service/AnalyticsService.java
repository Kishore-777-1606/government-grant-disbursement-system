package com.infosys.grantdisbursementsystem.service;
import com.infosys.grantdisbursementsystem.dto.ApplicationSummaryDTO;
import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.dto.DashboardSummaryDTO;
import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;
import com.infosys.grantdisbursementsystem.entity.DisbursementInstallment;
import com.infosys.grantdisbursementsystem.entity.DisbursementPlan;
import com.infosys.grantdisbursementsystem.repository.BeneficiaryRepository;
import com.infosys.grantdisbursementsystem.dto.DisbursementSummaryDTO;
import com.infosys.grantdisbursementsystem.entity.DisbursementPlan;
import com.infosys.grantdisbursementsystem.entity.DisbursementInstallment;
import com.infosys.grantdisbursementsystem.dto.MilestoneSummaryDTO;
import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;

import java.time.LocalDate;

import java.util.List;

import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.ComplianceMilestoneRepository;
import com.infosys.grantdisbursementsystem.repository.DisbursementInstallmentRepository;
import com.infosys.grantdisbursementsystem.repository.DisbursementPlanRepository;
import com.infosys.grantdisbursementsystem.repository.SchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    @Autowired
private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private DisbursementPlanRepository disbursementPlanRepository;

    @Autowired
    private DisbursementInstallmentRepository installmentRepository;

    @Autowired
    private ComplianceMilestoneRepository milestoneRepository;

    public ApplicationSummaryDTO getApplicationSummary() {

        List<Application> applications = applicationRepository.findAll();

        long total = applications.size();

        long approved = applications.stream()
                .filter(a -> "Approved".equalsIgnoreCase(a.getStatus()))
                .count();

        long pending = applications.stream()
                .filter(a -> "Pending".equalsIgnoreCase(a.getStatus()))
                .count();

        long rejected = applications.stream()
                .filter(a -> "Rejected".equalsIgnoreCase(a.getStatus()))
                .count();

        return new ApplicationSummaryDTO(
                total,
                approved,
                pending,
                rejected
        );
    }
    public DashboardSummaryDTO getDashboardSummary() {

    long beneficiaries = beneficiaryRepository.count();
    long schemes = schemeRepository.count();
    long applications = applicationRepository.count();

    long activePlans = disbursementPlanRepository.findAll().stream()
            .filter(p -> "Active".equalsIgnoreCase(p.getStatus()))
            .count();

    long completedPlans = disbursementPlanRepository.findAll().stream()
            .filter(p -> "Completed".equalsIgnoreCase(p.getStatus()))
            .count();

    long pendingMilestones = milestoneRepository.findAll().stream()
            .filter(m -> "Pending".equalsIgnoreCase(m.getStatus()))
            .count();

    long releasedInstallments = installmentRepository.findAll().stream()
            .filter(i -> "Released".equalsIgnoreCase(i.getStatus()))
            .count();

    return new DashboardSummaryDTO(
            beneficiaries,
            schemes,
            applications,
            activePlans,
            completedPlans,
            pendingMilestones,
            releasedInstallments
    );
}
public DisbursementSummaryDTO getDisbursementSummary() {

    List<DisbursementPlan> plans = disbursementPlanRepository.findAll();
    List<DisbursementInstallment> installments = installmentRepository.findAll();

    long totalPlans = plans.size();

    long activePlans = plans.stream()
            .filter(plan -> "Active".equalsIgnoreCase(plan.getStatus()))
            .count();

    long completedPlans = plans.stream()
            .filter(plan -> "Completed".equalsIgnoreCase(plan.getStatus()))
            .count();

    long totalInstallments = installments.size();

    long releasedInstallments = installments.stream()
            .filter(i -> "Released".equalsIgnoreCase(i.getStatus()))
            .count();

    double totalGrantAmount = plans.stream()
            .mapToDouble(plan -> plan.getTotalGrantAmount() != null ? plan.getTotalGrantAmount() : 0.0)
            .sum();

    double releasedAmount = installments.stream()
            .filter(i -> "Released".equalsIgnoreCase(i.getStatus()))
            .mapToDouble(i -> i.getInstallmentAmount() != null ? i.getInstallmentAmount() : 0.0)
            .sum();

    return new DisbursementSummaryDTO(
            totalPlans,
            activePlans,
            completedPlans,
            totalInstallments,
            releasedInstallments,
            totalGrantAmount,
            releasedAmount
    );
}
public MilestoneSummaryDTO getMilestoneSummary() {

    List<ComplianceMilestone> milestones = milestoneRepository.findAll();

    long total = milestones.size();

    long pending = milestones.stream()
            .filter(m -> "Pending".equalsIgnoreCase(m.getStatus()))
            .count();

    long completed = milestones.stream()
            .filter(m -> "Completed".equalsIgnoreCase(m.getStatus()))
            .count();

    long overdue = milestones.stream()
            .filter(m ->
                    "Pending".equalsIgnoreCase(m.getStatus())
                            && m.getDueDate() != null
                            && m.getDueDate().isBefore(LocalDate.now()))
            .count();

    return new MilestoneSummaryDTO(
            total,
            pending,
            completed,
            overdue
    );
}

}   