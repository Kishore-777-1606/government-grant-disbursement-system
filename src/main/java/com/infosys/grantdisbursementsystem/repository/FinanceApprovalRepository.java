package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.FinanceApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceApprovalRepository extends JpaRepository<FinanceApproval, Long> {

    Optional<FinanceApproval> findByApplication(Application application);

    List<FinanceApproval> findByApprovalStatus(String approvalStatus);

    List<FinanceApproval> findByApprovedBy(String approvedBy);

}