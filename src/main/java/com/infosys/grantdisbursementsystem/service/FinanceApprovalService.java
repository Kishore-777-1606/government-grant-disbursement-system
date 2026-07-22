package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.FinanceApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinanceApprovalService {

    @Autowired
    private FinanceApprovalRepository financeApprovalRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // Create Finance Approval
    public FinanceApproval createApproval(Long applicationId, String financeOfficer) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Prevent duplicate approval records
        if (financeApprovalRepository.findByApplication(application).isPresent()) {
            throw new RuntimeException("Finance approval already exists.");
        }

        FinanceApproval approval = new FinanceApproval();

        approval.setApplication(application);
        approval.setApprovedBy(financeOfficer);
        approval.setApprovalStatus("Pending");
        approval.setApprovalDate(LocalDate.now());
        approval.setRemarks("Waiting for Finance Approval");

        application.setApplicationStatus("Finance Approval Pending");
        applicationRepository.save(application);

        return financeApprovalRepository.save(approval);
    }

    // Get All Approvals
    public List<FinanceApproval> getAllApprovals() {
        return financeApprovalRepository.findAll();
    }

    // Get Approval By ID
    public FinanceApproval getApprovalById(Long id) {

        return financeApprovalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finance approval not found"));
    }

    // Approve Finance
    public FinanceApproval approve(Long id, String remarks) {

        FinanceApproval approval = getApprovalById(id);

        approval.setApprovalStatus("Approved");
        approval.setApprovalDate(LocalDate.now());
        approval.setRemarks(remarks);

        Application application = approval.getApplication();

        // Final status after finance approval
        application.setApplicationStatus("Approved");

        applicationRepository.save(application);

        return financeApprovalRepository.save(approval);
    }

    // Reject Finance
    public FinanceApproval reject(Long id, String remarks) {

        FinanceApproval approval = getApprovalById(id);

        approval.setApprovalStatus("Rejected");
        approval.setApprovalDate(LocalDate.now());
        approval.setRemarks(remarks);

        Application application = approval.getApplication();

        application.setApplicationStatus("Rejected");

        applicationRepository.save(application);

        return financeApprovalRepository.save(approval);
    }

    // Pending Approvals
    public List<FinanceApproval> getPendingApprovals() {
        return financeApprovalRepository.findByApprovalStatus("Pending");
    }

    // Approved Approvals
    public List<FinanceApproval> getApprovedApprovals() {
        return financeApprovalRepository.findByApprovalStatus("Approved");
    }

    // Rejected Approvals
    public List<FinanceApproval> getRejectedApprovals() {
        return financeApprovalRepository.findByApprovalStatus("Rejected");
    }

}
