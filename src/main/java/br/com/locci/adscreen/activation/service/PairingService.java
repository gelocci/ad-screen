package br.com.locci.adscreen.activation.service;

import br.com.locci.adscreen.activation.entity.ActivationSession;
import br.com.locci.adscreen.activation.entity.ActivationSessionStatus;
import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.screen.service.ScreenService;
import br.com.locci.adscreen.user.service.OrganizationAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PairingService {

    private final ActivationSessionService activationSessionService;
    private final ScreenService screenService;
    private final OrganizationAccessService organizationAccessService;

    public PairingService(
            ActivationSessionService activationSessionService,
            ScreenService screenService,
            OrganizationAccessService organizationAccessService
    ) {
        this.activationSessionService = activationSessionService;
        this.screenService = screenService;
        this.organizationAccessService = organizationAccessService;
    }

    @Transactional(readOnly = true)
    public ActivationSession getSession(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token de ativação não informado.");
        }
        return activationSessionService.findByToken(token);
    }

    public ActivationSession expireIfNeeded(ActivationSession session) {
        if (session.getStatus() == ActivationSessionStatus.PENDING &&
            activationSessionService.isExpired(session)) {
            return activationSessionService.expire(session.getId());
        }
        return session;
    }

    @Transactional(readOnly = true)
    public List<Organization> getUserOrganizations(String email) {
        List<Organization> orgs = organizationAccessService.findOrganizationsByUserEmail(email);

        if (orgs.isEmpty()) {
            throw new IllegalArgumentException("Usuário sem organização vinculada.");
        }

        return orgs;
    }

    @Transactional(readOnly = true)
    public UUID resolveOrganization(UUID requested, List<Organization> orgs) {

        if (orgs.size() == 1) {
            return orgs.get(0).getId();
        }

        if (requested == null) {
            throw new IllegalArgumentException("Selecione uma organização.");
        }

        boolean valid = orgs.stream()
                .anyMatch(o -> o.getId().equals(requested));

        if (!valid) {
            throw new IllegalArgumentException("Organização inválida.");
        }

        return requested;
    }

    public ActivationSession confirm(String token, UUID organizationId) {
        ActivationSession session = activationSessionService.confirmByToken(token);
        screenService.assignOrganization(session.getScreen().getId(), organizationId);
        return session;
    }
}
