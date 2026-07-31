package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.DisbursementInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisbursementInstallmentRepository extends JpaRepository<DisbursementInstallment, Long> {
    List<DisbursementInstallment> findByDisbursementPlanPlanId(Long planId);
}