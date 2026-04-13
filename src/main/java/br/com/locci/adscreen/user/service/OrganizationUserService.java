package br.com.locci.adscreen.user.service;

import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.service.OrganizationService;
import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.entity.OrganizationUser;
import br.com.locci.adscreen.user.entity.OrganizationUserRole;
import br.com.locci.adscreen.user.repository.OrganizationUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationUserService {

    private final OrganizationUserRepository repository;
    private final OrganizationService organizationService;
    private final AppUserService appUserService;

    public OrganizationUserService(
            final OrganizationUserRepository repository,
            final OrganizationService organizationService,
            final AppUserService appUserService
    ) {
        this.repository = repository;
        this.organizationService = organizationService;
        this.appUserService = appUserService;
    }

    public OrganizationUser linkUserToOrganization(
            final UUID organizationId,
            final UUID userId,
            final OrganizationUserRole role
    ) {
        Organization organization = organizationService.findById(organizationId);
        AppUser user = appUserService.findById(userId);

        OrganizationUser entity = OrganizationUser.link(organization, user, role);

        return repository.save(entity);
    }

    public List<OrganizationUser> findByOrganizationId(final UUID organizationId) {
        return repository.findByIdOrganizationId(organizationId);
    }

    public List<OrganizationUser> findByOrganizationIdFetchUser(final UUID organizationId) {
        return repository.findByOrganizationIdFetchUser(organizationId);
    }

    public List<OrganizationUser> findByUserId(final UUID userId) {
        return repository.findByIdUserId(userId);
    }
}
