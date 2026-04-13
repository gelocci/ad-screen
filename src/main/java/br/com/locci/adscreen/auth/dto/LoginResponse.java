package br.com.locci.adscreen.auth.dto;

public record LoginResponse(
        String email,
        String name,
        String globalRole
) {
}
