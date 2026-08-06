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



        Verification verification =
                verificationRepository
                        .findByApplication(application)
                        .orElseGet(Verification::new);



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



        if(score < 60) {


            application.setStatus("Rejected");

            verification.setVerifiedBy("System");

            verification.setVerificationStatus("Rejected");

            verification.setRemarks(
                    "Application rejected due to low eligibility score"
            );


        }
        else if(score < 80) {


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


        }
        else {


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
                    "Waiting for District Officer Verification"
            );


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








    public Verification sendForReVerification(
            @NonNull Long id,
            String remarks
    ){


        Verification verification =
                getVerificationById(id);



        verification.setVerificationStatus(
                "Re-Verification"
        );


        verification.setVerifiedBy(
                "Field Officer"
        );


        verification.setRemarks(
                remarks
        );



        Application application =
                Objects.requireNonNull(
                        verification.getApplication()
                );


        application.setStatus(
                "Re-Verification Pending"
        );


        applicationRepository.save(application);



        return verificationRepository.save(verification);

    }








    public List<Verification> getPendingVerifications(){

        return verificationRepository
                .findByVerificationStatus("Pending");

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