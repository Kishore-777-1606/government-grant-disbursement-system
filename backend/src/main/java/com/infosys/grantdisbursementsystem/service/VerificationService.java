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

    // Applications requesting this much or more are always escalated
    // to District Officer for extra scrutiny.
    public static final double HIGH_VALUE_THRESHOLD = 50000.0;

    private final VerificationRepository verificationRepository;

    private final ApplicationRepository applicationRepository;

    private final FinanceApprovalRepository financeApprovalRepository;

    // Audit Log Service
    private final AuditLogService auditLogService;

    public VerificationService(
            VerificationRepository verificationRepository,
            ApplicationRepository applicationRepository,
            FinanceApprovalRepository financeApprovalRepository,
            AuditLogService auditLogService
    ) {

        this.verificationRepository = verificationRepository;
        this.applicationRepository = applicationRepository;
        this.financeApprovalRepository = financeApprovalRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Creates the FIRST verification-stage record for an application.
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

        }
        else if (score < 80 && !highValue) {

            application.setStatus("Field Verification Pending");

            verification.setVerifiedBy("Field Officer");

            verification.setVerificationStatus("Pending");

            verification.setRemarks(
                    "Waiting for Field Officer Verification"
            );

        }
        else {

            application.setStatus("District Verification Pending");

            verification.setVerifiedBy("District Officer");

            verification.setVerificationStatus("Pending");

            verification.setRemarks(
                    score < 80
                            ?
                            "Waiting for District Officer Verification "
                                    + "(escalated: high-value application)"
                            :
                            "Waiting for District Officer Verification"
            );
        }

        applicationRepository.save(application);

        Verification savedVerification =
                verificationRepository.save(verification);

        // =========================
        // AUDIT LOG - CREATE
        // =========================
        auditLogService.log(
                "CREATE",
                "VERIFICATION",
                savedVerification.getVerificationId(),
                null,
                savedVerification.getVerificationStatus()
        );

        return savedVerification;
    }

    public List<Verification> getAllVerifications() {

        return verificationRepository.findAll();
    }

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

    /**
     * Approves the current verification stage.
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

        String officer =
                verification.getVerifiedBy();

        // Store old status before changing it
        String oldStatus =
                verification.getVerificationStatus();

        if ("Field Officer".equalsIgnoreCase(officer)) {

            // Close Field Officer stage
            verification.setVerificationStatus(
                    "Approved"
            );

            verification.setRemarks(
                    remarks
            );

            verificationRepository.save(verification);

            // =========================
            // AUDIT LOG - APPROVE
            // =========================
            auditLogService.log(
                    "APPROVE",
                    "VERIFICATION",
                    id,
                    oldStatus,
                    "Approved"
            );

            application.setStatus(
                    "District Verification Pending"
            );

            // Create District Officer stage
            Verification nextStage =
                    new Verification();

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

            return verificationRepository.save(nextStage);
        }

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

                approval.setApplication(application);

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

                financeApprovalRepository.save(approval);
            }
        }

        applicationRepository.save(application);

        Verification savedVerification =
                verificationRepository.save(verification);

        // =========================
        // AUDIT LOG - APPROVE
        // =========================
        auditLogService.log(
                "APPROVE",
                "VERIFICATION",
                id,
                oldStatus,
                "Approved"
        );

        return savedVerification;
    }

    public Verification rejectVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

        // Store old status
        String oldStatus =
                verification.getVerificationStatus();

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

        Verification savedVerification =
                verificationRepository.save(verification);

        // =========================
        // AUDIT LOG - REJECT
        // =========================
        auditLogService.log(
                "REJECT",
                "VERIFICATION",
                id,
                oldStatus,
                "Rejected"
        );

        return savedVerification;
    }

    /**
     * Sends the application back for re-verification.
     */
    public Verification sendForReVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

        // Store old status
        String oldStatus =
                verification.getVerificationStatus();

        // Preserve current verification row
        verification.setVerificationStatus(
                "Sent Back"
        );

        verification.setRemarks(
                remarks
        );

        verificationRepository.save(verification);

        // =========================
        // AUDIT LOG - REVERIFY
        // =========================
        auditLogService.log(
                "REVERIFY",
                "VERIFICATION",
                id,
                oldStatus,
                "Sent Back"
        );

        Application application =
                Objects.requireNonNull(
                        verification.getApplication()
                );

        application.setStatus(
                "Re-Verification Pending"
        );

        applicationRepository.save(application);

        // Create fresh Field Officer stage
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
                "Re-verification requested: " + remarks
        );

        return verificationRepository.save(
                reVerificationStage
        );
    }

    public List<Verification> getPendingVerifications() {

        return verificationRepository
                .findByVerificationStatus("Pending");
    }

    /**
     * Full audit trail for one application.
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

            String oldRemarks =
                    verification.getRemarks();

            String newRemarks =
                    "Escalated to higher officer due to delay";

            verification.setRemarks(
                    newRemarks
            );

            verificationRepository.save(
                    verification
            );

            // =========================
            // AUDIT LOG - ESCALATE
            // =========================
            auditLogService.log(
                    "ESCALATE",
                    "VERIFICATION",
                    verificationId,
                    oldRemarks,
                    newRemarks
            );
        }
    }
}