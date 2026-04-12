INSERT INTO organization (id, name, slug, active, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Locci',
    'locci',
    TRUE,
    NOW(),
    NOW()
);

INSERT INTO app_user (id, name, email, password_hash, active, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000010',
    'Admin',
    'admin@locci.com.br',
    '$2a$10$3UH/RD9kvaGDmS4yiwV8I.Vf054y.dxsuc.6zxO/0tgHQuU.WvgVW',
    TRUE,
    NOW(),
    NOW()
);

INSERT INTO organization_user (organization_id, user_id, role, joined_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000010',
    'OWNER',
    NOW()
);
