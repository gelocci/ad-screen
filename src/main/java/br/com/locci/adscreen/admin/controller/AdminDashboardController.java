package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.admin.service.AdminContextService;
import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.organization.repository.OrganizationRepository;
import br.com.locci.adscreen.screen.entity.ScreenStatus;
import br.com.locci.adscreen.screen.repository.ScreenRepository;
import br.com.locci.adscreen.user.repository.AppUserRepository;
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

    private final OrganizationRepository organizationRepository;
    private final AppUserRepository appUserRepository;
    private final ScreenRepository screenRepository;
    private final AdminContextService adminContextService;

    public AdminDashboardController(
            OrganizationRepository organizationRepository,
            AppUserRepository appUserRepository,
            ScreenRepository screenRepository,
            AdminContextService adminContextService
    ) {
        this.organizationRepository = organizationRepository;
        this.appUserRepository = appUserRepository;
        this.screenRepository = screenRepository;
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
            model.addAttribute("totalOrganizations", organizationRepository.countBy());
            model.addAttribute("totalUsers", appUserRepository.countBy());
            model.addAttribute("totalScreens", screenRepository.countBy());
            model.addAttribute("activeScreens", screenRepository.countByStatus(ScreenStatus.ACTIVE));
            model.addAttribute("pendingScreens", screenRepository.countByStatus(ScreenStatus.PENDING));
        } else {
            Organization currentOrg = adminContextService.resolveCurrentOrg(
                    authentication, request, response);
            List<Organization> userOrgs = adminContextService.getUserOrganizations(authentication);

            model.addAttribute("currentOrg", currentOrg);
            model.addAttribute("userOrgs", userOrgs);
            model.addAttribute("totalScreens",
                    screenRepository.countByOrganization_Id(currentOrg.getId()));
            model.addAttribute("activeScreens",
                    screenRepository.countByOrganization_IdAndStatus(
                            currentOrg.getId(), ScreenStatus.ACTIVE));
            model.addAttribute("pendingScreens",
                    screenRepository.countByOrganization_IdAndStatus(
                            currentOrg.getId(), ScreenStatus.PENDING));
        }

        return "admin/dashboard";
    }
}
