package com.infosys.grantdisbursementsystem.service.impl;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository repository;

    @Override
    public Application submitApplication(Application application) {
        return repository.save(application);
    }

    @Override
    public List<Application> getAllApplications() {
        return repository.findAll();
    }

    @Override
    public Application getApplicationById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Application updateApplication(Long id, Application application) {
        application.setApplicationId(id);
        return repository.save(application);
    }

    @Override
    public void deleteApplication(Long id) {
        repository.deleteById(id);
    }
}