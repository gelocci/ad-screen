CREATE TABLE organization_user (
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role            VARCHAR(20) NOT NULL DEFAULT 'VIEWER',
    joined_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (organization_id, user_id),
    CONSTRAINT chk_organization_user_role
        CHECK (role IN ('OWNER', 'ADMIN', 'EDITOR', 'VIEWER'))
);

CREATE INDEX idx_organization_user_user_id
    ON organization_user(user_id);
CREATE INDEX idx_organization_user_organization_id
    ON organization_user(organization_id);
