package br.com.locci.adscreen.screen.controller;
import br.com.locci.adscreen.screen.dto.AssignScreenOrganizationRequest;
import br.com.locci.adscreen.screen.dto.CreateScreenRequest;
import br.com.locci.adscreen.screen.dto.ScreenResponse;
import br.com.locci.adscreen.screen.entity.Screen;
import br.com.locci.adscreen.screen.service.ScreenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/screens")
public class ScreenController {
    private final ScreenService screenService;
    public ScreenController(final ScreenService screenService) {
        this.screenService = screenService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScreenResponse create(@RequestBody final CreateScreenRequest request) {
        Screen saved = screenService.createPendingScreen(request.name(), request.description());
        return toResponse(saved);
    }
    @PostMapping("/{screenId}/organization")
    public ScreenResponse assignOrganization(
            @PathVariable final UUID screenId,
            @RequestBody final AssignScreenOrganizationRequest request
    ) {
        Screen saved = screenService.assignOrganization(screenId, request.organizationId());
        return toResponse(saved);
    }
    private ScreenResponse toResponse(final Screen screen) {
        return new ScreenResponse(
                screen.getId(),
                screen.getOrganization() != null ? screen.getOrganization().getId() : null,
                screen.getName(),
                screen.getDescription(),
                screen.getStatus()
        );
    }
}
