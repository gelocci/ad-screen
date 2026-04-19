package br.com.locci.adscreen.organization.entity;

import br.com.locci.adscreen.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "organization")
public class Organization extends BaseAuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "active", nullable = false)
    private Boolean active;

    protected Organization() {
    }

    public static Organization create(final String name, final String slug) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Organization name is required.");
        }
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Organization slug is required.");
        }

        Organization organization = new Organization();
        organization.id = UUID.randomUUID();
        organization.name = name;
        organization.slug = slug;
        organization.active = Boolean.TRUE;
        return organization;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public Boolean getActive() {
        return active;
    }
}
