package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.DisbursementPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisbursementPlanRepository extends JpaRepository<DisbursementPlan, Long> {
}