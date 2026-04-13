package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.admin.service.AdminContextService;
import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.service.OrganizationService;
import br.com.locci.adscreen.screen.entity.ScreenStatus;
import br.com.locci.adscreen.screen.service.ScreenService;
import br.com.locci.adscreen.user.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final OrganizationService organizationService;
    private final AppUserService appUserService;
    private final ScreenService screenService;
    private final AdminContextService adminContextService;

    public AdminDashboardController(
            OrganizationService organizationService,
            AppUserService appUserService,
            ScreenService screenService,
            AdminContextService adminContextService
    ) {
        this.organizationService = organizationService;
        this.appUserService = appUserService;
        this.screenService = screenService;
        this.adminContextService = adminContextService;
    }

    @GetMapping
    public String dashboard(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        boolean superAdmin = adminContextService.isSuperAdmin(authentication);
        model.addAttribute("superAdmin", superAdmin);

        if (superAdmin) {
            model.addAttribute("totalOrganizations", organizationService.findAll().size());
            model.addAttribute("totalUsers", appUserService.findAll().size());
            model.addAttribute("totalScreens", screenService.findAll().size());
            model.addAttribute("pendingScreens", screenService.findAll().stream()
                    .filter(s -> s.getStatus() == ScreenStatus.PENDING).count());
            model.addAttribute("activeScreens", screenService.findAll().stream()
                    .filter(s -> s.getStatus() == ScreenStatus.ACTIVE).count());
        } else {
            Organization currentOrg = adminContextService.resolveCurrentOrg(
                    authentication, request, response);
            List<Organization> userOrgs = adminContextService.getUserOrganizations(authentication);

            model.addAttribute("currentOrg", currentOrg);
            model.addAttribute("userOrgs", userOrgs);
            model.addAttribute("totalScreens",
                    screenService.findByOrganizationId(currentOrg.getId()).size());
            model.addAttribute("pendingScreens",
                    screenService.findByOrganizationIdAndStatus(
                            currentOrg.getId(), ScreenStatus.PENDING).size());
            model.addAttribute("activeScreens",
                    screenService.findByOrganizationIdAndStatus(
                            currentOrg.getId(), ScreenStatus.ACTIVE).size());
        }

        return "admin/dashboard";
    }
}
