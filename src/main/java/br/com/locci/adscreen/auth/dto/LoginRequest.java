package br.com.locci.adscreen.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
