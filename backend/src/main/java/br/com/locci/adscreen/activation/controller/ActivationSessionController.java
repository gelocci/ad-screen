package br.com.locci.adscreen.activation.controller;

import br.com.locci.adscreen.activation.dto.ActivationSessionResponse;
import br.com.locci.adscreen.activation.dto.CreateActivationSessionRequest;
import br.com.locci.adscreen.activation.entity.ActivationSession;
import br.com.locci.adscreen.activation.service.ActivationSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/activation-sessions")
public class ActivationSessionController {

    private final ActivationSessionService service;

    public ActivationSessionController(ActivationSessionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivationSessionResponse create(@RequestBody CreateActivationSessionRequest request) {
        ActivationSession session = service.createPending(request.screenId());
        return toResponse(session);
    }

    @PostMapping("/confirm-by-token")
    public ActivationSessionResponse confirmByToken(@RequestParam String token) {
        ActivationSession session = service.confirmByToken(token);
        return toResponse(session);
    }

    @PostMapping("/{id}/expire")
    public ActivationSessionResponse expire(@PathVariable UUID id) {
        ActivationSession session = service.expire(id);
        return toResponse(session);
    }

    private ActivationSessionResponse toResponse(ActivationSession session) {
        return new ActivationSessionResponse(
                session.getId(),
                session.getScreen().getId(),
                session.getActivationCode(),
                session.getStatus(),
                session.getExpiresAt(),
                session.getActivatedAt()
        );
    }
}
