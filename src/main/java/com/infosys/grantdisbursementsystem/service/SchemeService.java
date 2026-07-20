package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Scheme;
import com.infosys.grantdisbursementsystem.repository.SchemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchemeService {

    private final SchemeRepository schemeRepository;

    public SchemeService(SchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    public List<Scheme> getAllSchemes() {
        return schemeRepository.findAll();
    }

    public Scheme getSchemeById(Long id) {
        return schemeRepository.findById(id).orElse(null);
    }

    public Scheme saveScheme(Scheme scheme) {
        return schemeRepository.save(scheme);
    }

    public Scheme updateScheme(Long id, Scheme schemeDetails) {

        Scheme scheme = schemeRepository.findById(id).orElse(null);

        if (scheme != null) {

            scheme.setSchemeCode(schemeDetails.getSchemeCode());
            scheme.setName(schemeDetails.getName());
            scheme.setDescription(schemeDetails.getDescription());
            scheme.setSchemeType(schemeDetails.getSchemeType());
            scheme.setDisbursementMode(schemeDetails.getDisbursementMode());
            scheme.setFrequency(schemeDetails.getFrequency());
            scheme.setAmount(schemeDetails.getAmount());
            scheme.setMaxBeneficiaries(schemeDetails.getMaxBeneficiaries());
            scheme.setStartDate(schemeDetails.getStartDate());
            scheme.setEndDate(schemeDetails.getEndDate());
            scheme.setIsActive(schemeDetails.getIsActive());

            return schemeRepository.save(scheme);
        }

        return null;
    }

    public void deleteScheme(Long id) {
        schemeRepository.deleteById(id);
    }
}