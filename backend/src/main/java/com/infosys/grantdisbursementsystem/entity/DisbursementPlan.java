package com.infosys.grantdisbursementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "disbursement_plans")
public class DisbursementPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Application application;

    @Column(name = "total_grant_amount")
    private Double totalGrantAmount;

    @Column(name = "number_of_installments")
    private Integer numberOfInstallments;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "status")
    private String status;

    public DisbursementPlan() {
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(
            Application application) {

        this.application = application;
    }

    public Double getTotalGrantAmount() {
        return totalGrantAmount;
    }

    public void setTotalGrantAmount(
            Double totalGrantAmount) {

        this.totalGrantAmount = totalGrantAmount;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(
            Integer numberOfInstallments) {

        this.numberOfInstallments = numberOfInstallments;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(
            LocalDate createdDate) {

        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}