package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.organization.service.OrganizationService;
import br.com.locci.adscreen.screen.service.ScreenService;
import br.com.locci.adscreen.user.service.AppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final OrganizationService organizationService;
    private final AppUserService appUserService;
    private final ScreenService screenService;

    public AdminDashboardController(
            OrganizationService organizationService,
            AppUserService appUserService,
            ScreenService screenService
    ) {
        this.organizationService = organizationService;
        this.appUserService = appUserService;
        this.screenService = screenService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalOrganizations", organizationService.findAll().size());
        model.addAttribute("totalUsers", appUserService.findAll().size());
        model.addAttribute("totalScreens", screenService.findAll().size());
        model.addAttribute("pendingScreens", screenService.findAll().stream()
                .filter(s -> s.getStatus().name().equals("PENDING"))
                .count());
        model.addAttribute("activeScreens", screenService.findAll().stream()
                .filter(s -> s.getStatus().name().equals("ACTIVE"))
                .count());
        return "admin/dashboard";
    }
}
