package br.com.locci.adscreen.activation.entity;

import br.com.locci.adscreen.screen.entity.Screen;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "activation_session")
public class ActivationSession {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "activation_code", nullable = false, unique = true)
    private UUID activationCode;

    @Column(name = "token", nullable = false, unique = true, length = 64)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ActivationSessionStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ActivationSession() {
    }

    public static ActivationSession createPending(Screen screen, String token) {
        ActivationSession session = new ActivationSession();
        session.screen = screen;
        session.activationCode = UUID.randomUUID();
        session.token = token;
        session.status = ActivationSessionStatus.PENDING;
        session.createdAt = Instant.now();
        session.expiresAt = Instant.now().plusSeconds(600);
        return session;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void confirm() {
        this.status = ActivationSessionStatus.CONFIRMED;
        this.activatedAt = Instant.now();
    }

    public void expire() {
        this.status = ActivationSessionStatus.EXPIRED;
    }

    public UUID getId() { return id; }
    public Screen getScreen() { return screen; }
    public UUID getActivationCode() { return activationCode; }
    public String getToken() { return token; }
    public ActivationSessionStatus getStatus() { return status; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getActivatedAt() { return activatedAt; }
    public Instant getCreatedAt() { return createdAt; }
}
