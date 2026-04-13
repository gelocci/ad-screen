package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.admin.service.AdminContextService;
import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.service.OrganizationService;
import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.entity.OrganizationUserRole;
import br.com.locci.adscreen.user.service.AppUserService;
import br.com.locci.adscreen.user.service.OrganizationUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AppUserService appUserService;
    private final OrganizationService organizationService;
    private final OrganizationUserService organizationUserService;
    private final PasswordEncoder passwordEncoder;
    private final AdminContextService adminContextService;

    public AdminUserController(
            AppUserService appUserService,
            OrganizationService organizationService,
            OrganizationUserService organizationUserService,
            PasswordEncoder passwordEncoder,
            AdminContextService adminContextService
    ) {
        this.appUserService = appUserService;
        this.organizationService = organizationService;
        this.organizationUserService = organizationUserService;
        this.passwordEncoder = passwordEncoder;
        this.adminContextService = adminContextService;
    }

    @GetMapping
    public String list(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        boolean superAdmin = adminContextService.isSuperAdmin(authentication);
        model.addAttribute("superAdmin", superAdmin);

        if (superAdmin) {
            model.addAttribute("users", appUserService.findAll());
        } else {
            Organization currentOrg = adminContextService.resolveCurrentOrg(
                    authentication, request, response);
            model.addAttribute("currentOrg", currentOrg);
            model.addAttribute("userOrgs", adminContextService.getUserOrganizations(authentication));
            model.addAttribute("users", organizationUserService
                    .findByOrganizationIdFetchUser(currentOrg.getId()).stream()
                    .map(ou -> ou.getUser())
                    .toList());
        }

        return "admin/users/list";
    }

    @GetMapping("/new")
    public String newForm(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        boolean superAdmin = adminContextService.isSuperAdmin(authentication);
        model.addAttribute("superAdmin", superAdmin);
        model.addAttribute("roles", OrganizationUserRole.values());

        if (superAdmin) {
            model.addAttribute("organizations", organizationService.findAll());
        } else {
            Organization currentOrg = adminContextService.resolveCurrentOrg(
                    authentication, request, response);
            model.addAttribute("currentOrg", currentOrg);
            model.addAttribute("userOrgs", adminContextService.getUserOrganizations(authentication));
            model.addAttribute("organizations", List.of(currentOrg));
        }

        return "admin/users/form";
    }

    @PostMapping
    public String create(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String organizationId,
            @RequestParam OrganizationUserRole role,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        try {
            AppUser user = appUserService.create(
                    AppUser.create(name, email, passwordEncoder.encode(password))
            );

            organizationUserService.linkUserToOrganization(
                    java.util.UUID.fromString(organizationId),
                    user.getId(),
                    role
            );

            redirectAttributes.addFlashAttribute("success", "Usuário criado com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/new";
        }
        return "redirect:/admin/users";
    }
}
