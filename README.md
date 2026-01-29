# Shoes Store - Sistema de Gestión de Calzado (Versión Profesional)

## 1. Descripción General

**Shoes Store** es una aplicación web completa desarrollada con Spring Boot para la gestión integral de una tienda de calzado. El sistema está diseñado siguiendo las mejores prácticas de la industria, con un enfoque en la robustez, escalabilidad y mantenibilidad.

Este proyecto no solo cubre las funcionalidades CRUD básicas, sino que también implementa características avanzadas como un pipeline de CI/CD, optimizaciones de rendimiento con caché, seguridad granular a nivel de método y procesamiento de tareas en segundo plano, lo que lo convierte en un software de nivel empresarial.

## 2. Funcionalidades Principales

- **Gestión de Autenticación y Usuarios:** Sistema de login seguro con roles (`ADMIN`, `SELLER`, `STOCK_MANAGER`).
- **Dashboard Interactivo:** Visualización en tiempo real de métricas clave como ventas, usuarios activos y pedidos.
- **Módulo de Productos:** Administración completa del inventario de calzado.
- **Módulo de Proveedores:** Gestión de la información de contacto y condiciones de pago.
- **Módulo de Compras:** Creación y seguimiento de órdenes de compra a proveedores.
- **Módulo de Ventas:** Registro de ventas (en tienda y online) con cálculo de descuentos y costos de envío.
- **Módulo de Reclamos:** Gestión del proceso de devoluciones y reclamos asociados a una venta.
- **API REST para Reportes:** Endpoints RESTful para generar reportes dinámicos de ventas, documentados con Swagger.

## 3. Arquitectura y Patrones de Diseño

Esta sección detalla las decisiones de arquitectura y los patrones de software implementados, que son fundamentales para la calidad y profesionalismo del proyecto.

### 3.1. Stack Tecnológico

- **Backend:** Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security.
- **Frontend:** Thymeleaf para renderizado del lado del servidor.
- **Base de Datos:** MySQL en producción (vía Docker) y H2 en memoria para desarrollo.
- **Build Tool:** Apache Maven.
- **Contenerización:** Docker y Docker Compose.

### 3.2. Patrones y Prácticas Implementadas

#### a. Inyección de Dependencias (DI) y Principio de Inversión de Control (IoC)
- **El Patrón:** Es el corazón del framework Spring. En lugar de que los componentes creen sus propias dependencias, el contenedor de Spring se las "inyecta".
- **Implementación en el Proyecto:** Todos los componentes (`@Service`, `@Repository`, `@Controller`) están débilmente acoplados. Por ejemplo, `ProductService` no crea una instancia de `ProductRepository`; la recibe en su constructor. Esto facilita enormemente las pruebas unitarias (permitiendo inyectar *mocks*) y hace que el sistema sea más modular y mantenible.

#### b. Patrón Repositorio y Data Transfer Object (DTO)
- **El Patrón:** El patrón Repositorio abstrae la lógica de acceso a datos, mientras que el patrón DTO desacopla el modelo de dominio interno de la representación externa.
- **Implementación en el Proyecto:** Spring Data JPA implementa el patrón Repositorio (`ProductRepository`, etc.), proporcionando una capa de abstracción sobre Hibernate. Además, se utilizan DTOs (ej. `ProductDto`) para transferir datos entre el frontend y los controladores. Esto evita exponer las entidades JPA directamente, previene problemas de seguridad y permite dar forma a los datos específicamente para la vista, mejorando la eficiencia.

#### c. Patrón Observador (Publisher-Subscriber) para Tareas Asíncronas
- **El Patrón:** Permite que un objeto (el *publisher*) notifique a múltiples objetos (los *subscribers*) sobre un cambio de estado, sin que el publisher necesite conocer a los subscribers.
- **Implementación en el Proyecto:** Para mejorar la capacidad de respuesta, las operaciones lentas como el envío de correos se manejan de forma asíncrona.
  1.  Cuando se crea una venta, `SaleService` publica un `SaleCreatedEvent`.
  2.  Un componente desacoplado, `SaleEventListener`, escucha este evento (`@EventListener`).
  3.  Al recibir el evento, invoca a `NotificationService`, cuyo método `@Async` envía el email en un hilo separado.
  El resultado es que la solicitud del usuario finaliza instantáneamente, y la notificación se procesa en segundo plano.

#### d. Patrón de Caché (Cache-Aside) para Optimización de Rendimiento
- **El Patrón:** Consiste en consultar primero la caché antes de acceder a una fuente de datos más lenta (como una base de datos).
- **Implementación en el Proyecto:** Se utiliza Spring Cache con **Caffeine** (una caché en memoria de alto rendimiento) para acelerar las consultas frecuentes.
  - El método `getAllProductsWithSuppliers()` en `ProductService` está anotado con `@Cacheable("products")`. La primera llamada ejecuta la consulta a la base de datos y almacena el resultado. Las llamadas posteriores devuelven el resultado directamente desde la memoria.
  - Para garantizar la consistencia, los métodos que modifican productos (`save`, `update`, `delete`) están anotados con `@CacheEvict(allEntries = true)`, que limpia la caché de productos, forzando una nueva consulta a la base de datos en la siguiente lectura.

#### e. Seguridad Detallada y Principio de Mínimo Privilegio
- **El Principio:** Un usuario solo debe tener acceso a los recursos estrictamente necesarios para realizar su función.
- **Implementación en el Proyecto:** Además de la seguridad por roles a nivel de URL, se aplica seguridad a nivel de método con `@PreAuthorize`. Por ejemplo, el endpoint para ver los detalles de una venta está protegido con la expresión SpEL `@PreAuthorize("hasRole('ADMIN') or @saleService.isOwner(#saleId, principal.username)")`. Esto asegura que solo un administrador o el usuario que realizó la compra puedan ver los detalles, aplicando una lógica de autorización mucho más fina y segura.

#### f. Práctica DevOps: Integración Continua (CI)
- **La Práctica:** Automatizar la construcción y prueba del código en cada cambio para detectar errores de forma temprana.
- **Implementación en el Proyecto:** Se ha configurado un workflow de **GitHub Actions** (`.github/workflows/ci.yml`) que se dispara en cada `push` y `pull request`. Este pipeline ejecuta `mvn verify`, que compila el código, ejecuta todas las pruebas unitarias y de integración, y empaqueta la aplicación. Esto garantiza un alto nivel de calidad y fiabilidad en la base de código.

## 4. Guía de Instalación y Ejecución

### Entorno de Desarrollo (Recomendado para pruebas rápidas)
1.  **Clonar el repositorio:** `git clone <URL_DEL_REPOSITORIO>`
2.  **Ejecutar con Maven:** `mvn spring-boot:run`
3.  **Acceder:**
    - **Aplicación:** `http://localhost:8080`
    - **Consola H2:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Pass: `1234`)

### Entorno de Producción (Simulado con Docker)
1.  **Construir el JAR:** `mvn clean package`
2.  **Levantar los servicios:** `docker-compose up --build`
3.  **Acceder:**
    - **Aplicación:** `http://localhost:8080`
    - **Documentación API (Swagger):** `http://localhost:8080/swagger-ui.html`
```