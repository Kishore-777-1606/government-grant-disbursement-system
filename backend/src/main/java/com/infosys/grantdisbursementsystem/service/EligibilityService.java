package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.dto.EligibilityView;
import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.entity.Scheme;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.ApplicationRepository;
import com.infosys.grantdisbursementsystem.repository.BeneficiaryRepository;
import com.infosys.grantdisbursementsystem.repository.SchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EligibilityService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    public List<EligibilityView> getAllEligibilityRecords() {
        return applicationRepository.findAll()
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    public EligibilityView getEligibilityByApplicationId(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));
        return toView(application);
    }

    private EligibilityView toView(Application application) {

        String beneficiaryName = beneficiaryRepository.findById(application.getBeneficiaryId())
                .map(this::fullName)
                .orElse("Unknown");

        String schemeName = schemeRepository.findById(application.getSchemeId())
                .map(Scheme::getName)
                .orElse("Unknown");

        return new EligibilityView(
                application.getApplicationId(),
                application.getBeneficiaryId(),
                beneficiaryName,
                application.getSchemeId(),
                schemeName,
                application.getEligibilityScore(),
                application.getStatus(),
                application.getApplicationDate()
        );
    }

    private String fullName(Beneficiary beneficiary) {
        String last = beneficiary.getLastName() != null ? " " + beneficiary.getLastName() : "";
        return beneficiary.getFirstName() + last;
    }
}
