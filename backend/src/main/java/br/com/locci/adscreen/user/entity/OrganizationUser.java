package br.com.locci.adscreen.user.entity;

import br.com.locci.adscreen.organization.entity.Organization;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "organization_user")
public class OrganizationUser {

    @EmbeddedId
    private OrganizationUserId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("organizationId")
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private OrganizationUserRole role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    protected OrganizationUser() {
    }

    public static OrganizationUser link(Organization organization, AppUser user, OrganizationUserRole role) {
        OrganizationUser link = new OrganizationUser();
        link.id = new OrganizationUserId(organization.getId(), user.getId());
        link.organization = organization;
        link.user = user;
        link.role = role;
        link.joinedAt = Instant.now();
        return link;
    }

    public OrganizationUserId getId() { return id; }
    public Organization getOrganization() { return organization; }
    public AppUser getUser() { return user; }
    public OrganizationUserRole getRole() { return role; }
    public Instant getJoinedAt() { return joinedAt; }
}
