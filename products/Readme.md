## Descripción de dependencias utilizadas

Actuator
Validation
Web
Config
Eureka Client
Open Feign
DevTools
Lombok
SpringDoc OpenApi

1. Actuator

We gonna use Actuar for monitoring application health state and for performance monitoring.
But mainly health state, due to the scope of this scanner.

Vamos a utilizar Actuator para monitorear el estado de la aplicación principalmente.
También para monitorear el rendimiento, pero principalmente nos enfocamos en su estado de salud
ya que la aplicación tiene un uso comercial que no requiere un uso intensivo al menos en el estado actual
De los procesos de negocio.

2. Validation

We gonna use this for validating input data, Dtos and other kind of entities.
It offers automatic validation before processing data in controllers and services.

Vamos a utilizar Validation para validar datos de entrada, Dtos y otro tipo de entidades.
Ofrece Validación automática antes de procesar datos en controladores o servicios.

3. Web

We gonna use this for exposing RestControllers that will communicate with client application and Monolith.

Lo vamos a utilizar para exponer RestControllers que van a permitir comunicarnos con la aplicacion Cliente 
y el monolito actual que ofrece las operaciones de ventas y control de stock.


4. Config

Managing centralized configuracion in Spring.
Externalizing properties in application.yml
Allowing us to use different profiles (dev, prod) and dynamic configuration.

Gestión de configuración centralizada en Spring.
Externalizar propiedades en application.yml.
Soporta perfiles (dev, prod) y configuración dinámica.

5. Eureka Client

Microservices and service discovery.
Microservicios y descubrimiento de servicios.

6. OpenFeign

Used for calling other microservices using Rest.
Utilizado para llamar otros microservicios usando Rest.

7. DevTools

To Make Easy Local Development.
Facilitar el desarrollo local.

8. Lombok

We gonna use Lombok to Reduce boilerplate in Java.
Vamos a usar Lombok para Reducir boilerplate en Java.

9. SpringDoc OpenAPI

We gonna use this for documenting REST APIs automatically using Swagger.
Documentar APIs REST de forma automática, generando documentación Swagger de los endpoints.


## ETAPA DE PRUEBAS

### Capa de Modelo / Entidades

Qué es: Clases Product, Category, ScannerResult, etc.

Qué testear:
    * Métodos de utilidad que tengas (si hay lógica en getters/setters rara vez se testea).
    * equals(), hashCode(), toString() si son importantes para la lógica.

Tipo de test: Unit test.

### Capa de Servicio / Lógica de Negocio

Qué es: Clases con @Service que implementan la lógica de tu scanner, validaciones, reglas de negocio, etc.

Qué testear:
    * Todo cálculo, transformación o filtrado que haga tu servicio. 
    * Manejo de errores, excepciones o validaciones.

Tipo de test: Unit test puro con JUnit 5 + Mockito para mockear repositorios u otros servicios.

### Capa de Controlador / API

Qué es: Clases @RestController que exponen endpoints.

Qué testear:
    * Que los endpoints devuelvan los códigos HTTP correctos (200, 404, 400). 
    * Que se llamen los servicios correctos.

Tipo de test:
    * Unit test con @WebMvcTest para testear solo controladores. 
    * Integration test usando @SpringBootTest para levantar todo el contexto.

### Capa de Integración / Microservicios externos

Qué es: Llamadas a otros servicios (Eureka, OpenFeign, APIs externas).

Qué testear:
    * Que se hagan correctamente las llamadas. 
    * Manejo de errores si el servicio externo falla.

Tipo de test: 
    * Unit test con Mockito para simular la respuesta de Feign. 
    * Integration test con WireMock o MockWebServer si querés test realista.

Todos los tests los realizaremos siguiendo la metodología de TDD.

## Objetivos testing estructural

El objetivo de testing estructural posterior al TDD es obtener un 100% de cobertura de sentencias.
