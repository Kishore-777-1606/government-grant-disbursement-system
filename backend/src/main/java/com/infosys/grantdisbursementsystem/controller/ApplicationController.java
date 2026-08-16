package com.infosys.grantdisbursementsystem.controller;

import com.infosys.grantdisbursementsystem.entity.Application;
import com.infosys.grantdisbursementsystem.service.ApplicationService;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {


    private final ApplicationService service;


    public ApplicationController(ApplicationService service){
        this.service = service;
    }



  @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'ADMIN')")
    @PostMapping
    public Application submitApplication(
            @RequestBody Application application) {

        return service.submitApplication(application);
    }



    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @GetMapping
    public List<Application> getAllApplications() {

        return service.getAllApplications();
    }



    @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @GetMapping("/{id}")
    public Application getApplicationById(
            @PathVariable @NonNull Long id) {

        return service.getApplicationById(id);
    }



   @PreAuthorize("hasAnyRole('FIELD_OFFICER', 'DISTRICT_OFFICER', 'ADMIN')")
    @PutMapping("/{id}")
    public Application updateApplication(
            @PathVariable @NonNull Long id,
            @RequestBody Application application) {

        return service.updateApplication(id, application);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteApplication(
            @PathVariable @NonNull Long id) {

        service.deleteApplication(id);

        return "Application deleted successfully";
    }

}