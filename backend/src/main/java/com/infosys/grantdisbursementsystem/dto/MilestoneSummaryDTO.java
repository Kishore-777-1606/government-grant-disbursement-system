package com.infosys.grantdisbursementsystem.dto;

public class MilestoneSummaryDTO {

    private long totalMilestones;
    private long pendingMilestones;
    private long completedMilestones;
    private long overdueMilestones;

    public MilestoneSummaryDTO() {
    }

    public MilestoneSummaryDTO(long totalMilestones,
                               long pendingMilestones,
                               long completedMilestones,
                               long overdueMilestones) {
        this.totalMilestones = totalMilestones;
        this.pendingMilestones = pendingMilestones;
        this.completedMilestones = completedMilestones;
        this.overdueMilestones = overdueMilestones;
    }

    public long getTotalMilestones() {
        return totalMilestones;
    }

    public void setTotalMilestones(long totalMilestones) {
        this.totalMilestones = totalMilestones;
    }

    public long getPendingMilestones() {
        return pendingMilestones;
    }

    public void setPendingMilestones(long pendingMilestones) {
        this.pendingMilestones = pendingMilestones;
    }

    public long getCompletedMilestones() {
        return completedMilestones;
    }

    public void setCompletedMilestones(long completedMilestones) {
        this.completedMilestones = completedMilestones;
    }

    public long getOverdueMilestones() {
        return overdueMilestones;
    }

    public void setOverdueMilestones(long overdueMilestones) {
        this.overdueMilestones = overdueMilestones;
    }
}