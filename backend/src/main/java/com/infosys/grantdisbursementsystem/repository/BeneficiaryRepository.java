package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

}