package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.entity.Scheme;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.DisbursementPlanRepository;
import com.infosys.grantdisbursementsystem.repository.FinanceApprovalRepository;
import com.infosys.grantdisbursementsystem.repository.SchemeRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;



@Service
public class FinanceApprovalService {



    private final FinanceApprovalRepository financeApprovalRepository;

    private final ApplicationRepository applicationRepository;

    private final SchemeRepository schemeRepository;

    private final DisbursementPlanRepository disbursementPlanRepository;

    private final DisbursementPlanService disbursementPlanService;

    // Default number of staged installments a newly-approved grant is split into.
    // Matches the 3 milestone types (Documentation, Ground Verification, Utilization Proof)
    // defined in DisbursementPlanService.
    private static final int DEFAULT_INSTALLMENTS = 3;




    public FinanceApprovalService(
            FinanceApprovalRepository financeApprovalRepository,
            ApplicationRepository applicationRepository,
            SchemeRepository schemeRepository,
            DisbursementPlanRepository disbursementPlanRepository,
            DisbursementPlanService disbursementPlanService
    ) {

        this.financeApprovalRepository = financeApprovalRepository;

        this.applicationRepository = applicationRepository;

        this.schemeRepository = schemeRepository;

        this.disbursementPlanRepository = disbursementPlanRepository;

        this.disbursementPlanService = disbursementPlanService;

    }




    // Create Finance Approval

    public FinanceApproval createApproval(
            @NonNull Long applicationId,
            String financeOfficer
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

        // FinanceApproval is @OneToOne with a UNIQUE constraint on
        // application_id (see schema.sql) — we can never insert a second
        // row for the same application, even after a rejection. So a prior
        // REJECTED row is reset back to Pending and reused instead of
        // inserted fresh; anything still Pending/Approved blocks a new
        // request as before.
        var existingOpt =
                financeApprovalRepository.findByApplication(application);

        if (existingOpt.isPresent()) {

            FinanceApproval existing = existingOpt.get();

            if (!"Rejected".equalsIgnoreCase(existing.getApprovalStatus())) {

                throw new IllegalStateException(
                        "Finance approval already exists for this application."
                );

            }

            existing.setApprovedBy(financeOfficer);
            existing.setApprovalStatus("Pending");
            existing.setApprovalDate(LocalDate.now());
            existing.setRemarks("Waiting for Finance Approval (resubmitted)");

            application.setStatus("Finance Approval Pending");
            applicationRepository.save(application);

            return financeApprovalRepository.save(existing);

        }

        FinanceApproval approval =
                new FinanceApproval();

        approval.setApplication(application);

        approval.setApprovedBy(financeOfficer);

        approval.setApprovalStatus("Pending");

        approval.setApprovalDate(LocalDate.now());

        approval.setRemarks(
                "Waiting for Finance Approval"
        );

        application.setStatus(
                "Finance Approval Pending"
        );

        applicationRepository.save(application);

        return financeApprovalRepository.save(approval);

    }




    // Get All Approvals

    public List<FinanceApproval> getAllApprovals() {

        return financeApprovalRepository.findAll();

    }




    // Get Approval By ID

    public FinanceApproval getApprovalById(
            @NonNull Long id
    ) {

        return financeApprovalRepository.findById(
                Objects.requireNonNull(id)
        )
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Finance approval not found with ID: "
                        + id
                )
        );

    }




    // Approve Finance

    public FinanceApproval approve(
            @NonNull Long id,
            String remarks
    ) {

        FinanceApproval approval =
                getApprovalById(id);

        approval.setApprovalStatus(
                "Approved"
        );

        approval.setApprovalDate(
                LocalDate.now()
        );

        approval.setRemarks(
                remarks
        );

        Application application =
                approval.getApplication();

        application.setStatus(
                "Approved"
        );

        applicationRepository.save(application);


        FinanceApproval saved =
                financeApprovalRepository.save(approval);


        // Auto-trigger staged disbursement now that finance has signed off.
        // Grant amount: prefer what finance actually approved, then what
        // was applied for, and only fall back to the scheme's default
        // amount if neither was ever set (e.g. very old records). Skip
        // quietly if a plan already exists so re-approving (e.g. after a
        // fix) doesn't create duplicates.
        boolean planAlreadyExists =
                disbursementPlanRepository
                        .findByApplication(application)
                        .isPresent();

        if (!planAlreadyExists) {

            java.math.BigDecimal grantAmount =
                    application.getApprovedAmount() != null
                            ? application.getApprovedAmount()
                            : application.getAppliedAmount();

            if (grantAmount == null && application.getSchemeId() != null) {

                Scheme scheme =
                        schemeRepository.findById(application.getSchemeId())
                        .orElse(null);

                if (scheme != null) {
                    grantAmount = scheme.getAmount();
                }

            }

            // Record what was actually approved, so it's visible on the
            // application even if it was defaulted from appliedAmount/scheme.
            if (grantAmount != null && application.getApprovedAmount() == null) {
                application.setApprovedAmount(grantAmount);
                applicationRepository.save(application);
            }

            if (grantAmount != null) {

                disbursementPlanService.createPlan(
                        application,
                        grantAmount.doubleValue(),
                        DEFAULT_INSTALLMENTS
                );

            }

        }


        return saved;

    }




    // Reject Finance

    public FinanceApproval reject(
            @NonNull Long id,
            String remarks
    ) {

        FinanceApproval approval =
                getApprovalById(id);

        approval.setApprovalStatus(
                "Rejected"
        );

        approval.setApprovalDate(
                LocalDate.now()
        );

        approval.setRemarks(
                remarks
        );

        Application application =
                approval.getApplication();

        application.setStatus(
                "Rejected"
        );

        applicationRepository.save(application);

        return financeApprovalRepository.save(approval);

    }




    // Pending Approvals

    public List<FinanceApproval> getPendingApprovals() {

        return financeApprovalRepository
                .findByApprovalStatus("Pending");

    }




    // Approved Approvals

    public List<FinanceApproval> getApprovedApprovals() {

        return financeApprovalRepository
                .findByApprovalStatus("Approved");

    }




    // Rejected Approvals

    public List<FinanceApproval> getRejectedApprovals() {

        return financeApprovalRepository
                .findByApprovalStatus("Rejected");

    }


}