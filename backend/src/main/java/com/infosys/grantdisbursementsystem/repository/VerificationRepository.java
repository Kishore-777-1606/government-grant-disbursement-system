package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VerificationRepository extends JpaRepository<Verification, Long> {


    // Full audit trail for an application, oldest stage first.
    List<Verification> findByApplicationOrderByVerificationIdAsc(Application application);

    // The currently active / most recent stage for an application.
    Optional<Verification> findFirstByApplicationOrderByVerificationIdDesc(Application application);

    // The very first stage ever recorded for an application (used for
    // "submission -> first verification action" turnaround metrics).
    Optional<Verification> findFirstByApplicationOrderByVerificationIdAsc(Application application);


    // Find verification records by status
    List<Verification> findByVerificationStatus(String verificationStatus);


    // Find verification records by officer
    List<Verification> findByVerifiedBy(String verifiedBy);


}