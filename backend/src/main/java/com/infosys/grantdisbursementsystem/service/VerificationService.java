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


    // Applications requesting this much or more are always escalated to
    // District Officer for extra scrutiny, regardless of eligibility score
    // (Module 2: "high-value cases escalated for additional scrutiny").
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
     * Creates the FIRST verification-stage record for an application, once it
     * has passed eligibility scoring. Always inserts a new row (an
     * application should only reach this once, from ApplicationServiceImpl),
     * rather than reusing/overwriting an existing record — each stage of the
     * workflow gets its own permanent row so the audit trail survives later
     * stage transitions.
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
                ?
                application.getEligibilityScore()
                :
                0;


        // High-value applications get extra scrutiny (Module 2 requirement:
        // "flagged or high-value cases escalated for additional scrutiny"),
        // regardless of which score band they'd otherwise fall into.
        double amount =
                application.getAppliedAmount() != null
                ?
                application.getAppliedAmount().doubleValue()
                :
                0;

        boolean highValue = amount >= HIGH_VALUE_THRESHOLD;


        if(score < 60) {

            application.setStatus("Rejected");
            verification.setVerifiedBy("System");
            verification.setVerificationStatus("Rejected");
            verification.setRemarks(
                    "Application rejected due to low eligibility score"
            );

        }
        else if(score < 80 && !highValue) {

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


        return verificationRepository.save(verification);

    }







    public List<Verification> getAllVerifications(){

        return verificationRepository.findAll();

    }








    public Verification getVerificationById(
            @NonNull Long id
    ){

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
    ){


        Verification verification =
                getVerificationById(id);



        Application application =
                Objects.requireNonNull(
                        verification.getApplication()
                );



        String officer =
                verification.getVerifiedBy();




        if("Field Officer".equalsIgnoreCase(officer)){


            // Close out the Field Officer stage — this row now permanently
            // records that decision.
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


            // Open a new stage record for the District Officer instead of
            // overwriting the Field Officer's row.
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

            return verificationRepository.save(nextStage);

        }

        else if("District Officer".equalsIgnoreCase(officer)){


            application.setStatus(
                    "Finance Approval Pending"
            );


            verification.setVerificationStatus(
                    "Approved"
            );


            verification.setRemarks(
                    remarks
            );



            if(financeApprovalRepository
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


        return verificationRepository.save(verification);

    }







    public Verification rejectVerification(
            @NonNull Long id,
            String remarks
    ){


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



        return verificationRepository.save(verification);

    }








    /**
     * Sends the application back for re-verification. The officer who sent
     * it back keeps their own row intact (status "Sent Back", with their
     * remarks) — a new "Field Officer" stage row is created for the
     * re-verification cycle rather than overwriting the sender's record.
     */
    public Verification sendForReVerification(
            @NonNull Long id,
            String remarks
    ){


        Verification verification =
                getVerificationById(id);


        // Preserve which officer sent it back and why — don't relabel this
        // row as "Field Officer" / overwrite their identity.
        verification.setVerificationStatus(
                "Sent Back"
        );

        verification.setRemarks(
                remarks
        );

        verificationRepository.save(verification);



        Application application =
                Objects.requireNonNull(
                        verification.getApplication()
                );


        application.setStatus(
                "Re-Verification Pending"
        );

        applicationRepository.save(application);


        // Open a fresh Field Officer stage for the re-verification cycle.
        Verification reVerificationStage = new Verification();

        reVerificationStage.setApplication(application);

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


        return verificationRepository.save(reVerificationStage);

    }








    public List<Verification> getPendingVerifications(){

        return verificationRepository
                .findByVerificationStatus("Pending");

    }




    /**
     * Full audit trail for one application — every stage it has ever passed
     * through, oldest first. This is the payoff of the @ManyToOne redesign:
     * previously there was only ever one (overwritten) row per application.
     */
    public List<Verification> getVerificationHistory(
            @NonNull Long applicationId
    ){

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
                .findByApplicationOrderByVerificationIdAsc(application);

    }








    public void checkEscalation(
            @NonNull Long verificationId
    ){


        Verification verification =
                getVerificationById(verificationId);



        LocalDate today =
                LocalDate.now();



        if(verification.getVerificationDate()
                .plusDays(3)
                .isBefore(today)){


            verification.setRemarks(
                    "Escalated to higher officer due to delay"
            );


            verificationRepository.save(verification);

        }

    }


}