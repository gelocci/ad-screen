CREATE TABLE activation_session (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    screen_id       UUID NOT NULL REFERENCES screen(id) ON DELETE CASCADE,
    activation_code UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    token           VARCHAR(64) NOT NULL UNIQUE,
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    expires_at      TIMESTAMP NOT NULL,
    activated_at    TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_activation_session_status
        CHECK (status IN ('PENDING', 'CONFIRMED', 'EXPIRED', 'CANCELLED'))
);

CREATE INDEX idx_activation_session_screen_id
    ON activation_session(screen_id);

CREATE INDEX idx_activation_session_activation_code
    ON activation_session(activation_code);

CREATE INDEX idx_activation_session_status
    ON activation_session(status);
