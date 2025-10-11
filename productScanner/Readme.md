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


