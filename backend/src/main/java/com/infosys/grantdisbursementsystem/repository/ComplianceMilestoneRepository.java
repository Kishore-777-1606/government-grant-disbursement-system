package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.ComplianceMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplianceMilestoneRepository extends JpaRepository<ComplianceMilestone, Long> {
    List<ComplianceMilestone> findByStatus(String status);
    List<ComplianceMilestone> findByApplicationApplicationId(Long applicationId);
}