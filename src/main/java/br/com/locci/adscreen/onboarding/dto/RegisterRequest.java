package br.com.locci.adscreen.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Nome da organização é obrigatório.")
        String organizationName,

        @NotBlank(message = "Seu nome é obrigatório.")
        String name,

        @NotBlank(message = "E-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "Senha é obrigatória.")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres.")
        String password
) {
}
