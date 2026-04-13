package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.admin.service.AdminContextService;
import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.service.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/organizations")
public class AdminOrganizationController {

    private final OrganizationService organizationService;
    private final AdminContextService adminContextService;

    public AdminOrganizationController(
            OrganizationService organizationService,
            AdminContextService adminContextService
    ) {
        this.organizationService = organizationService;
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

        List<Organization> organizations;
        if (superAdmin) {
            organizations = organizationService.findAll();
        } else {
            organizations = adminContextService.getUserOrganizations(authentication);
            Organization currentOrg = adminContextService.resolveCurrentOrg(
                    authentication, request, response);
            model.addAttribute("currentOrg", currentOrg);
            model.addAttribute("userOrgs", adminContextService.getUserOrganizations(authentication));
        }

        model.addAttribute("organizations", organizations);
        return "admin/organizations/list";
    }

    @GetMapping("/new")
    public String newForm(Authentication authentication, Model model) {
        model.addAttribute("superAdmin", adminContextService.isSuperAdmin(authentication));
        return "admin/organizations/form";
    }

    @PostMapping
    public String create(
            @RequestParam String name,
            @RequestParam String slug,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            organizationService.create(Organization.create(name, slug));
            redirectAttributes.addFlashAttribute("success", "Organização criada com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/organizations/new";
        }
        return "redirect:/admin/organizations";
    }
}
