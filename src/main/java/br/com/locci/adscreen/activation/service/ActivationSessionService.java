package br.com.locci.adscreen.activation.service;

import br.com.locci.adscreen.activation.entity.ActivationSession;
import br.com.locci.adscreen.activation.entity.ActivationSessionStatus;
import br.com.locci.adscreen.activation.repository.ActivationSessionRepository;
import br.com.locci.adscreen.screen.entity.Screen;
import br.com.locci.adscreen.screen.repository.ScreenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ActivationSessionService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ActivationSessionRepository repository;
    private final ScreenRepository screenRepository;

    public ActivationSessionService(
            ActivationSessionRepository repository,
            ScreenRepository screenRepository
    ) {
        this.repository = repository;
        this.screenRepository = screenRepository;
    }

    public ActivationSession createPending(UUID screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new IllegalArgumentException("Screen não encontrada."));

        Optional<ActivationSession> existing =
                repository.findByScreen_IdAndStatus(screenId, ActivationSessionStatus.PENDING);

        if (existing.isPresent()) {
            ActivationSession session = existing.get();
            if (!isExpired(session)) {
                return session;
            }
            session.expire();
            repository.save(session);
        }

        String token = generateSecureToken();
        ActivationSession session = ActivationSession.createPending(screen, token);
        return repository.save(session);
    }

    public ActivationSession createPendingSession(UUID screenId) {
        return createPending(screenId);
    }

    @Transactional(readOnly = true)
    public Optional<ActivationSession> findPendingByScreenId(UUID screenId) {
        return repository.findByScreen_IdAndStatus(screenId, ActivationSessionStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public ActivationSession findByToken(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Sessão de ativação não encontrada."));
    }

    @Transactional(readOnly = true)
    public Optional<ActivationSession> findOptionalByToken(String token) {
        return repository.findByToken(token);
    }

    @Transactional(readOnly = true)
    public Optional<ActivationSession> findOptionalById(UUID id) {
        return repository.findById(id);
    }

    public ActivationSession findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sessão de ativação não encontrada."));
    }

    public ActivationSession confirm(UUID id) {
        ActivationSession session = findById(id);

        if (session.getStatus() != ActivationSessionStatus.PENDING) {
            throw new IllegalArgumentException("A sessão não está mais pendente.");
        }

        if (isExpired(session)) {
            session.expire();
            repository.save(session);
            throw new IllegalArgumentException("O link de ativação expirou.");
        }

        session.confirm();
        return repository.save(session);
    }

    public ActivationSession confirmByToken(String token) {
        ActivationSession session = findByToken(token);

        if (session.getStatus() != ActivationSessionStatus.PENDING) {
            throw new IllegalArgumentException("A sessão não está mais pendente.");
        }

        if (isExpired(session)) {
            session.expire();
            repository.save(session);
            throw new IllegalArgumentException("O link de ativação expirou.");
        }

        session.confirm();
        return repository.save(session);
    }

    public ActivationSession expire(UUID id) {
        ActivationSession session = findById(id);
        session.expire();
        return repository.save(session);
    }

    @Transactional(readOnly = true)
    public boolean isExpired(ActivationSession session) {
        return session.getExpiresAt() != null && session.getExpiresAt().isBefore(Instant.now());
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[24];
        SECURE_RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
