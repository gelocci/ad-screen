package br.com.locci.adscreen.user.entity;

import br.com.locci.adscreen.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "app_user")
public class AppUser extends BaseAuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "global_role", nullable = true, length = 20)
    private GlobalRole globalRole;

    @Column(name = "active", nullable = false)
    private Boolean active;

    protected AppUser() {
    }

    public static AppUser create(final String name, final String email, final String passwordHash) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name is required.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("User email is required.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("User password is required.");
        }

        AppUser user = new AppUser();
        user.id = UUID.randomUUID();
        user.name = name;
        user.email = email;
        user.passwordHash = passwordHash;
        user.globalRole = null;
        user.active = Boolean.TRUE;
        return user;
    }

    public boolean isSuperAdmin() {
        return globalRole == GlobalRole.SUPERADMIN;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public GlobalRole getGlobalRole() { return globalRole; }
    public Boolean getActive() { return active; }
}
