package br.com.locci.adscreen.admin.service;

import br.com.locci.adscreen.organization.entity.Organization;
import br.com.locci.adscreen.user.entity.AppUser;
import br.com.locci.adscreen.user.service.AppUserService;
import br.com.locci.adscreen.user.service.OrganizationAccessService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminContextService {

    private static final String CURRENT_ORG_COOKIE = "currentOrgId";

    private final AppUserService appUserService;
    private final OrganizationAccessService organizationAccessService;

    public AdminContextService(
            AppUserService appUserService,
            OrganizationAccessService organizationAccessService
    ) {
        this.appUserService = appUserService;
        this.organizationAccessService = organizationAccessService;
    }

    public boolean isSuperAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
    }

    public AppUser getAuthenticatedUser(Authentication authentication) {
        return appUserService.findByEmail(authentication.getName());
    }

    public List<Organization> getUserOrganizations(Authentication authentication) {
        return organizationAccessService.findOrganizationsByUserEmail(authentication.getName());
    }

    public Optional<UUID> getCurrentOrgId(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> CURRENT_ORG_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .map(v -> {
                    try { return UUID.fromString(v); }
                    catch (IllegalArgumentException e) { return null; }
                })
                .filter(v -> v != null)
                .findFirst();
    }

    public void setCurrentOrgId(HttpServletResponse response, UUID organizationId) {
        Cookie cookie = new Cookie(CURRENT_ORG_COOKIE, organizationId.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
    }

    public Organization resolveCurrentOrg(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        List<Organization> orgs = getUserOrganizations(authentication);

        if (orgs.isEmpty()) {
            throw new IllegalStateException("Usuário não possui organização vinculada.");
        }

        Optional<UUID> cookieOrgId = getCurrentOrgId(request);

        if (cookieOrgId.isPresent()) {
            Optional<Organization> org = orgs.stream()
                    .filter(o -> o.getId().equals(cookieOrgId.get()))
                    .findFirst();
            if (org.isPresent()) return org.get();
        }

        Organization defaultOrg = orgs.get(0);
        setCurrentOrgId(response, defaultOrg.getId());
        return defaultOrg;
    }
}
