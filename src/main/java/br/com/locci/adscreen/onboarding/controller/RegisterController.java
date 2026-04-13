package br.com.locci.adscreen.onboarding.controller;

import br.com.locci.adscreen.onboarding.dto.RegisterRequest;
import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.service.OrganizationService;
import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.entity.OrganizationUser;
import br.com.locci.adscreen.user.entity.OrganizationUserRole;
import br.com.locci.adscreen.user.service.AppUserService;
import br.com.locci.adscreen.user.service.OrganizationUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final OrganizationService organizationService;
    private final AppUserService appUserService;
    private final OrganizationUserService organizationUserService;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(
            OrganizationService organizationService,
            AppUserService appUserService,
            OrganizationUserService organizationUserService,
            PasswordEncoder passwordEncoder
    ) {
        this.organizationService = organizationService;
        this.appUserService = appUserService;
        this.organizationUserService = organizationUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String form() {
        return "onboarding/register";
    }

    @PostMapping
    public String register(
            @ModelAttribute RegisterRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String slug = request.organizationName()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("^-|-$", "");

            Organization organization = organizationService.create(
                    Organization.create(request.organizationName(), slug)
            );

            AppUser user = appUserService.create(
                    AppUser.create(request.name(), request.email(),
                            passwordEncoder.encode(request.password()))
            );

            organizationUserService.linkUserToOrganization(
                    organization.getId(),
                    user.getId(),
                    OrganizationUserRole.OWNER
            );

            redirectAttributes.addFlashAttribute("success",
                    "Conta criada com sucesso. Faça login para continuar.");
            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("request", request);
            return "onboarding/register";
        }
    }
}
