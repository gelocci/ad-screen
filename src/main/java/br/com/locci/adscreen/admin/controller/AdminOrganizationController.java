package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.service.OrganizationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/organizations")
public class AdminOrganizationController {

    private final OrganizationService organizationService;

    public AdminOrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("organizations", organizationService.findAll());
        return "admin/organizations/list";
    }

    @GetMapping("/new")
    public String newForm() {
        return "admin/organizations/form";
    }

    @PostMapping
    public String create(
            @RequestParam String name,
            @RequestParam String slug,
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
