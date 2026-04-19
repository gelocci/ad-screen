package br.com.locci.adscreen.user.dto;
import br.com.locci.adscreen.user.entity.OrganizationUserRole;
public record LinkOrganizationUserRequest(
        OrganizationUserRole role
) {
}
