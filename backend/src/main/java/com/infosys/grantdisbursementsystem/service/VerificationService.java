package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.entity.Verification;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.FinanceApprovalRepository;
import com.infosys.grantdisbursementsystem.repository.VerificationRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class VerificationService {

<<<<<<< HEAD
    // Applications requesting this much or more are always escalated
    // to District Officer for extra scrutiny.
=======
>>>>>>> origin/kishore/milestone4-security-integrations
    public static final double HIGH_VALUE_THRESHOLD = 50000.0;

    private final VerificationRepository verificationRepository;
    private final ApplicationRepository applicationRepository;
    private final FinanceApprovalRepository financeApprovalRepository;

<<<<<<< HEAD
    // Audit Log Service
    private final AuditLogService auditLogService;

=======
>>>>>>> origin/kishore/milestone4-security-integrations
    public VerificationService(
            VerificationRepository verificationRepository,
            ApplicationRepository applicationRepository,
            FinanceApprovalRepository financeApprovalRepository,
            AuditLogService auditLogService
    ) {
        this.verificationRepository = verificationRepository;
        this.applicationRepository = applicationRepository;
        this.financeApprovalRepository = financeApprovalRepository;
<<<<<<< HEAD
        this.auditLogService = auditLogService;
    }

    /**
     * Creates the FIRST verification-stage record for an application.
     */
=======
    }

    // ============================================================
    // CREATE VERIFICATION
    // ============================================================

>>>>>>> origin/kishore/milestone4-security-integrations
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

<<<<<<< HEAD
        }
        else if (score < 80 && !highValue) {
=======
        } else if (score < 80 && !highValue) {
>>>>>>> origin/kishore/milestone4-security-integrations

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
<<<<<<< HEAD
                            ?
                            "Waiting for District Officer Verification "
                                    + "(escalated: high-value application)"
                            :
                            "Waiting for District Officer Verification"
=======
                            ? "Waiting for District Officer Verification "
                              + "(escalated: high-value application)"
                            : "Waiting for District Officer Verification"
>>>>>>> origin/kishore/milestone4-security-integrations
            );
        }

        applicationRepository.save(application);

<<<<<<< HEAD
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

=======
        return verificationRepository.save(verification);
    }

    // ============================================================
    // GET ALL
    // ============================================================

>>>>>>> origin/kishore/milestone4-security-integrations
    public List<Verification> getAllVerifications() {

        return verificationRepository.findAll();
    }

<<<<<<< HEAD
=======
    // ============================================================
    // GET BY ID
    // ============================================================

>>>>>>> origin/kishore/milestone4-security-integrations
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

<<<<<<< HEAD
    /**
     * Confirms the currently authenticated caller is actually allowed to act
     * on THIS verification's current stage — not just "some officer role",
     * which @PreAuthorize alone can't distinguish. ADMIN can always act.
     * A Field Officer cannot approve a row that's waiting on the District
     * Officer stage, and vice versa, no matter what a client-supplied
     * request param claims.
     */
    private void assertCallerCanActOnStage(Verification verification) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin =
                auth != null && auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        String stage = verification.getVerifiedBy();

        String requiredAuthority =
                "District Officer".equalsIgnoreCase(stage)
                        ? "ROLE_DISTRICT_OFFICER"
                        : "ROLE_FIELD_OFFICER";

        boolean hasRequiredAuthority =
                auth != null && auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(requiredAuthority));

        if (!hasRequiredAuthority) {

            throw new AccessDeniedException(
                    "You are not authorized to act on this verification's "
                    + "current stage (" + stage + ")"
            );

        }

    }

    /**
     * Approves the current verification stage. Approving does NOT reuse the
     * same row for the next stage — it closes out this record (status
     * "Approved", with the approver's remarks preserved) and, if there's a
     * next stage, inserts a brand-new Verification row for it. This keeps a
     * permanent, un-overwritten history of every stage the application
     * passed through.
     */
=======
    // ============================================================
    // APPROVE
    // ============================================================

>>>>>>> origin/kishore/milestone4-security-integrations
    public Verification approveVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

<<<<<<< HEAD
        assertCallerCanActOnStage(verification);

=======
>>>>>>> origin/kishore/milestone4-security-integrations
        Application application =
                Objects.requireNonNull(
                        verification.getApplication()
                );

        String officer =
                verification.getVerifiedBy();

