package br.com.locci.adscreen.auth.entity;

import br.com.locci.adscreen.user.entity.AppUser;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RefreshToken() {
    }

    public static RefreshToken create(AppUser user, String token, Instant expiresAt) {
        RefreshToken rt = new RefreshToken();
        rt.user = user;
        rt.token = token;
        rt.expiresAt = expiresAt;
        rt.revoked = false;
        rt.createdAt = Instant.now();
        return rt;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public UUID getId() { return id; }
    public AppUser getUser() { return user; }
    public String getToken() { return token; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public Instant getCreatedAt() { return createdAt; }
}
