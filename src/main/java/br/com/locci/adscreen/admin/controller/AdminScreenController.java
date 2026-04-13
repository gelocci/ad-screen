package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.admin.service.AdminContextService;
import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.screen.service.ScreenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/screens")
public class AdminScreenController {

    private final ScreenService screenService;
    private final AdminContextService adminContextService;

    public AdminScreenController(
            ScreenService screenService,
            AdminContextService adminContextService
    ) {
        this.screenService = screenService;
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
            model.addAttribute("screens", screenService.findAll());
        } else {
            Organization currentOrg = adminContextService.resolveCurrentOrg(
                    authentication, request, response);
            model.addAttribute("currentOrg", currentOrg);
            model.addAttribute("userOrgs", adminContextService.getUserOrganizations(authentication));
            model.addAttribute("screens",
                    screenService.findByOrganizationId(currentOrg.getId()));
        }

        return "admin/screens/list";
    }
}
