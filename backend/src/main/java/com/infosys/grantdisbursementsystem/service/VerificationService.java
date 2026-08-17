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

    // Applications requesting this amount or more are escalated
    // to District Officer for additional scrutiny.
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

    /**
     * Creates the first verification-stage record for an application.
     */
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

        verification.setVerificationDate(LocalDate.now());

        double score =
                application.getEligibilityScore() != null
                        ? application.getEligibilityScore()
                        : 0;

        double amount =
                application.getAppliedAmount() != null
                        ? application.getAppliedAmount().doubleValue()
                        : 0;

        boolean highValue = amount >= HIGH_VALUE_THRESHOLD;

        /*
         * Eligibility workflow:
         *
         * Score < 60
         *      -> Rejected
         *
         * Score 60-79.99
         *      -> Field Officer
         *
         * Score >= 80
         *      -> District Officer
         *
         * High-value application
         *      -> District Officer
         */

        if (score < 60) {

            application.setStatus("Rejected");

            verification.setVerifiedBy("System");

            verification.setVerificationStatus("Rejected");

            verification.setRemarks(
                    "Application rejected due to low eligibility score"
            );

        } else if (score < 80 && !highValue) {

            application.setStatus(
                    "Field Verification Pending"
            );

            verification.setVerifiedBy(
                    "Field Officer"
            );

            verification.setVerificationStatus(
                    "Pending"
            );

            verification.setRemarks(
                    "Waiting for Field Officer Verification"
            );

        } else {

            application.setStatus(
                    "District Verification Pending"
            );

            verification.setVerifiedBy(
                    "District Officer"
            );

            verification.setVerificationStatus(
                    "Pending"
            );

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

    /**
     * Get all verification records.
     */
    public List<Verification> getAllVerifications() {

        return verificationRepository.findAll();
    }

    /**
     * Get verification by ID.
     */
    public Verification getVerificationById(
            @NonNull Long id
    ) {

        return verificationRepository.findById(
                Objects.requireNonNull(id)
        )
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Verification not found with ID: " + id
                )
        );
    }

    /**
     * Approve the current verification stage.
     */
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

        String officer = verification.getVerifiedBy();

        /*
         * FIELD OFFICER APPROVAL
         */
        if ("Field Officer".equalsIgnoreCase(officer)) {

            verification.setVerificationStatus(
                    "Approved"
            );

            verification.setRemarks(
                    remarks
            );

            verificationRepository.save(verification);

            application.setStatus(
                    "District Verification Pending"
            );

            Verification nextStage = new Verification();

            nextStage.setApplication(application);

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

            applicationRepository.save(application);

            return verificationRepository.save(
                    nextStage
            );
        }

        /*
         * DISTRICT OFFICER APPROVAL
         */
        else if ("District Officer".equalsIgnoreCase(officer)) {

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

        applicationRepository.save(application);

        return verificationRepository.save(
                verification
        );
    }

    /**
     * Reject the current verification stage.
     */
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

        applicationRepository.save(application);

        return verificationRepository.save(
                verification
        );
    }

    /**
     * Send application back for re-verification.
     */
    public Verification sendForReVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

        /*
         * Preserve the current verification record
         * for audit purposes.
         */
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

        /*
         * Create a new Field Officer stage.
         */
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

    /**
     * Get currently actionable verification records.
     *
     * Includes:
     * - Pending
     * - Re-Verification
     *
     * This ensures older Re-Verification records are
     * also visible on the Verification page.
     */
    public List<Verification> getPendingVerifications() {

        return verificationRepository
                .findByVerificationStatusIn(
                        List.of(
                                "Pending",
                                "Re-Verification"
                        )
                );
    }

    /**
     * Get complete verification history for an application.
     */
    public List<Verification> getVerificationHistory(
            @NonNull Long applicationId
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

        return verificationRepository
                .findByApplicationOrderByVerificationIdAsc(
                        application
                );
    }

    /**
     * Check whether verification has been delayed.
     */
    public void checkEscalation(
            @NonNull Long verificationId
    ) {

        Verification verification =
                getVerificationById(verificationId);

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