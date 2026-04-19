package br.com.locci.adscreen.organization.dto;
import java.util.UUID;
public record OrganizationResponse(
        UUID id,
        String name,
        String slug,
        Boolean active
) {
}
