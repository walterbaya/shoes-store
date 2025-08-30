-- Inserción de usuarios (bcrypt sin tocar)
INSERT INTO users (username, created_at, password, full_name, role, email) VALUES
    ('admin',        NOW(), '$2a$12$Dvlnp.XiHtxgjrmhlINPfOElXDej4TmCfq4ioXuo3NwvHtJduKHn.', 'Administrador Principal', 'ROLE_ADMIN',        'admin@example.com'),
    ('seller1',      NOW(), '$2a$12$Dvlnp.XiHtxgjrmhlINPfOElXDej4TmCfq4ioXuo3NwvHtJduKHn.', 'Vendedor Uno',         'ROLE_SELLER',       'seller1@example.com'),
    ('seller2',      NOW(), '$2a$12$Dvlnp.XiHtxgjrmhlINPfOElXDej4TmCfq4ioXuo3NwvHtJduKHn.', 'Vendedor Dos',         'ROLE_SELLER',       'seller2@example.com'),
    ('seller3',      NOW(), '$2a$12$Dvlnp.XiHtxgjrmhlINPfOElXDej4TmCfq4ioXuo3NwvHtJduKHn.', 'Vendedor Tres',        'ROLE_SELLER',       'seller3@example.com'),
    ('stockmanager', NOW(), '$2a$12$Dvlnp.XiHtxgjrmhlINPfOElXDej4TmCfq4ioXuo3NwvHtJduKHn.', 'Gestors de Stock',     'ROLE_STOCK_MANAGER',       'stockmanager@example.com');
