package com.infosys.grantdisbursementsystem.repository;

import com.infosys.grantdisbursementsystem.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SchemeRepository extends JpaRepository<Scheme, Long> {

}