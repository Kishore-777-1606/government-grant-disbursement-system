package com.infosys.grantdisbursementsystem.service;


import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.FinanceApprovalRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;



@Service
public class FinanceApprovalService {



    private final FinanceApprovalRepository financeApprovalRepository;

    private final ApplicationRepository applicationRepository;




    public FinanceApprovalService(
            FinanceApprovalRepository financeApprovalRepository,
            ApplicationRepository applicationRepository
    ) {

        this.financeApprovalRepository = financeApprovalRepository;

        this.applicationRepository = applicationRepository;

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




        if(financeApprovalRepository
                .findByApplication(application)
                .isPresent()) {


            throw new IllegalStateException(
                    "Finance approval already exists for this application."
            );

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



        return financeApprovalRepository.save(approval);

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