package br.com.locci.adscreen.user.dto;
import br.com.locci.adscreen.user.entity.OrganizationUserRole;
import java.time.Instant;
import java.util.UUID;
public record OrganizationUserResponse(
        UUID organizationId,
        UUID userId,
        OrganizationUserRole role,
        Instant joinedAt
) {
}
