# Shoe Store Backend



Backend REST API for managing a shoe store, including product catalog, stock control, purchase orders, supplier management, claim management, and user roles.

## 

## Problem Statement



Small and medium shoe stores often struggle with stock inconsistencies sometimes because of claims, manual order tracking, and lack of centralized control over products, variants, and sales. Also they used to buy their products from different suppliers and is a necesity to keep all the records related with them in a organized way, the system provides user roles management and the main objetive is that users with certain roles can only make use of their belonging information and not more.



This project provides a backend system that:

* Manages shoe products with variants
* Controls stock safely under concurrent purchases (future)
* Handles orders and basic payment flow
* Separates customer and admin operations





## Features



* Product management with size and color variants
* Stock control with concurrency handling
* Order creation and lifecycle management
* User authentication and role-based authorization
* Basic discount and promotion support
* Global error handling
* RESTful API design

## 

## Architecture



The application follows a layered architecture:

* Controller layer: HTTP request handling
* Service layer: business logic and domain rules
* Repository layer: persistence and database access
* DTO layer: request/response models

Key design decisions:

* Monolithic Spring Boot application for simplicity and maintainability debido principalmente

a que se la solución está orientada a un problema de pequeña escala.

* Domain-oriented package structure
* Al haber trabajado en este diseño un pequeño número de personas en el equipo este enfoque 

permitió enfocarnos principalmente en la lógica del negocio del problema.

* Transactional boundaries defined at service level
* Esperamos que el numero de request por segundo no sea muy grande.
* El problema tiene una funcionalidad limitada y no se espera que crezca enormemente.
* El desarrollo y la recopilación de datos inicialmente surgió como un trabajo práctico universitario

por esta razón tuvimos 4 meses para encarar este problema y al necesitarse un desarrollo veloz y

un lanzamiento rápido de la funcionalidad pareció mucho más adecuado utilizar esta arquitectura

monolítica.

* Validation split between DTOs and domain logic
* La aplicación va a correr en un único servidor local.
* Domain-driven design (light)
* Factory Method for creation of valid instances only
* DTOs for I/O operations

## 

## Tech Stack



* Java 17
* Spring Boot
* Spring Data JPA
* Spring Security (JWT)
* PostgreSQL
* Docker \& Docker Compose
* JUnit 5 / Mockito

## 

## Domain Model



Main entities:

* Product
* ProductVariant (size, color)
* Stock
* Order
* OrderItem
* User
* Role



## Running the Project

### Using Docker



1. Clone the repository
2. Run:
   docker-compose up --build
3. API will be available at:
   http://localhost:8080



## Security



The API uses JWT-based authentication.

Roles:

* ADMIN: manage products, stock, and orders
* CUSTOMER: browse catalog and create orders

## 

## API Documentation



Swagger UI available at:
http://localhost:8080/swagger-ui.html



## Testing



The project includes:

* Unit tests for core business logic
* Integration tests for REST endpoints

Run tests with:
mvn test



## Trade-offs and Decisions



* A monolithic architecture was chosen over microservices to reduce complexity.
* JPA was used for rapid development and maintainability.
* Stock concurrency is handled at the database transaction level to prevent overselling.

## 

## Future Improvements



* Advanced promotion rules
* External payment gateway integration
* Caching for catalog endpoints
* Metrics and observability



## Author



Walter Ariel Baya  
Java Backend Developer

