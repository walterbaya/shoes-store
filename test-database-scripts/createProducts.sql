-- Selecciona tu base de datos
USE shoes_store;

-- Inserta varios productos de ejemplo en una sola operación
INSERT INTO products
(id, brand, color, created_at, description, gender, material, price, size, stock, type)
VALUES
    (1, 'Nike',       'Negro', NOW(), 'Zapatilla running ligera',            'HOMBRE', 'Mesh',      120.00, 'S42',  50, 'Sneakers'),
    (2, 'Adidas',     'Blanco',NOW(), 'Clásica de cuero con estilo retro',    'MUJER',  'Leather',    85.50, 'S38',  70, 'Casual'),
    (3, 'Puma',       'Rojo',  NOW(), 'Sneaker urbano con amortiguación',     'UNISEX', 'Textil',     99.99, 'S40', 100, 'Street'),
    (4, 'Reebok',     'Azul',  NOW(), 'Entrenamiento y gym',                 'HOMBRE', 'Mesh',       75.00, 'S44',  60, 'Sport'),
    (5, 'Vans',       'Negro', NOW(), 'Skate clásico con suela vulcanizada', 'NIÑO',   'Canvas',     65.00, 'S39',  80, 'Casual'),
    (6, 'Converse',   'Blanco',NOW(), 'All Star lona alta',                  'NIÑA',   'Canvas',     55.00, 'S37', 120, 'Casual'),
    (7, 'NewBalance', 'Gris',  NOW(), 'Running con soporte extra',           'HOMBRE', 'Mesh',      110.00, 'S43',  40, 'Sport'),
    (8, 'Skechers',   'Beige', NOW(), 'Cómoda y ligera para diario',         'MUJER',  'Synthetic',  70.00, 'S39',  65, 'Casual'),
    (9, 'UnderArmour','Negro', NOW(), 'Crossfit y alto impacto',             'HOMBRE', 'Textil',    130.00, 'S44',  55, 'Sport'),
    (10,'DrMartens',  'Marrón',NOW(), 'Botín de cuero con ribete clásico',    'UNISEX', 'Leather',   150.00, 'S40',  30, 'Boots')
;