<<<<<<< HEAD
        // Store old status before changing it
        String oldStatus =
                verification.getVerificationStatus();

        if ("Field Officer".equalsIgnoreCase(officer)) {

            // Close Field Officer stage
=======
        if ("Field Officer".equalsIgnoreCase(officer)) {

>>>>>>> origin/kishore/milestone4-security-integrations
            verification.setVerificationStatus(
                    "Approved"
            );

            verification.setRemarks(
                    remarks
            );

<<<<<<< HEAD
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
=======
            verificationRepository.save(
                    verification
>>>>>>> origin/kishore/milestone4-security-integrations
            );

            application.setStatus(
                    "District Verification Pending"
            );

<<<<<<< HEAD
            // Create District Officer stage
            Verification nextStage =
                    new Verification();

            nextStage.setApplication(application);
=======
            Verification nextStage =
                    new Verification();

            nextStage.setApplication(
                    application
            );
>>>>>>> origin/kishore/milestone4-security-integrations

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

<<<<<<< HEAD
            return verificationRepository.save(nextStage);
        }

        else if ("District Officer".equalsIgnoreCase(officer)) {
=======
            return verificationRepository.save(
                    nextStage
            );

        } else if ("District Officer".equalsIgnoreCase(officer)) {
>>>>>>> origin/kishore/milestone4-security-integrations

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

<<<<<<< HEAD
                approval.setApplication(application);
=======
                approval.setApplication(
                        application
                );
>>>>>>> origin/kishore/milestone4-security-integrations

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

<<<<<<< HEAD
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

=======
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

>>>>>>> origin/kishore/milestone4-security-integrations
    public Verification rejectVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

<<<<<<< HEAD
        assertCallerCanActOnStage(verification);

        // Store old status
        String oldStatus =
                verification.getVerificationStatus();

=======
>>>>>>> origin/kishore/milestone4-security-integrations
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

<<<<<<< HEAD
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
=======
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

>>>>>>> origin/kishore/milestone4-security-integrations
    public Verification sendForReVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

<<<<<<< HEAD
        assertCallerCanActOnStage(verification);

        // Store old status
        String oldStatus =
                verification.getVerificationStatus();

        // Preserve current verification row
=======
>>>>>>> origin/kishore/milestone4-security-integrations
        verification.setVerificationStatus(
                "Sent Back"
        );

        verification.setRemarks(
                remarks
        );

<<<<<<< HEAD
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
=======
        verificationRepository.save(
                verification
>>>>>>> origin/kishore/milestone4-security-integrations
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

<<<<<<< HEAD
        // Create fresh Field Officer stage
=======
>>>>>>> origin/kishore/milestone4-security-integrations
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

<<<<<<< HEAD
    public List<Verification> getPendingVerifications() {

        return verificationRepository
                .findByVerificationStatus("Pending");
    }

    /**
     * Full audit trail for one application.
     */
=======
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

>>>>>>> origin/kishore/milestone4-security-integrations
    public List<Verification> getVerificationHistory(
            @NonNull Long applicationId
    ) {

        Application application =
                applicationRepository.findById(
<<<<<<< HEAD
                                Objects.requireNonNull(applicationId)
=======
                                Objects.requireNonNull(
                                        applicationId
                                )
>>>>>>> origin/kishore/milestone4-security-integrations
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

<<<<<<< HEAD
    /**
     * If a verification has sat in its current stage for more than 3 days,
     * escalate it: record the delay, AND actually open a new stage at
     * District Officer level so the application is genuinely rerouted, not
     * just annotated.
     */
=======
    // ============================================================
    // ESCALATION
    // ============================================================

>>>>>>> origin/kishore/milestone4-security-integrations
    public void checkEscalation(
            @NonNull Long verificationId
    ) {

        Verification verification =
<<<<<<< HEAD
                getVerificationById(verificationId);
=======
                getVerificationById(
                        verificationId
                );
>>>>>>> origin/kishore/milestone4-security-integrations

        LocalDate today =
                LocalDate.now();

        if (verification.getVerificationDate()
                .plusDays(3)
                .isBefore(today)) {
<<<<<<< HEAD

            String oldRemarks =
                    verification.getRemarks();

            String newRemarks =
                    "Escalated to higher officer due to delay";
=======
>>>>>>> origin/kishore/milestone4-security-integrations

            verification.setRemarks(
                    newRemarks
            );

            verificationRepository.save(
                    verification
            );
<<<<<<< HEAD

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

            // Actually reroute — open a new stage at District Officer
            // level instead of just noting the delay.
            Application application =
                    Objects.requireNonNull(
                            verification.getApplication()
                    );

            Verification escalated = new Verification();

            escalated.setApplication(application);

            escalated.setVerificationDate(
                    LocalDate.now()
            );

            escalated.setVerifiedBy(
                    "District Officer"
            );

            escalated.setVerificationStatus(
                    "Pending"
            );

            escalated.setRemarks(
                    "Escalated from delayed Field Officer review"
            );

            application.setStatus(
                    "District Verification Pending"
            );

            applicationRepository.save(application);

            verificationRepository.save(escalated);
=======
>>>>>>> origin/kishore/milestone4-security-integrations
        }
    }
}