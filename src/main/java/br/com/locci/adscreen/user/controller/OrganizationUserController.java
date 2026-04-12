package br.com.locci.adscreen.user.controller;

import br.com.locci.adscreen.user.dto.LinkOrganizationUserRequest;
import br.com.locci.adscreen.user.dto.OrganizationUserResponse;
import br.com.locci.adscreen.user.entity.OrganizationUser;
import br.com.locci.adscreen.user.service.OrganizationUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/organizations")
public class OrganizationUserController {

    private final OrganizationUserService organizationUserService;

    public OrganizationUserController(OrganizationUserService organizationUserService) {
        this.organizationUserService = organizationUserService;
    }

    @PostMapping("/{organizationId}/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationUserResponse linkUser(
            @PathVariable UUID organizationId,
            @PathVariable UUID userId,
            @RequestBody LinkOrganizationUserRequest request
    ) {
        OrganizationUser link = organizationUserService.linkUserToOrganization(
                organizationId,
                userId,
                request.role()
        );
        return toResponse(link);
    }

    @GetMapping("/{organizationId}/users")
    public List<OrganizationUserResponse> findByOrganizationId(@PathVariable UUID organizationId) {
        return organizationUserService.findByOrganizationId(organizationId).stream()
                .map(this::toResponse)
                .toList();
    }

    private OrganizationUserResponse toResponse(OrganizationUser link) {
        return new OrganizationUserResponse(
                link.getOrganization().getId(),
                link.getUser().getId(),
                link.getRole(),
                link.getJoinedAt()
        );
    }
}
