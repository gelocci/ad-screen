package br.com.locci.adscreen.screen.dto;
import br.com.locci.adscreen.screen.entity.ScreenStatus;
import java.util.UUID;
public record ScreenResponse(
        UUID id,
        UUID organizationId,
        String name,
        String description,
        ScreenStatus status
) {
}
