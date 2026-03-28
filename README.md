# DOSW-Library

API REST de biblioteca desarrollada con Spring Boot para gestionar libros, usuarios y prestamos.

## Requisitos

- Java 21
- Maven Wrapper (incluido en el proyecto)

## Ejecucion del proyecto

En Windows:

```bash
./mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

La aplicacion levanta por defecto en:

- `http://localhost:8080`

## Funcionalidades implementadas

### 1. Uso de Lombok

Se incorporo Lombok para reducir boilerplate en modelos y DTOs:

- Modelos:
	- `Book`: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@ToString`, `@EqualsAndHashCode(of = "id")`
	- `User`: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@ToString`, `@EqualsAndHashCode(of = "id")`
	- `Loan`: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@ToString`
- DTOs:
	- `BookDTO`, `UserDTO`, `LoanDTO` con `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

Configuracion Maven:

- Dependencia `org.projectlombok:lombok`
- `maven-compiler-plugin` con `annotationProcessorPaths` para Lombok

### 2. Documentacion con Swagger / OpenAPI

Se agrego documentacion OpenAPI para las APIs:

- Dependencia:
	- `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6`
- Configuracion general en `OpenApiConfig`:
	- Titulo: `DOSW Library API`
	- Version: `v1`
	- Descripcion y metadatos
- Configuracion de rutas en `application.properties`:
	- `springdoc.api-docs.path=/api-docs`
	- `springdoc.swagger-ui.path=/swagger-ui.html`
- Documentacion por controlador:
	- `@Tag`
	- `@Operation`
	- `@ApiResponse` y `@ApiResponses`

### 3. Manejo de errores

Se implemento manejo global de excepciones con respuestas estandar:

- `GlobalExceptionHandler` con `@RestControllerAdvice`
- `ErrorResponse` como contrato uniforme de error con:
	- `timestamp`
	- `status`
	- `error`
	- `message`
	- `path`
- Mapeo de excepciones frecuentes a codigos HTTP:
	- `BookNotAvailableException` -> `409 CONFLICT`
	- `UserNotFoundException` -> `404 NOT_FOUND`
	- `LoanLimitExceededException` -> `403 FORBIDDEN`
	- `IllegalArgumentException` -> `400 BAD_REQUEST` (o `404` cuando el mensaje contiene `not found`)
	- `IllegalStateException` -> `409 CONFLICT`
	- `Exception` -> `500 INTERNAL_SERVER_ERROR`

### 4. Validadores y utilidades

Se mantienen y usan validadores/utilidades transversales para asegurar reglas de entrada:

- Validadores por caso de uso:
	- `BookValidator`
	- `UserValidator`
	- `LoanValidator`
- Utilidades:
	- `ValidationUtil`: validaciones comunes (`not null`, `not empty`, rangos, positivos)
	- `DateUtil`: conversiones/validaciones de fechas
	- `IdGeneratorUtil`: apoyo para generacion de IDs

Estos componentes se usan desde controladores, mappers y servicios para mantener consistencia en las reglas de negocio.

### 5. Persistencia con JPA

Se agrego persistencia para almacenar la informacion de libros, usuarios y prestamos usando Spring Data JPA con H2.

- Dependencias agregadas:
	- `spring-boot-starter-data-jpa`
	- `com.h2database:h2`
- Entidades persistentes:
	- `Book`
	- `User`
	- `Loan` (con relaciones `@ManyToOne` hacia `Book` y `User`)
- Repositorios Spring Data:
	- `BookRepository`
	- `UserRepository`
	- `LoanRepository`
- Configuracion en `application.properties`:
	- Base H2 en memoria
	- `spring.jpa.hibernate.ddl-auto=update`
	- Consola H2 habilitada en `/h2-console`

Nota: Los servicios conservan compatibilidad con pruebas unitarias existentes en memoria, y en ejecucion Spring usan repositorios JPA automaticamente.

### 6. Seguridad con JWT

Se implemento autenticacion stateless con Spring Security y JWT.

- Dependencias agregadas:
	- `spring-boot-starter-security`
	- `io.jsonwebtoken` (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- Endpoint de autenticacion:
	- `POST /auth/login`
- Filtro Bearer JWT:
	- Se valida el header `Authorization: Bearer <token>` en cada request protegida.
- Rutas publicas:
	- `/auth/**`
	- `/swagger-ui.html`, `/swagger-ui.htm`, `/swagger-ui/**`
	- `/api-docs/**`
	- `/h2-console/**`
- Rutas protegidas:
	- Todas las demas (`/api/**` incluidas) requieren JWT.

Credenciales por defecto para login (configurables en `application.properties`):

- Usuario: `admin`
- Clave: `admin1234`

## Endpoints principales

- Libros:
	- `POST /api/books`
	- `GET /api/books`
	- `GET /api/books/{id}`
	- `PUT /api/books/{id}/availability?available=true|false`
- Usuarios:
	- `POST /api/users`
	- `GET /api/users`
	- `GET /api/users/{id}`
- Prestamos:
	- `POST /api/loans`
	- `PUT /api/loans/{loanId}/return`
	- `GET /api/loans`
	- `GET /api/loans/{loanId}`
	- `GET /api/loans/user/{userId}/active`

## Como probar Swagger

1. Levanta la aplicacion:

```bash
./mvnw.cmd spring-boot:run
```

2. Abre la interfaz Swagger UI en el navegador:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/swagger-ui.htm` (redirecciona automaticamente)

3. Verifica el JSON OpenAPI:

- `http://localhost:8080/api-docs`

4. Prueba endpoints desde Swagger UI:

- Primero genera token en `POST /auth/login` con:

```json
{
	"username": "admin",
	"password": "admin1234"
}
```

- Copia el `token` de la respuesta.
- Click en el boton `Authorize` (arriba en Swagger UI).
- Pega: `Bearer <tu_token>`
- Luego prueba endpoints protegidos (por ejemplo `POST /api/books`).

Ejemplo de body para registrar libro:

```json
{
	"id": "B-101",
	"title": "Clean Code",
	"author": "Robert C. Martin",
	"copies": 3
}
```

## Ejecutar pruebas

```bash
./mvnw.cmd test
```

## Consola H2

Con la aplicacion encendida, puedes abrir:

- `http://localhost:8080/h2-console`

Parametros sugeridos:

- JDBC URL: `jdbc:h2:mem:librarydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- User: `sa`
- Password: (vacio)