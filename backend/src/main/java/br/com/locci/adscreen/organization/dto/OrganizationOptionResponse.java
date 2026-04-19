package br.com.locci.adscreen.organization.dto;

import java.util.UUID;

public record OrganizationOptionResponse(
        UUID id,
        String name
) {
}
