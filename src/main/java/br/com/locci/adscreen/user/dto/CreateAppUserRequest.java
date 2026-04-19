package br.com.locci.adscreen.user.dto;
public record CreateAppUserRequest(
        String name,
        String email,
        String password
) {
}
