package br.com.locci.adscreen.user.dto;
import java.util.UUID;
public record AppUserResponse(
        UUID id,
        String name,
        String email,
        Boolean active
) {
}
