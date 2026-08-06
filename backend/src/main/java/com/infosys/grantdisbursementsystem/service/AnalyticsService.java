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
import com.infosys.grantdisbursementsystem.dto.RegionUtilizationDTO;
import com.infosys.grantdisbursementsystem.dto.CategoryDistributionDTO;
import com.infosys.grantdisbursementsystem.dto.FundUtilizationDTO;
import com.infosys.grantdisbursementsystem.dto.BudgetExhaustionDTO;
import com.infosys.grantdisbursementsystem.dto.RecentActivityDTO;
import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.entity.Scheme;
import java.util.*;

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
// Region-wise fund utilization (region = districtId, since no region
    // name lookup table exists yet — flagged as a known simplification)
    public List<RegionUtilizationDTO> getRegionWiseFundUtilization() {
        Map<String, Double> totals = new LinkedHashMap<>();

        for (DisbursementInstallment inst : installmentRepository.findAll()) {
            if (!"Released".equalsIgnoreCase(inst.getStatus())) continue;

            DisbursementPlan plan = inst.getDisbursementPlan();
            if (plan == null || plan.getApplication() == null) continue;

            Long beneficiaryId = plan.getApplication().getBeneficiaryId();
            if (beneficiaryId == null) continue;

            Beneficiary b = beneficiaryRepository.findById(beneficiaryId).orElse(null);
            if (b == null || b.getDistrictId() == null) continue;

            String region = "District " + b.getDistrictId();
            double amount = inst.getInstallmentAmount() != null ? inst.getInstallmentAmount() : 0.0;
            totals.merge(region, amount, Double::sum);
        }

        List<RegionUtilizationDTO> result = new ArrayList<>();
        for (Map.Entry<String, Double> e : totals.entrySet()) {
            result.add(new RegionUtilizationDTO(e.getKey(), e.getValue()));
        }
        return result;
    }

    // Category-wise distribution (category = Scheme.schemeType, since
    // Beneficiary/Application have no category field yet — known simplification)
    public List<CategoryDistributionDTO> getCategoryWiseDistribution() {
        Map<String, Long> counts = new LinkedHashMap<>();

        for (Application app : applicationRepository.findAll()) {
            if (app.getSchemeId() == null) continue;
            Scheme scheme = schemeRepository.findById(app.getSchemeId()).orElse(null);
            String category = (scheme != null && scheme.getSchemeType() != null)
                    ? scheme.getSchemeType() : "Unclassified";
            counts.merge(category, 1L, Long::sum);
        }

        List<CategoryDistributionDTO> result = new ArrayList<>();
        for (Map.Entry<String, Long> e : counts.entrySet()) {
            result.add(new CategoryDistributionDTO(e.getKey(), e.getValue()));
        }
        return result;
    }

    // Scheme-wise fund utilization: total planned vs released vs remaining
    public List<FundUtilizationDTO> getSchemeWiseFundUtilization() {
        Map<Long, Double> totalBySchemeId = new HashMap<>();
        Map<Long, Double> releasedBySchemeId = new HashMap<>();
        Map<Long, String> schemeNameById = new LinkedHashMap<>();

        for (DisbursementPlan plan : disbursementPlanRepository.findAll()) {
            Application app = plan.getApplication();
            if (app == null || app.getSchemeId() == null) continue;
            Long schemeId = app.getSchemeId();

            double total = plan.getTotalGrantAmount() != null ? plan.getTotalGrantAmount() : 0.0;
            totalBySchemeId.merge(schemeId, total, Double::sum);

            schemeNameById.computeIfAbsent(schemeId, id ->
                    schemeRepository.findById(id).map(Scheme::getName).orElse("Unknown Scheme"));
        }

        for (DisbursementInstallment inst : installmentRepository.findAll()) {
            if (!"Released".equalsIgnoreCase(inst.getStatus())) continue;
            DisbursementPlan plan = inst.getDisbursementPlan();
            if (plan == null || plan.getApplication() == null) continue;
            Long schemeId = plan.getApplication().getSchemeId();
            if (schemeId == null) continue;

            double amount = inst.getInstallmentAmount() != null ? inst.getInstallmentAmount() : 0.0;
            releasedBySchemeId.merge(schemeId, amount, Double::sum);
        }

        List<FundUtilizationDTO> result = new ArrayList<>();
        for (Map.Entry<Long, String> e : schemeNameById.entrySet()) {
            double total = totalBySchemeId.getOrDefault(e.getKey(), 0.0);
            double released = releasedBySchemeId.getOrDefault(e.getKey(), 0.0);
            result.add(new FundUtilizationDTO(e.getValue(), total, released, total - released));
        }
        return result;
    }

    // Budget exhaustion % per scheme (derived from the same data as above)
    public List<BudgetExhaustionDTO> getBudgetExhaustion() {
        List<BudgetExhaustionDTO> result = new ArrayList<>();
        for (FundUtilizationDTO dto : getSchemeWiseFundUtilization()) {
            double pct = (dto.getTotalAmount() != null && dto.getTotalAmount() > 0)
                    ? (dto.getReleasedAmount() / dto.getTotalAmount()) * 100.0 : 0.0;
            result.add(new BudgetExhaustionDTO(dto.getSchemeName(), Math.round(pct * 100.0) / 100.0));
        }
        return result;
    }

    // Recent activity feed: plan creations, milestone completions, installment
    // releases, merged and sorted by date, most recent 10
    public List<RecentActivityDTO> getRecentActivities() {
        List<RecentActivityDTO> activities = new ArrayList<>();

        for (DisbursementPlan plan : disbursementPlanRepository.findAll()) {
            if (plan.getCreatedDate() == null) continue;
            String appId = plan.getApplication() != null
                    ? String.valueOf(plan.getApplication().getApplicationId()) : "N/A";
            activities.add(new RecentActivityDTO(
                    "Disbursement plan created for Application #" + appId,
                    "PLAN_CREATED", plan.getCreatedDate()));
        }

        for (DisbursementInstallment inst : installmentRepository.findAll()) {
            if (!"Released".equalsIgnoreCase(inst.getStatus()) || inst.getActualReleaseDate() == null) continue;
            activities.add(new RecentActivityDTO(
                    "Installment #" + inst.getInstallmentNumber() + " released (₹" + inst.getInstallmentAmount() + ")",
                    "INSTALLMENT_RELEASED", inst.getActualReleaseDate()));
        }

        for (ComplianceMilestone m : milestoneRepository.findAll()) {
            if (!"Completed".equalsIgnoreCase(m.getStatus()) || m.getCompletedDate() == null) continue;
            activities.add(new RecentActivityDTO(
                    m.getMilestoneType() + " milestone completed", "MILESTONE_COMPLETED", m.getCompletedDate()));
        }

        activities.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return activities.size() > 10 ? activities.subList(0, 10) : activities;
    }
}   