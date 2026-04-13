package br.com.locci.adscreen.admin.controller;

import br.com.locci.adscreen.admin.service.AdminContextService;
import br.com.locci.adscreen.organization.entity.Organization;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/org/switch")
public class AdminOrgSwitchController {

    private final AdminContextService adminContextService;

    public AdminOrgSwitchController(AdminContextService adminContextService) {
        this.adminContextService = adminContextService;
    }

    @GetMapping
    public String switchOrg(
            @RequestParam UUID orgId,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        List<Organization> orgs = adminContextService.getUserOrganizations(authentication);

        boolean valid = orgs.stream().anyMatch(o -> o.getId().equals(orgId));
        if (valid) {
            adminContextService.setCurrentOrgId(response, orgId);
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/admin");
    }
}
