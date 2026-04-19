package br.com.locci.adscreen.activation.dto;
import java.util.UUID;
public record CreateActivationSessionRequest(
        UUID screenId
) {
}
