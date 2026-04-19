package br.com.locci.adscreen.activation.controller;

import br.com.locci.adscreen.activation.entity.ActivationSession;
import br.com.locci.adscreen.activation.entity.ActivationSessionStatus;
import br.com.locci.adscreen.activation.service.PairingService;
import br.com.locci.adscreen.organization.dto.OrganizationOptionResponse;
import br.com.locci.adscreen.organization.entity.Organization;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class PairController {

    private final PairingService pairingService;

    public PairController(PairingService pairingService) {
        this.pairingService = pairingService;
    }

    @GetMapping("/pair")
    public String pair(@RequestParam(required = false) String token,
                       Authentication auth,
                       Model model) {

        model.addAttribute("activationToken", token);
        model.addAttribute("pageTitle", "Parear tela");

        try {
            ActivationSession session = pairingService.getSession(token);
            session = pairingService.expireIfNeeded(session);

            List<Organization> orgs = pairingService.getUserOrganizations(auth.getName());

            return render(model, session, orgs);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pair/pair";
        }
    }

    @PostMapping("/pair/confirm")
    public String confirm(@RequestParam String token,
                          @RequestParam(required = false) UUID organizationId,
                          Authentication auth,
                          Model model) {

        model.addAttribute("activationToken", token);
        model.addAttribute("pageTitle", "Parear tela");

        try {
            ActivationSession session = pairingService.getSession(token);
            session = pairingService.expireIfNeeded(session);

            List<Organization> orgs = pairingService.getUserOrganizations(auth.getName());

            UUID resolvedOrg = pairingService.resolveOrganization(organizationId, orgs);

            session = pairingService.confirm(token, resolvedOrg);

            model.addAttribute("success", "Ativação realizada com sucesso.");
            return render(model, session, orgs);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pair/pair";
        }
    }

    private String render(Model model,
                          ActivationSession session,
                          List<Organization> orgs) {

        model.addAttribute("pageTitle", "Parear tela");
        model.addAttribute("status", session.getStatus().name());
        model.addAttribute("activationCode", session.getActivationCode());
        model.addAttribute("expiresAt", session.getExpiresAt());
        model.addAttribute("confirmed", session.getStatus() == ActivationSessionStatus.CONFIRMED);

        List<OrganizationOptionResponse> options = orgs.stream()
                .map(o -> new OrganizationOptionResponse(o.getId(), o.getName()))
                .toList();

        model.addAttribute("organizations", options);

        if (session.getStatus() == ActivationSessionStatus.EXPIRED
                && model.getAttribute("error") == null) {
            model.addAttribute("error", "Token expirado.");
        }

        if (session.getStatus() == ActivationSessionStatus.CONFIRMED
                && model.getAttribute("success") == null) {
            model.addAttribute("success", "Esta tela já foi ativada.");
        }

        return "pair/pair";
    }
}
