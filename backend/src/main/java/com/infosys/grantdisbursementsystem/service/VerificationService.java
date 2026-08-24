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
       else if (score < 100 && !highValue) {

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
        highValue
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
    public Verification approveVerification(
            @NonNull Long id,
            String remarks
    ) {

        Verification verification =
                getVerificationById(id);

        assertCallerCanActOnStage(verification);

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

        assertCallerCanActOnStage(verification);

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

        assertCallerCanActOnStage(verification);

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

    /**
     * If a verification has sat in its current stage for more than 3 days,
     * escalate it: record the delay, AND actually open a new stage at
     * District Officer level so the application is genuinely rerouted, not
     * just annotated.
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
        }
    }
}