package br.com.locci.adscreen.screen.service;

import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.repository.OrganizationRepository;
import br.com.locci.adscreen.screen.entity.Screen;
import br.com.locci.adscreen.screen.entity.ScreenStatus;
import br.com.locci.adscreen.screen.repository.ScreenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final OrganizationRepository organizationRepository;

    public ScreenService(
            ScreenRepository screenRepository,
            OrganizationRepository organizationRepository
    ) {
        this.screenRepository = screenRepository;
        this.organizationRepository = organizationRepository;
    }

    public Screen createPending(String name, String description) {
        Screen screen = Screen.create(name, description);
        return screenRepository.save(screen);
    }

    // compatibilidade com a Fase 3
    public Screen createPendingScreen(String name, String description) {
        return createPending(name, description);
    }

    @Transactional(readOnly = true)
    public Screen findById(UUID id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Screen não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Screen> findAll() {
        return screenRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Screen> findByOrganizationId(UUID organizationId) {
        return screenRepository.findByOrganization_Id(organizationId);
    }

    @Transactional(readOnly = true)
    public List<Screen> findByOrganizationIdAndStatus(UUID organizationId, ScreenStatus status) {
        return screenRepository.findByOrganization_IdAndStatus(organizationId, status);
    }

    public Screen assignOrganization(UUID screenId, UUID organizationId) {
        Screen screen = findById(screenId);

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada."));

        screen.assignOrganization(organization);
        return screenRepository.save(screen);
    }
}
