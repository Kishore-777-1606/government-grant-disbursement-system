package com.infosys.grantdisbursementsystem.service.impl;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.entity.Scheme;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.BeneficiaryRepository;
import com.infosys.grantdisbursementsystem.repository.SchemeRepository;
import com.infosys.grantdisbursementsystem.service.ApplicationService;
import com.infosys.grantdisbursementsystem.service.EligibilityScoringService;
import com.infosys.grantdisbursementsystem.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository repository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private EligibilityScoringService eligibilityScoringService;

    @Autowired
    private VerificationService verificationService;

    @Override
    public Application submitApplication(Application application) {

        if (application.getBeneficiaryId() == null) {
            throw new IllegalArgumentException("Beneficiary ID is required");
        }

        if (application.getSchemeId() == null) {
            throw new IllegalArgumentException("Scheme ID is required");
        }

        Beneficiary beneficiary = beneficiaryRepository
                .findById(application.getBeneficiaryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Beneficiary not found with ID: " + application.getBeneficiaryId()));

        Scheme scheme = schemeRepository
                .findById(application.getSchemeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Scheme not found with ID: " + application.getSchemeId()));

        double score = eligibilityScoringService.calculateScore(beneficiary, scheme);
        boolean eligible = eligibilityScoringService.isEligible(score);

        application.setEligibilityScore(score);
        application.setStatus(eligible ? "Eligible" : "Not Eligible");

        Application saved = repository.save(application);

        // Automatically create the Verification record and route it to the
        // correct officer as soon as the application is eligible - this is
        // what previously required a separate manual API call.
        if (eligible) {
            verificationService.createVerification(saved.getApplicationId(), null);
            // createVerification also updates and saves the application's
            // status (e.g. "Field Verification Pending"), so re-fetch the
            // latest state to return to the caller.
            saved = repository.findById(saved.getApplicationId()).orElse(saved);
        }

        return saved;
    }

    @Override
    public List<Application> getAllApplications() {
        return repository.findAll();
    }

    @Override
    public Application getApplicationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
    }

    @Override
    public Application updateApplication(Long id, Application application) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found with ID: " + id);
        }
        application.setApplicationId(id);
        return repository.save(application);
    }

    @Override
    public void deleteApplication(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
