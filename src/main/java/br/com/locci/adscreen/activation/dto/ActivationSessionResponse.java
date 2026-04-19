package br.com.locci.adscreen.activation.dto;
import br.com.locci.adscreen.activation.entity.ActivationSessionStatus;
import java.time.Instant;
import java.util.UUID;
public record ActivationSessionResponse(
        UUID id,
        UUID screenId,
        UUID activationCode,
        ActivationSessionStatus status,
        Instant expiresAt,
        Instant activatedAt
) {
}
