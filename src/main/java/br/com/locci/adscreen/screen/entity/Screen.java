package br.com.locci.adscreen.screen.entity;

import br.com.locci.adscreen.common.entity.BaseAuditableEntity;
import br.com.locci.adscreen.organization.entity.Organization;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "screen")
public class Screen extends BaseAuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ScreenStatus status;

    protected Screen() {}

    public static Screen create(final String name, final String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Screen name is required.");
        }

        Screen screen = new Screen();
        screen.id = UUID.randomUUID();
        screen.name = name;
        screen.description = description;
        screen.status = ScreenStatus.PENDING;
        return screen;
    }

    public void assignOrganization(final Organization organization) {
        this.organization = organization;

        if (this.status == ScreenStatus.PENDING) {
            this.status = ScreenStatus.ACTIVE;
        }
    }

    public UUID getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ScreenStatus getStatus() {
        return status;
    }
}
