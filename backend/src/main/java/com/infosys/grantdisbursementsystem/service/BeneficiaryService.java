package com.infosys.grantdisbursementsystem.service;

import com.infosys.grantdisbursementsystem.entity.Beneficiary;
import com.infosys.grantdisbursementsystem.exception.ResourceNotFoundException;
import com.infosys.grantdisbursementsystem.repository.BeneficiaryRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class BeneficiaryService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private final BeneficiaryRepository beneficiaryRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository
    ) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public List<Beneficiary> getAllBeneficiaries() {

        return beneficiaryRepository.findAll();

    }

    public Beneficiary getBeneficiaryById(Long id) {

        return beneficiaryRepository.findById(
                Objects.requireNonNull(id)
        )
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Beneficiary not found with ID: " + id
                )
        );

    }

    public Beneficiary saveBeneficiary(
            Beneficiary beneficiary
    ) {

        return beneficiaryRepository.save(
                Objects.requireNonNull(beneficiary)
        );

    }

    public Beneficiary updateBeneficiary(
            Long id,
            Beneficiary beneficiaryDetails
    ) {

        Beneficiary beneficiary =
                beneficiaryRepository.findById(
                        Objects.requireNonNull(id)
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Beneficiary not found with ID: " + id
                        )
                );

        beneficiary.setBeneficiaryUid(
                beneficiaryDetails.getBeneficiaryUid()
        );

        beneficiary.setFirstName(
                beneficiaryDetails.getFirstName()
        );

        beneficiary.setLastName(
                beneficiaryDetails.getLastName()
        );

        beneficiary.setDateOfBirth(
                beneficiaryDetails.getDateOfBirth()
        );

        beneficiary.setGender(
                beneficiaryDetails.getGender()
        );

        beneficiary.setMobileNumber(
                beneficiaryDetails.getMobileNumber()
        );

        beneficiary.setEmail(
                beneficiaryDetails.getEmail()
        );

        beneficiary.setAddressLine1(
                beneficiaryDetails.getAddressLine1()
        );

        beneficiary.setAddressLine2(
                beneficiaryDetails.getAddressLine2()
        );

        beneficiary.setVillageId(
                beneficiaryDetails.getVillageId()
        );

        beneficiary.setBlockId(
                beneficiaryDetails.getBlockId()
        );

        beneficiary.setDistrictId(
                beneficiaryDetails.getDistrictId()
        );

        beneficiary.setStateId(
                beneficiaryDetails.getStateId()
        );

        beneficiary.setPincode(
                beneficiaryDetails.getPincode()
        );

        beneficiary.setBankAccountNumber(
                beneficiaryDetails.getBankAccountNumber()
        );

        beneficiary.setIfscCode(
                beneficiaryDetails.getIfscCode()
        );

        beneficiary.setBankName(
                beneficiaryDetails.getBankName()
        );

        beneficiary.setAadhaarVerified(
                beneficiaryDetails.getAadhaarVerified()
        );

        beneficiary.setBankVerified(
        beneficiaryDetails.getBankVerified()
);

beneficiary.setCategory(
        beneficiaryDetails.getCategory()
);

beneficiary.setAnnualIncome(
        beneficiaryDetails.getAnnualIncome()
);

beneficiary.setDisabilityStatus(
        beneficiaryDetails.getDisabilityStatus()
);

beneficiary.setIsActive(
        beneficiaryDetails.getIsActive()
);

        return beneficiaryRepository.save(
                beneficiary
        );

    }

    // NEW: Update Aadhaar and bank verification status
    public Beneficiary updateVerificationStatus(
            Long id,
            Boolean aadhaarVerified,
            Boolean bankVerified
    ) {

        Beneficiary beneficiary = getBeneficiaryById(id);

        if (aadhaarVerified != null) {
            beneficiary.setAadhaarVerified(aadhaarVerified);
        }

        if (bankVerified != null) {
            beneficiary.setBankVerified(bankVerified);
        }

        return beneficiaryRepository.save(beneficiary);
    }

    public void deleteBeneficiary(Long id) {

        Long beneficiaryId = Objects.requireNonNull(id);

        if (!beneficiaryRepository.existsById(
                beneficiaryId
        )) {

            throw new ResourceNotFoundException(
                    "Beneficiary not found with ID: " + beneficiaryId
            );

        }

        beneficiaryRepository.deleteById(
                beneficiaryId
        );

    }

    /**
     * Saves an uploaded identity/eligibility proof document to disk and
     * records it against the beneficiary (Module 1: "document upload, and
     * identity validation"). Replaces any previously uploaded document for
     * this beneficiary — a beneficiary has at most one document on file.
     */
    public Beneficiary uploadDocument(
            Long id,
            MultipartFile file
    ) {

        Beneficiary beneficiary = getBeneficiaryById(
                Objects.requireNonNull(id)
        );

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "No file was uploaded"
            );

        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {

            throw new IllegalArgumentException(
                    "Only PDF, JPEG, or PNG files are allowed"
            );

        }

        try {

            Path uploadPath = Paths.get(uploadDir);

            Files.createDirectories(uploadPath);

            String originalName = Objects.requireNonNullElse(
                    file.getOriginalFilename(),
                    "document"
            );

            String extension = "";

            int dotIndex = originalName.lastIndexOf('.');

            if (dotIndex >= 0) {
                extension = originalName.substring(dotIndex);
            }

            // Disk-safe, collision-proof filename — the user-facing name is
            // kept separately in documentOriginalName for display/download.
            String storedFileName =
                    "beneficiary-" + id + "-" + UUID.randomUUID() + extension;

            Path destination = uploadPath.resolve(storedFileName);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Remove the previous file, if any, now that the new one is
            // safely written — avoids leaving orphaned files behind on disk.
            if (beneficiary.getDocumentPath() != null) {

                Files.deleteIfExists(
                        uploadPath.resolve(beneficiary.getDocumentPath())
                );

            }

            beneficiary.setDocumentPath(storedFileName);
            beneficiary.setDocumentOriginalName(originalName);

            return beneficiaryRepository.save(beneficiary);

        } catch (IOException e) {

            throw new UncheckedIOException(
                    "Failed to store uploaded document",
                    e
            );

        }

    }

    /**
     * Loads a beneficiary's stored document from disk for download/viewing.
     */
    public Resource loadDocument(Long id) {

        Beneficiary beneficiary = getBeneficiaryById(
                Objects.requireNonNull(id)
        );

        if (beneficiary.getDocumentPath() == null) {

            throw new ResourceNotFoundException(
                    "No document has been uploaded for beneficiary ID: " + id
            );

        }

        try {

            Path filePath =
                    Paths.get(uploadDir)
                    .resolve(beneficiary.getDocumentPath());

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {

                throw new ResourceNotFoundException(
                        "Stored document could not be read for beneficiary ID: " + id
                );

            }

            return resource;

        } catch (MalformedURLException e) {

            throw new ResourceNotFoundException(
                    "Stored document path is invalid for beneficiary ID: " + id
            );

        }

    }

}