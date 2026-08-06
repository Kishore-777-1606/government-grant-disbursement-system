package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VerificationRepository extends JpaRepository<Verification, Long> {


    // Find verification record for an application
    Optional<Verification> findByApplication(Application application);


    // Find verification records by status
    List<Verification> findByVerificationStatus(String verificationStatus);


    // Find verification records by officer
    List<Verification> findByVerifiedBy(String verifiedBy);


}