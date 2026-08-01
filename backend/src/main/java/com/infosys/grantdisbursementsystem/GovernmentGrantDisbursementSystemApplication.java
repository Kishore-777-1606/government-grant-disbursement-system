package com.infosys.grantdisbursementsystem;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.service.DisbursementPlanService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GovernmentGrantDisbursementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernmentGrantDisbursementSystemApplication.class, args);
    }
    
}