-- Eliminar BD existente
DROP DATABASE IF EXISTS shoes_store;
CREATE DATABASE shoes_store;
USE shoes_store;

-- Tabla Users (SIN CAMBIOS en los roles)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    role ENUM('ADMIN', 'SELLER') NOT NULL,  -- Se mantiene igual
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Tabla Products (ÚNICO CAMBIO: ENUM de tallas con 'S')
CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    size ENUM('S35','S36','S37','S38','S39','S40','S41','S42','S43','S44') NOT NULL,  -- Valores ajustados
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Tabla Sales (MODIFICADO: nuevos valores para el canal de venta)
CREATE TABLE sale (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    channel ENUM('ONLINE', 'TIENDA') NOT NULL,  -- Valores que coinciden con el enum en Java
    total DECIMAL(10,2) NOT NULL,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- Tabla Sale_Details (MODIFICADO: se agrega columna id auto-incrementable)
CREATE TABLE sale_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Usuario administrador (Se mantiene igual; revisa que el rol concuerde con lo que espera Spring Security)
INSERT INTO users (username, password, full_name, role)
VALUES (
    'admin',
    '$2a$04$f6MJKU3x50kcIx5kTQdOf.VYwWUZQgSR4xld82QbDRzROAe5v.Uoe',
    'Administrador Principal',
    'ADMIN'
);

-- Insertar algunos productos de ejemplo
INSERT INTO product (name, description, size, price, stock)
VALUES
('Zapatilla Deportiva', 'Zapatilla para correr', 'S42', 59.99, 20),
('Zapato Formal', 'Zapato para ocasiones formales', 'S41', 89.99, 15),
('Bota de Montaña', 'Bota resistente para senderismo', 'S43', 120.50, 10);

