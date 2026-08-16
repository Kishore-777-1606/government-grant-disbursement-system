package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.entity.Verification;

import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;

import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.FinanceApprovalRepository;
import com.infosys.grantdisbursementsystem.repository.VerificationRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class VerificationService {

    public static final double HIGH_VALUE_THRESHOLD = 50000.0;

    private final VerificationRepository verificationRepository;
    private final ApplicationRepository applicationRepository;
    private final FinanceApprovalRepository financeApprovalRepository;

    public VerificationService(
            VerificationRepository verificationRepository,
            ApplicationRepository applicationRepository,
            FinanceApprovalRepository financeApprovalRepository
    ) {
        this.verificationRepository = verificationRepository;
        this.applicationRepository = applicationRepository;
        this.financeApprovalRepository = financeApprovalRepository;
    }

    // ============================================================
    // CREATE VERIFICATION
    // ============================================================

    public Verification createVerification(
            @NonNull Long applicationId,
            String officerRole
    ) {

        Application application =
                applicationRepository.findById(
                                Objects.requireNonNull(applicationId)
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with ID: "
                                                + applicationId
                                )
                        );

        Verification verification = new Verification();

        verification.setApplication(application);

        verification.setVerificationDate(
                LocalDate.now()
        );

        double score =
                application.getEligibilityScore() != null
                        ? application.getEligibilityScore()
                        : 0;

        double amount =
                application.getAppliedAmount() != null
                        ? application.getAppliedAmount().doubleValue()
                        : 0;

        boolean highValue = amount >= HIGH_VALUE_THRESHOLD;

        if (score < 60) {

            application.setStatus("Rejected");

            verification.setVerifiedBy("System");

            verification.setVerificationStatus("Rejected");

            verification.setRemarks(
                    "Application rejected due to low eligibility score"
            );

        } else if (score < 80 && !highValue) {

            application.setStatus("Field Verification Pending");

            verification.setVerifiedBy("Field Officer");

            verification.setVerificationStatus("Pending");

            verification.setRemarks(
                    "Waiting for Field Officer Verification"
            );

        } else {

            application.setStatus("District Verification Pending");

            verification.setVerifiedBy("District Officer");

            verification.setVerificationStatus("Pending");

            verification.setRemarks(
                    score < 80
                            ? "Waiting for District Officer Verification "
                              + "(escalated: high-value application)"
                            : "Waiting for District Officer Verification"
            );
        }

        applicationRepository.save(application);

        return verificationRepository.save(verification);
    }

    // ============================================================
    // GET ALL
    // ============================================================

    public List<Verification> getAllVerifications() {

        return verificationRepository.findAll();
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    public Verification getVerificationById(
            @NonNull Long id
    ) {

        return verificationRepository.findById(
                        Objects.requireNonNull(id)
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Verification not found with ID: "
                                        + id
                        )
                );
    }

    // ============================================================
    // APPROVE
    // ============================================================

    public Verification approveVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

        Application application =
                Objects.requireNonNull(
                        verification.getApplication()
                );

        String officer =
                verification.getVerifiedBy();

        if ("Field Officer".equalsIgnoreCase(officer)) {

            verification.setVerificationStatus(
                    "Approved"
            );

            verification.setRemarks(
                    remarks
            );

            verificationRepository.save(
                    verification
            );

            application.setStatus(
                    "District Verification Pending"
            );

            Verification nextStage =
                    new Verification();

            nextStage.setApplication(
                    application
            );

            nextStage.setVerificationDate(
                    LocalDate.now()
            );

            nextStage.setVerifiedBy(
                    "District Officer"
            );

            nextStage.setVerificationStatus(
                    "Pending"
            );

            nextStage.setRemarks(
                    "Waiting for District Officer Verification"
            );

            applicationRepository.save(
                    application
            );

            return verificationRepository.save(
                    nextStage
            );

        } else if ("District Officer".equalsIgnoreCase(officer)) {

            application.setStatus(
                    "Finance Approval Pending"
            );

            verification.setVerificationStatus(
                    "Approved"
            );

            verification.setRemarks(
                    remarks
            );

            if (financeApprovalRepository
                    .findByApplication(application)
                    .isEmpty()) {

                FinanceApproval approval =
                        new FinanceApproval();

                approval.setApplication(
                        application
                );

                approval.setApprovedBy(
                        "Finance Officer"
                );

                approval.setApprovalStatus(
                        "Pending"
                );

                approval.setApprovalDate(
                        LocalDate.now()
                );

                approval.setRemarks(
                        "Waiting for Finance Approval"
                );

                financeApprovalRepository.save(
                        approval
                );
            }
        }

        applicationRepository.save(
                application
        );

        return verificationRepository.save(
                verification
        );
    }

    // ============================================================
    // REJECT
    // ============================================================

    public Verification rejectVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

        verification.setVerificationStatus(
                "Rejected"
        );

        verification.setRemarks(
                remarks
        );

        Application application =
                Objects.requireNonNull(
                        verification.getApplication()
                );

        application.setStatus(
                "Rejected"
        );

        applicationRepository.save(
                application
        );

        return verificationRepository.save(
                verification
        );
    }

    // ============================================================
    // RE-VERIFICATION
    // ============================================================

    public Verification sendForReVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

        verification.setVerificationStatus(
                "Sent Back"
        );

        verification.setRemarks(
                remarks
        );

        verificationRepository.save(
                verification
        );

        Application application =
                Objects.requireNonNull(
                        verification.getApplication()
                );

        application.setStatus(
                "Re-Verification Pending"
        );

        applicationRepository.save(
                application
        );

        Verification reVerificationStage =
                new Verification();

        reVerificationStage.setApplication(
                application
        );

        reVerificationStage.setVerificationDate(
                LocalDate.now()
        );

        reVerificationStage.setVerifiedBy(
                "Field Officer"
        );

        reVerificationStage.setVerificationStatus(
                "Pending"
        );

        reVerificationStage.setRemarks(
                "Re-verification requested: "
                        + remarks
        );

        return verificationRepository.save(
                reVerificationStage
        );
    }

    // ============================================================
    // GET PENDING
    // ============================================================

    public List<Verification> getPendingVerifications() {

        return verificationRepository
                .findByVerificationStatus(
                        "Pending"
                );
    }

    // ============================================================
    // GET HISTORY
    // ============================================================

    public List<Verification> getVerificationHistory(
            @NonNull Long applicationId
    ) {

        Application application =
                applicationRepository.findById(
                                Objects.requireNonNull(
                                        applicationId
                                )
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with ID: "
                                                + applicationId
                                )
                        );

        return verificationRepository
                .findByApplicationOrderByVerificationIdAsc(
                        application
                );
    }

    // ============================================================
    // ESCALATION
    // ============================================================

    public void checkEscalation(
            @NonNull Long verificationId
    ) {

        Verification verification =
                getVerificationById(
                        verificationId
                );

        LocalDate today =
                LocalDate.now();

        if (verification.getVerificationDate()
                .plusDays(3)
                .isBefore(today)) {

            verification.setRemarks(
                    "Escalated to higher officer due to delay"
            );

            verificationRepository.save(
                    verification
            );
        }
    }
}