package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.entity.Verification;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.FinanceApprovalRepository;
import com.infosys.grantdisbursementsystem.repository.VerificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VerificationService {

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private FinanceApprovalRepository financeApprovalRepository;


    // Create Verification and Automatic Routing
    public Verification createVerification(Long applicationId, String officerRole) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));


        Verification verification = new Verification();

        verification.setApplication(application);
        verification.setVerificationDate(LocalDate.now());


        double score = application.getEligibilityScore();


        // Eligibility Based Routing

        if (score < 60) {

           application.setStatus("Rejected");

            verification.setVerifiedBy("System");
            verification.setVerificationStatus("Rejected");
            verification.setRemarks(
                    "Application rejected due to low eligibility score"
            );


        } else if (score >= 60 && score < 80) {


           application.setStatus(
                    "Field Verification Pending"
            );

            verification.setVerifiedBy("Field Officer");
            verification.setVerificationStatus("Pending");
            verification.setRemarks(
                    "Waiting for Field Officer Verification"
            );


        } else {


           application.setStatus(
                    "District Verification Pending"
            );

            verification.setVerifiedBy("District Officer");
            verification.setVerificationStatus("Pending");
            verification.setRemarks(
                    "Waiting for District Officer Verification"
            );

        }


        applicationRepository.save(application);

        return verificationRepository.save(verification);
    }



    // Get All Verifications
    public List<Verification> getAllVerifications() {

        return verificationRepository.findAll();
    }



    // Get Verification By ID
    public Verification getVerificationById(Long id) {

        return verificationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Verification not found"));
    }



    // Approve Verification Workflow

    public Verification approveVerification(Long id, String remarks) {


        Verification verification = getVerificationById(id);

        Application application = verification.getApplication();



        // Field Officer Approval
        if (verification.getVerifiedBy()
                .equalsIgnoreCase("Field Officer")) {


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


        // District Officer Approval

        else if (verification.getVerifiedBy()
                .equalsIgnoreCase("District Officer")) {


           application.setStatus(
                    "Finance Approval Pending"
            );


            verification.setVerificationStatus(
                    "Approved"
            );

            verification.setRemarks(remarks);



            // Create Finance Approval Record

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

        return verificationRepository.save(verification);
    }





    // Reject Verification

    public Verification rejectVerification(Long id, String remarks) {


        Verification verification =
                getVerificationById(id);


        verification.setVerificationStatus(
                "Rejected"
        );

        verification.setRemarks(remarks);



        Application application =
                verification.getApplication();


       application.setStatus(
                "Rejected"
        );


        applicationRepository.save(application);


        return verificationRepository.save(verification);
    }





    // Send For Re-Verification

    public Verification sendForReVerification(
            Long id,
            String remarks) {


        Verification verification =
                getVerificationById(id);



        verification.setVerificationStatus(
                "Re-Verification"
        );


        verification.setVerifiedBy(
                "Field Officer"
        );


        verification.setRemarks(remarks);



        Application application =
                verification.getApplication();


       application.setStatus(
                "Re-Verification Pending"
        );



        applicationRepository.save(application);


        return verificationRepository.save(verification);
    }





    // Get Pending Verifications

    public List<Verification> getPendingVerifications() {

        return verificationRepository
                .findByVerificationStatus("Pending");
    }





    // Escalation Logic (3 Days Delay)

    public void checkEscalation(Long verificationId) {


        Verification verification =
                getVerificationById(verificationId);



        LocalDate today = LocalDate.now();



        if (verification.getVerificationDate()
                .plusDays(3)
                .isBefore(today)) {



            verification.setRemarks(
                    "Escalated to higher officer due to delay"
            );


            verificationRepository.save(verification);

        }

    }

}