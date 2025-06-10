-- Eliminar base de datos existente, crearla y seleccionarla
DROP DATABASE IF EXISTS shoes_store;
CREATE DATABASE shoes_store;
USE shoes_store;

-- Inserción de usuarios (bcrypt sin tocar)
INSERT INTO users (username, password, full_name, role) VALUES
                                                            ('admin',  '$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Administrador Principal', 'ROLE_ADMIN'),
                                                            ('seller1','$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Vendedor Uno',         'ROLE_SELLER'),
                                                            ('seller2','$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Vendedor Dos',         'ROLE_SELLER'),
                                                            ('seller3','$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Vendedor Tres',        'ROLE_SELLER');

-- Inserción de 50 productos (idéntico a tu lista original)
INSERT INTO product (name, description, size, price, stock) VALUES
                                                                ('Zapatilla Deportiva',        'Descripción de Zapatilla Deportiva',        'S35', 51.50, 20),
                                                                ('Zapato Formal',              'Descripción de Zapato Formal',              'S36', 53.00, 20),
                                                                ('Bota de Montaña',            'Descripción de Bota de Montaña',            'S37', 54.50, 20),
                                                                ('Botín Casual',               'Descripción de Botín Casual',               'S38', 56.00, 20),
                                                                ('Sandalia de Playa',          'Descripción de Sandalia de Playa',          'S39', 57.50, 20),
                                                                ('Deportivo Casual',           'Descripción de Deportivo Casual',           'S40', 59.00, 20),
                                                                ('Mocasín',                    'Descripción de Mocasín',                    'S41', 60.50, 20),
                                                                ('Alpargata',                  'Descripción de Alpargata',                  'S42', 62.00, 20),
                                                                ('Zapato Oxford',              'Descripción de Zapato Oxford',              'S43', 63.50, 20),
                                                                ('Zapato Derby',               'Descripción de Zapato Derby',               'S44', 65.00, 20),
                                                                ('Bota de Cuero',              'Descripción de Bota de Cuero',              'S35', 66.50, 20),
                                                                ('Bota Militar',               'Descripción de Bota Militar',               'S36', 68.00, 20),
                                                                ('Zapato Slip-On',             'Descripción de Zapato Slip-On',             'S37', 69.50, 20),
                                                                ('Zapatilla Urbana',           'Descripción de Zapatilla Urbana',           'S38', 71.00, 20),
                                                                ('Zapato de Vestir',           'Descripción de Zapato de Vestir',           'S39', 72.50, 20),
                                                                ('Zapato Brogue',              'Descripción de Zapato Brogue',              'S40', 74.00, 20),
                                                                ('Mocasín de Piel',            'Descripción de Mocasín de Piel',            'S41', 75.50, 20),
                                                                ('Zapato de Conducción',       'Descripción de Zapato de Conducción',       'S42', 77.00, 20),
                                                                ('Zapato Deportivo',           'Descripción de Zapato Deportivo',           'S43', 78.50, 20),
                                                                ('Zapato de Seguridad',        'Descripción de Zapato de Seguridad',        'S44', 80.00, 20),
                                                                ('Sandalia Casual',            'Descripción de Sandalia Casual',            'S35', 81.50, 20),
                                                                ('Chancla',                    'Descripción de Chancla',                    'S36', 83.00, 20),
                                                                ('Bota de Senderismo',         'Descripción de Bota de Senderismo',         'S37', 84.50, 20),
                                                                ('Botín de Montaña',           'Descripción de Botín de Montaña',           'S38', 86.00, 20),
                                                                ('Zapato Náutico',             'Descripción de Zapato Náutico',             'S39', 87.50, 20),
                                                                ('Zapato de Running',          'Descripción de Zapato de Running',          'S40', 89.00, 20),
                                                                ('Zapatilla Casual',           'Descripción de Zapatilla Casual',           'S41', 90.50, 20),
                                                                ('Zapatilla de Moda',          'Descripción de Zapatilla de Moda',          'S42', 92.00, 20),
                                                                ('Botín Elegante',             'Descripción de Botín Elegante',             'S43', 93.50, 20),
                                                                ('Zapatilla Urbana II',        'Descripción de Zapatilla Urbana II',        'S44', 95.00, 20),
                                                                ('Zapato Clásico',             'Descripción de Zapato Clásico',             'S35', 96.50, 20),
                                                                ('Zapato Contemporáneo',       'Descripción de Zapato Contemporáneo',       'S36', 98.00, 20),
                                                                ('Bota Formal',                'Descripción de Bota Formal',                'S37', 99.50, 20),
                                                                ('Bota de Trabajo',            'Descripción de Bota de Trabajo',            'S38',101.00, 20),
                                                                ('Zapato de Oficina',          'Descripción de Zapato de Oficina',          'S39',102.50, 20),
                                                                ('Zapato Ancho',               'Descripción de Zapato Ancho',               'S40',104.00, 20),
                                                                ('Zapato Moderno',             'Descripción de Zapato Moderno',             'S41',105.50, 20),
                                                                ('Zapatilla de Entrenamiento','Descripción de Zapatilla de Entrenamiento','S42',107.00, 20),
                                                                ('Zapatilla de Running',       'Descripción de Zapatilla de Running',       'S43',108.50, 20),
                                                                ('Zapato de Cuero',            'Descripción de Zapato de Cuero',            'S44',110.00, 20),
                                                                ('Bota Casual',                'Descripción de Bota Casual',                'S35',111.50, 20),
                                                                ('Botín de Cuero',             'Descripción de Botín de Cuero',             'S36',113.00, 20),
                                                                ('Zapatilla de Casa',          'Descripción de Zapatilla de Casa',          'S37',114.50, 20),
                                                                ('Zapato Elegante',            'Descripción de Zapato Elegante',            'S38',116.00, 20),
                                                                ('Zapato Deportivo Formal',    'Descripción de Zapato Deportivo Formal',    'S39',117.50, 20),
                                                                ('Sandalia Casual II',         'Descripción de Sandalia Casual II',         'S40',119.00, 20),
                                                                ('Zapatilla para Correr',      'Descripción de Zapatilla para Correr',      'S41',120.50, 20),
                                                                ('Bota de Invierno',           'Descripción de Bota de Invierno',           'S42',122.00, 20),
                                                                ('Zapatilla de Gym',           'Descripción de Zapatilla de Gym',           'S43',123.50, 20),
                                                                ('Zapato Premium',             'Descripción de Zapato Premium',             'S44',125.00, 20);

