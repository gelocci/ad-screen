package br.com.locci.adscreen.onboarding.dto;

public record RegisterRequest(
        String organizationName,
        String name,
        String email,
        String password
) {
}
