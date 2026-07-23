package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

}