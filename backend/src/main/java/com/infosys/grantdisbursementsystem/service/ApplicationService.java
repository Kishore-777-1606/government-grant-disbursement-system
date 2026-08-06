package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ApplicationService {


    Application submitApplication(Application application);


    List<Application> getAllApplications();


    Application getApplicationById(@NonNull Long id);


    Application updateApplication(
            @NonNull Long id,
            Application application
    );


    void deleteApplication(@NonNull Long id);

}