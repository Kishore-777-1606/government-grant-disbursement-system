package com.infosys.grantdisbursementsystem;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableScheduling
@SpringBootApplication
public class GovernmentGrantDisbursementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernmentGrantDisbursementSystemApplication.class, args);
    }

}