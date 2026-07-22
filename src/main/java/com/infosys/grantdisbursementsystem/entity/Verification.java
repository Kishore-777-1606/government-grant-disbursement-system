package com.infosys.grantdisbursementsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "verifications")
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verificationId;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(nullable = false)
    private String verificationStatus;

    @Column(nullable = false)
    private String verifiedBy;

    @Column(nullable = false)
    private LocalDate verificationDate;

    @Column(length = 500)
    private String remarks;

    // Default Constructor
    public Verification() {
    }

    // Parameterized Constructor
    public Verification(Long verificationId, Application application,
                        String verificationStatus, String verifiedBy,
                        LocalDate verificationDate, String remarks) {
        this.verificationId = verificationId;
        this.application = application;
        this.verificationStatus = verificationStatus;
        this.verifiedBy = verifiedBy;
        this.verificationDate = verificationDate;
        this.remarks = remarks;
    }

    // Getters and Setters

    public Long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDate getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(LocalDate verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}