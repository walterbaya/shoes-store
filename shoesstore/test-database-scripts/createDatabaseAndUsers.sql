-- Eliminar base de datos existente, crearla y seleccionarla
DROP DATABASE IF EXISTS shoes_store;
CREATE DATABASE shoes_store;
USE shoes_store;

-- Inserción de usuarios (bcrypt sin tocar)
INSERT INTO users (username, password, full_name, role) VALUES
                                                            ('admin',  '$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Administrador Principal', 'ROLE_ADMIN'),
                                                            ('seller1','$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Vendedor Uno',         'ROLE_SELLER'),
                                                            ('seller2','$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Vendedor Dos',         'ROLE_SELLER'),
                                                            ('seller3','$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Vendedor Tres',        'ROLE_SELLER'),
                                                            ('stockmanager','$2a$12$A95WpI9tt5Zy4Y4Ih.IOLe1a1QJyxalyrrMsoYj8bP2eg7jBCULgy', 'Gestors de Stock',        'ROLE_SELLER');

