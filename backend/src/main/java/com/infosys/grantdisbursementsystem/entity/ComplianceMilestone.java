package com.infosys.grantdisbursementsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "compliance_milestones")
public class ComplianceMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long milestoneId;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    private String milestoneType;
    private LocalDate dueDate;
    private String status;
    private LocalDate completedDate;
    private String remarks;

    public ComplianceMilestone() {}

    public Long getMilestoneId() { return milestoneId; }
    public void setMilestoneId(Long milestoneId) { this.milestoneId = milestoneId; }
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
    public String getMilestoneType() { return milestoneType; }
    public void setMilestoneType(String milestoneType) { this.milestoneType = milestoneType; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}