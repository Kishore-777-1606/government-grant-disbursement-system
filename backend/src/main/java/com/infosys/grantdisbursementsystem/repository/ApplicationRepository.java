package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Used by P7a — checks whether this beneficiary already has an
    // active/pending application for this scheme, before letting a
    // new one through.
    List<Application> findByBeneficiaryIdAndSchemeId(
            Long beneficiaryId,
            Long schemeId
    );

}