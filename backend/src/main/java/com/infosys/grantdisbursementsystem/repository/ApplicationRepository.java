package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ApplicationRepository extends JpaRepository<Application, Long> {

}