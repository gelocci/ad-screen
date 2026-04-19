package br.com.locci.adscreen.organization.service;

import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(final OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization create(final Organization organization) {
        if (organizationRepository.existsBySlug(organization.getSlug())) {
            throw new IllegalArgumentException("Organization slug already exists.");
        }

        return organizationRepository.save(organization);
    }

    public Organization findById(final UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found."));
    }

    public Organization findBySlug(final String slug) {
        return organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found."));
    }

    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }
}
