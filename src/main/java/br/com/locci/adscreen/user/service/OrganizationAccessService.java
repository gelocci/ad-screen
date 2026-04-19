package br.com.locci.adscreen.user.service;

import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.entity.OrganizationUser;
import br.com.locci.adscreen.user.repository.AppUserRepository;
import br.com.locci.adscreen.user.repository.OrganizationUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrganizationAccessService {

    private final AppUserRepository appUserRepository;
    private final OrganizationUserRepository organizationUserRepository;

    public OrganizationAccessService(
            AppUserRepository appUserRepository,
            OrganizationUserRepository organizationUserRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.organizationUserRepository = organizationUserRepository;
    }

    public AppUser findUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));
    }

    public List<Organization> findOrganizationsByUserEmail(String email) {
        List<OrganizationUser> links = organizationUserRepository.findByUserEmailFetchOrganization(email);

        return links.stream()
                .map(OrganizationUser::getOrganization)
                .toList();
    }
}
