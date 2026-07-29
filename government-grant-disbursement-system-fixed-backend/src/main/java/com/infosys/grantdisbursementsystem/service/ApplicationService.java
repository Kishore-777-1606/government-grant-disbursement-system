package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Application;
import java.util.List;

public interface ApplicationService {

    Application submitApplication(Application application);

    List<Application> getAllApplications();

    Application getApplicationById(Long id);

    Application updateApplication(Long id, Application application);

    void deleteApplication(Long id);
}