-- Procedimiento para generar 100 ventas en 2025
DELIMITER //
CREATE PROCEDURE generate_sales()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE random_days INT;
  DECLARE random_channel VARCHAR(10);
  DECLARE random_total DOUBLE;
  DECLARE random_user INT;
  WHILE i <= 100 DO
    SET random_days    = FLOOR(RAND()*365);
    SET random_channel = IF(RAND()<0.5,'ONLINE','TIENDA');
    SET random_total   = ROUND(50 + RAND()*450,2);
    SET random_user    = FLOOR(2 + RAND()*3);
INSERT INTO sale (user_id, channel, total, sale_date)
VALUES (random_user, random_channel, random_total,
        DATE_ADD('2025-01-01', INTERVAL random_days DAY));
SET i = i + 1;
END WHILE;
END//
DELIMITER ;
CALL generate_sales();

-- Procedimiento para generar detalles de venta
DELIMITER //
CREATE PROCEDURE generate_sale_details()
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE current_sale_id INT;
  DECLARE num_details INT;
  DECLARE j INT;
  DECLARE sale_cursor CURSOR FOR SELECT id FROM sale;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

OPEN sale_cursor;
read_loop: LOOP
    FETCH sale_cursor INTO current_sale_id;
    IF done THEN LEAVE read_loop; END IF;

    SET num_details = FLOOR(1 + RAND()*3);
    SET j = 1;
    WHILE j <= num_details DO
      INSERT INTO sale_details (sale_id, product_id, quantity, subtotal)
        VALUES (
          current_sale_id,
          FLOOR(1 + RAND()*50),
          FLOOR(1 + RAND()*5),
          ROUND(10 + RAND()*90,2)
        );
      SET j = j + 1;
END WHILE;

END LOOP;
CLOSE sale_cursor;
END//
DELIMITER ;
CALL generate_sale_details();


