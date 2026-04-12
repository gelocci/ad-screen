package br.com.locci.adscreen.organization.controller;

import br.com.locci.adscreen.organization.dto.CreateOrganizationRequest;
import br.com.locci.adscreen.organization.dto.OrganizationResponse;
import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(final OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationResponse create(@RequestBody final CreateOrganizationRequest request) {
        Organization saved = organizationService.create(
                Organization.create(request.name(), request.slug())
        );

        return new OrganizationResponse(
                saved.getId(),
                saved.getName(),
                saved.getSlug(),
                saved.getActive()
        );
    }
}
