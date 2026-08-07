package com.infosys.grantdisbursementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GovernmentGrantDisbursementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernmentGrantDisbursementSystemApplication.class, args);
    }
}