package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.service.BeneficiaryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping
    public List<Beneficiary> getAllBeneficiaries() {
        return beneficiaryService.getAllBeneficiaries();
    }

    @GetMapping("/{id}")
    public Beneficiary getBeneficiaryById(@PathVariable Long id) {
        return beneficiaryService.getBeneficiaryById(id);
    }

    @PostMapping
    public Beneficiary createBeneficiary(@RequestBody Beneficiary beneficiary) {
        return beneficiaryService.saveBeneficiary(beneficiary);
    }

    @PutMapping("/{id}")
    public Beneficiary updateBeneficiary(@PathVariable Long id,
                                         @RequestBody Beneficiary beneficiaryDetails) {
        return beneficiaryService.updateBeneficiary(id, beneficiaryDetails);
    }

    @DeleteMapping("/{id}")
    public String deleteBeneficiary(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return "Beneficiary deleted successfully!";
    }
}