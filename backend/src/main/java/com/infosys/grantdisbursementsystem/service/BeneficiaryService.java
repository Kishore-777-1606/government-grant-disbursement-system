package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.repository.BeneficiaryRepository;
import org.springframework.stereotype.Service;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public List<Beneficiary> getAllBeneficiaries() {
        return beneficiaryRepository.findAll();
    }

    public Beneficiary getBeneficiaryById(Long id) {
        return beneficiaryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + id));
    }

    public Beneficiary saveBeneficiary(Beneficiary beneficiary) {
        return beneficiaryRepository.save(beneficiary);
    }

    public Beneficiary updateBeneficiary(Long id, Beneficiary beneficiaryDetails) {

        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + id));

        beneficiary.setBeneficiaryUid(beneficiaryDetails.getBeneficiaryUid());
        beneficiary.setFirstName(beneficiaryDetails.getFirstName());
        beneficiary.setLastName(beneficiaryDetails.getLastName());
        beneficiary.setDateOfBirth(beneficiaryDetails.getDateOfBirth());
        beneficiary.setGender(beneficiaryDetails.getGender());
        beneficiary.setMobileNumber(beneficiaryDetails.getMobileNumber());
        beneficiary.setEmail(beneficiaryDetails.getEmail());
        beneficiary.setAddressLine1(beneficiaryDetails.getAddressLine1());
        beneficiary.setAddressLine2(beneficiaryDetails.getAddressLine2());
        beneficiary.setVillageId(beneficiaryDetails.getVillageId());
        beneficiary.setBlockId(beneficiaryDetails.getBlockId());
        beneficiary.setDistrictId(beneficiaryDetails.getDistrictId());
        beneficiary.setStateId(beneficiaryDetails.getStateId());
        beneficiary.setPincode(beneficiaryDetails.getPincode());
        beneficiary.setBankAccountNumber(beneficiaryDetails.getBankAccountNumber());
        beneficiary.setIfscCode(beneficiaryDetails.getIfscCode());
        beneficiary.setBankName(beneficiaryDetails.getBankName());
        beneficiary.setAadhaarVerified(beneficiaryDetails.getAadhaarVerified());
        beneficiary.setBankVerified(beneficiaryDetails.getBankVerified());
        beneficiary.setIsActive(beneficiaryDetails.getIsActive());

        return beneficiaryRepository.save(beneficiary);
    }

    public void deleteBeneficiary(Long id) {
        if (!beneficiaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Beneficiary not found with ID: " + id);
        }
        beneficiaryRepository.deleteById(id);
    }
}