package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.DisbursementPlan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisbursementPlanRepository
        extends JpaRepository<DisbursementPlan, Long> {

    Optional<DisbursementPlan>
    findByApplication(Application application);
}