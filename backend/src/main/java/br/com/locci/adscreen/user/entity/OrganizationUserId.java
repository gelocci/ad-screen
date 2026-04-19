package br.com.locci.adscreen.user.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
@Embeddable
public class OrganizationUserId implements Serializable {
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    protected OrganizationUserId() {
    }
    public OrganizationUserId(final UUID organizationId, final UUID userId) {
        this.organizationId = organizationId;
        this.userId = userId;
    }
    public UUID getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(final UUID organizationId) {
        this.organizationId = organizationId;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(final UUID userId) {
        this.userId = userId;
    }
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganizationUserId that)) {
            return false;
        }
        return Objects.equals(organizationId, that.organizationId)
                && Objects.equals(userId, that.userId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(organizationId, userId);
    }
}
