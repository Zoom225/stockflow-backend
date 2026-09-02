WITH updated_demo_user AS (
    UPDATE users
    SET full_name = 'Démonstration StockFlow',
        email = 'demo@stockflow.app',
        password_hash = '$2a$10$Nd8mepJseeDr2Ow7I8Rcp.jxAbLJWVZMWBn25awq0aoep9TY.Sg62',
        role = 'ROLE_USER',
        updated_at = CURRENT_TIMESTAMP
    WHERE LOWER(email) = 'demo@stockflow.app'
    RETURNING id
)
INSERT INTO users (full_name, email, password_hash, role, created_at, updated_at)
SELECT
    'Démonstration StockFlow',
    'demo@stockflow.app',
    '$2a$10$Nd8mepJseeDr2Ow7I8Rcp.jxAbLJWVZMWBn25awq0aoep9TY.Sg62',
    'ROLE_USER',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM updated_demo_user);
