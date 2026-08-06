package com.infosys.grantdisbursementsystem.dto;

public class DisbursementSummaryDTO {

    private long totalPlans;
    private long activePlans;
    private long completedPlans;
    private long totalInstallments;
    private long releasedInstallments;
    private double totalGrantAmount;
    private double releasedAmount;

    public DisbursementSummaryDTO() {
    }

    public DisbursementSummaryDTO(long totalPlans,
                                  long activePlans,
                                  long completedPlans,
                                  long totalInstallments,
                                  long releasedInstallments,
                                  double totalGrantAmount,
                                  double releasedAmount) {
        this.totalPlans = totalPlans;
        this.activePlans = activePlans;
        this.completedPlans = completedPlans;
        this.totalInstallments = totalInstallments;
        this.releasedInstallments = releasedInstallments;
        this.totalGrantAmount = totalGrantAmount;
        this.releasedAmount = releasedAmount;
    }

    public long getTotalPlans() { return totalPlans; }
    public void setTotalPlans(long totalPlans) { this.totalPlans = totalPlans; }

    public long getActivePlans() { return activePlans; }
    public void setActivePlans(long activePlans) { this.activePlans = activePlans; }

    public long getCompletedPlans() { return completedPlans; }
    public void setCompletedPlans(long completedPlans) { this.completedPlans = completedPlans; }

    public long getTotalInstallments() { return totalInstallments; }
    public void setTotalInstallments(long totalInstallments) { this.totalInstallments = totalInstallments; }

    public long getReleasedInstallments() { return releasedInstallments; }
    public void setReleasedInstallments(long releasedInstallments) { this.releasedInstallments = releasedInstallments; }

    public double getTotalGrantAmount() { return totalGrantAmount; }
    public void setTotalGrantAmount(double totalGrantAmount) { this.totalGrantAmount = totalGrantAmount; }

    public double getReleasedAmount() { return releasedAmount; }
    public void setReleasedAmount(double releasedAmount) { this.releasedAmount = releasedAmount; }
}