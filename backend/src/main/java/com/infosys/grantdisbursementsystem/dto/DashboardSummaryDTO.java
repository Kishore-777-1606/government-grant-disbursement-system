package com.infosys.grantdisbursementsystem.dto;

public class DashboardSummaryDTO {

    private long totalBeneficiaries;
    private long totalSchemes;
    private long totalApplications;
    private long activeDisbursementPlans;
    private long completedDisbursementPlans;
    private long pendingMilestones;
    private long releasedInstallments;

    public DashboardSummaryDTO() {
    }

    public DashboardSummaryDTO(long totalBeneficiaries,
                               long totalSchemes,
                               long totalApplications,
                               long activeDisbursementPlans,
                               long completedDisbursementPlans,
                               long pendingMilestones,
                               long releasedInstallments) {
        this.totalBeneficiaries = totalBeneficiaries;
        this.totalSchemes = totalSchemes;
        this.totalApplications = totalApplications;
        this.activeDisbursementPlans = activeDisbursementPlans;
        this.completedDisbursementPlans = completedDisbursementPlans;
        this.pendingMilestones = pendingMilestones;
        this.releasedInstallments = releasedInstallments;
    }

    public long getTotalBeneficiaries() {
        return totalBeneficiaries;
    }

    public void setTotalBeneficiaries(long totalBeneficiaries) {
        this.totalBeneficiaries = totalBeneficiaries;
    }

    public long getTotalSchemes() {
        return totalSchemes;
    }

    public void setTotalSchemes(long totalSchemes) {
        this.totalSchemes = totalSchemes;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getActiveDisbursementPlans() {
        return activeDisbursementPlans;
    }

    public void setActiveDisbursementPlans(long activeDisbursementPlans) {
        this.activeDisbursementPlans = activeDisbursementPlans;
    }

    public long getCompletedDisbursementPlans() {
        return completedDisbursementPlans;
    }

    public void setCompletedDisbursementPlans(long completedDisbursementPlans) {
        this.completedDisbursementPlans = completedDisbursementPlans;
    }

    public long getPendingMilestones() {
        return pendingMilestones;
    }

    public void setPendingMilestones(long pendingMilestones) {
        this.pendingMilestones = pendingMilestones;
    }

    public long getReleasedInstallments() {
        return releasedInstallments;
    }

    public void setReleasedInstallments(long releasedInstallments) {
        this.releasedInstallments = releasedInstallments;
    }
}