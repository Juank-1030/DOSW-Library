# DOSW-Library

Informe de avance tecnico del proyecto de biblioteca desarrollado con Spring Boot.

Este documento esta pensado como referencia de arquitectura, guia de implementacion y manual funcional del proyecto.

## Indice

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Objetivo y Alcance](#objetivo-y-alcance)
3. [Stack Tecnologico](#stack-tecnologico)
4. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
5. [Anotaciones y Por Que Se Usan](#anotaciones-y-por-que-se-usan)
6. [Estructura de Paquetes](#estructura-de-paquetes)
7. [Paquetes y Clases: Responsabilidad Detallada](#paquetes-y-clases-responsabilidad-detallada)
8. [Configuracion y Ejecucion](#configuracion-y-ejecucion)
9. [Endpoints Implementados](#endpoints-implementados)
10. [Flujos Funcionales Clave](#flujos-funcionales-clave)
11. [Flujo Entre Paquetes y Clases](#flujo-entre-paquetes-y-clases)
12. [Explicacion Paso a Paso: Flujos con Codigo Real](#explicacion-paso-a-paso-flujos-con-codigo-real)
13. [Explicacion de Todas las Clases](#explicacion-de-todas-las-clases)
14. [Como Implementar y Extender el Proyecto](#como-implementar-y-extender-el-proyecto)
15. [Pruebas y Cobertura Actual](#pruebas-y-cobertura-actual)
16. [Riesgos Tecnicos y Mejoras Recomendadas](#riesgos-tecnicos-y-mejoras-recomendadas)
17. [Glosario](#glosario)

## Resumen Ejecutivo

DOSW-Library implementa una API REST para administrar:

- Libros
- Usuarios
- Prestamos y devoluciones
- Seguridad basada en JWT
- Documentacion OpenAPI/Swagger

El proyecto tiene una arquitectura por capas clara (controller -> service -> model/repository), manejo centralizado de errores y pruebas unitarias sobre la logica de negocio principal.

## Objetivo y Alcance

### Objetivo

Construir una API de biblioteca robusta para practicar principios de arquitectura limpia en Spring:

- Separacion de responsabilidades
- Validaciones de entrada y de negocio
- Seguridad stateless
- Documentacion profesional de API

### Alcance funcional actual

- CRUD parcial de libros y usuarios
- Gestion completa del ciclo de vida del prestamo
- Reglas de negocio (limite de prestamos, disponibilidad, no duplicados)
- Seguridad JWT para rutas protegidas
- Soporte de H2 en memoria

## Stack Tecnologico

- Java 21
- Spring Boot 4.0.3
- Spring Web
- Spring Validation (Jakarta Validation)
- Spring Security
- JJWT 0.12.6
- Spring Data JPA
- H2 Database
- Lombok
- Springdoc OpenAPI
- JUnit 5

## Arquitectura del Proyecto

### Vista de capas

- Capa de presentacion: controladores REST, DTOs y mappers
- Capa de negocio: servicios, validadores y excepciones
- Capa de persistencia/modelo: entidades y repositorios JPA
- Capa transversal: seguridad JWT, configuracion OpenAPI, utilidades

### Principio de diseno aplicado

Cada clase existe para resolver una responsabilidad concreta:

- Controllers: exponen HTTP y delegan
- Services: aplican reglas de negocio
- Mappers: convierten Entity <-> DTO
- Validators: concentran validacion semantica
- Exception Handler: estandariza errores
- Security: autentica y autoriza

## Anotaciones y Por Que Se Usan

Esta seccion responde al "por que" de las anotaciones mas importantes del proyecto.

### Anotaciones de Spring Boot y configuracion

- @SpringBootApplication
	- Donde: clase principal.
	- Para que sirve: habilita autoconfiguracion, escaneo de componentes y bootstrap de la app.
	- Por que se usa: evita configurar manualmente docenas de beans base.

- @Configuration
	- Donde: clases de configuracion como OpenApiConfig y SecurityConfig.
	- Para que sirve: declara configuracion Java basada en beans.
	- Por que se usa: concentra decisiones tecnicas en un punto mantenible.

- @Bean
	- Donde: metodos dentro de clases @Configuration.
	- Para que sirve: registra objetos en el contenedor de Spring.
	- Por que se usa: inyeccion de dependencias desacoplada.

- @EnableMethodSecurity
	- Donde: SecurityConfig.
	- Para que sirve: permite seguridad a nivel de metodo (por ejemplo @PreAuthorize).
	- Por que se usa: prepara la app para autorizacion mas fina.

### Anotaciones web y API

- @RestController
	- Donde: BookController, UserController, LoanController.
	- Para que sirve: convierte respuestas a JSON automaticamente.
	- Por que se usa: construir API REST sin boilerplate de serializacion.

- @RequestMapping, @GetMapping, @PostMapping, @PatchMapping, @PutMapping, @DeleteMapping
	- Donde: controladores.
	- Para que sirven: enrutan metodos Java a endpoints HTTP.
	- Por que se usan: separar claramente verbo HTTP y caso de uso.

- @RequestBody, @PathVariable, @RequestParam
	- Para que sirven: enlazan body, variables de ruta y query params al metodo.
	- Por que se usan: tipado fuerte y legibilidad del contrato HTTP.

### Anotaciones de validacion

- @Valid
	- Donde: parametros de entrada en controladores.
	- Para que sirve: dispara validaciones Jakarta en DTOs.
	- Por que se usa: rechazar payload invalido temprano (antes de negocio).

- @NotBlank, @NotNull, @Min, @Size, @Email
	- Donde: DTOs.
	- Para que sirven: reglas declarativas de formato y obligatoriedad.
	- Por que se usan: minimizar validacion manual repetitiva.

### Anotaciones de dominio y persistencia

- @Entity y @Table
	- Donde: Book, User, Loan.
	- Para que sirven: mapear clases a tablas.
	- Por que se usan: habilitar persistencia ORM con JPA.

- @Id
	- Para que sirve: marca clave primaria.
	- Por que se usa: identidad unica por entidad.

- @Column
	- Para que sirve: restricciones de columna (nullable, unique).
	- Por que se usa: expresar reglas de datos a nivel de esquema.

- @ManyToOne y @JoinColumn
	- Donde: Loan hacia Book y User.
	- Para que sirven: modelar relacion N:1.
	- Por que se usan: representar que muchos prestamos pertenecen a un libro/usuario.

- @Enumerated(EnumType.STRING)
	- Donde: Loan.status.
	- Para que sirve: persistir enums como texto.
	- Por que se usa: mayor claridad y menor fragilidad que ordinales numericos.

### Anotaciones de componentes y capas

- @Service
	- Donde: servicios.
	- Para que sirve: marca capa de negocio.
	- Por que se usa: separar reglas del transporte HTTP.

- @Component
	- Donde: mappers, utilidades, validadores, filtros.
	- Para que sirve: registrar componentes reutilizables.
	- Por que se usa: composicion modular e inyeccion limpia.

- @Repository
	- Donde: interfaces JPA.
	- Para que sirve: capa de acceso a datos.
	- Por que se usa: abstraer persistencia y consultas.

### Anotaciones de manejo de errores

- @RestControllerAdvice
	- Donde: GlobalExceptionHandler.
	- Para que sirve: interceptor global de excepciones.
	- Por que se usa: respuestas de error homogéneas para toda la API.

- @ExceptionHandler
	- Para que sirve: mapear tipo de excepcion a codigo y payload HTTP.
	- Por que se usa: eliminar try/catch repetitivo en controladores.

### Anotaciones de documentacion

- @Tag, @Operation, @ApiResponse, @ApiResponses, @Schema
	- Donde: controladores y DTOs.
	- Para que sirven: enriquecer contrato OpenAPI.
	- Por que se usan: API autoexplicable y facil de consumir.

### Anotaciones de Lombok

- @Data, @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor, @EqualsAndHashCode, @ToString
	- Para que sirven: generar codigo repetitivo automaticamente.
	- Por que se usan: reducir ruido y enfocarse en logica de negocio.

## Estructura de Paquetes

```text
edu.eci.dosw.DOSW_Library
|-- DoswLibraryApplication.java
|-- config
|   |-- OpenApiConfig.java
|-- controller
|   |-- BookController.java
|   |-- UserController.java
|   |-- LoanController.java
|   |-- SwaggerRedirectController.java
|   |-- dto
|   |   |-- BookDTO.java
|   |   |-- CreateBookDTO.java
|   |   |-- UpdateBookInventoryDTO.java
|   |   |-- UserDTO.java
|   |   |-- CreateUserDTO.java
|   |   |-- UpdateUserDTO.java
|   |   |-- LoginRequest.java
|   |   |-- CreateLoanDTO.java
|   |   |-- LoanDTO.java
|   |   |-- LoanSummaryDTO.java
|   |-- mapper
|       |-- BookMapper.java
|       |-- UserMapper.java
|       |-- LoanMapper.java
|-- core
|   |-- model
|   |   |-- Book.java
|   |   |-- User.java
|   |   |-- Loan.java
|   |   |-- LoanStatus.java
|   |-- service
|   |   |-- BookService.java
|   |   |-- UserService.java
|   |   |-- LoanService.java
|   |-- repository
|   |   |-- BookRepository.java
|   |   |-- UserRepository.java
|   |   |-- LoanRepository.java
|   |-- validator
|   |   |-- ValidationUtil.java
|   |   |-- BookValidator.java
|   |   |-- UserValidator.java
|   |   |-- LoanValidator.java
|   |-- exception
|   |   |-- ErrorResponse.java
|   |   |-- GlobalExceptionHandler.java
|   |   |-- ResourceNotFoundException.java
|   |   |-- UserNotFoundException.java
|   |   |-- BookNotAvailableException.java
|   |   |-- LoanLimitExceededException.java
|   |-- util
|       |-- Constants.java
|       |-- DateUtil.java
|       |-- IdGeneratorUtil.java
|-- security
		|-- SecurityConfig.java
		|-- JwtService.java
		|-- JwtAuthenticationFilter.java
```

## Paquetes y Clases: Responsabilidad Detallada

### Paquete principal

- Rol: punto de arranque y frontera del escaneo de Spring.
- Clase:
	- DoswLibraryApplication: inicia toda la aplicacion.

### config

- Rol: decisiones de configuracion transversal.
- Clases:
	- OpenApiConfig: define metadata de API, esquema bearer JWT y seguridad global de docs.

### controller

- Rol: capa de entrada HTTP.
- Clases:
	- BookController: publica operaciones de libros e inventario.
	- UserController: publica operaciones de usuarios.
	- LoanController: publica operaciones de prestamos/devoluciones/listados.
	- SwaggerRedirectController: compatibilidad de ruta swagger-ui.htm.

### controller.dto

- Rol: contratos de entrada y salida HTTP.
- Clases:
	- CreateBookDTO, UpdateBookInventoryDTO, CreateUserDTO, UpdateUserDTO, CreateLoanDTO, LoginRequest: entrada.
	- BookDTO, UserDTO, LoanDTO, LoanSummaryDTO: salida.

### controller.mapper

- Rol: traduccion entre DTO y entidades.
- Clases:
	- BookMapper: conversiones de libro + logica de operacion de inventario en memoria.
	- UserMapper: conversiones de usuario + merge de update parcial.
	- LoanMapper: conversiones de prestamo full y summary.

### core.model

- Rol: modelo de dominio.
- Clases:
	- Book: catalogo e inventario.
	- User: persona registrada.
	- Loan: prestamo de libro por usuario.
	- LoanStatus: estado del prestamo.

### core.service

- Rol: reglas de negocio del sistema.
- Clases:
	- BookService: reglas de stock y disponibilidad.
	- UserService: registro, unicidad y actualizacion de usuario.
	- LoanService: orquestacion de validaciones y ciclo del prestamo.

### core.repository

- Rol: consultas y persistencia JPA definidas por interfaz.
- Clases:
	- BookRepository, UserRepository, LoanRepository.
- Estado actual de avance:
	- Definidos para persistencia relacional, pero los servicios usan almacenamiento en memoria.

### core.validator

- Rol: reglas semanticas reutilizables por dominio.
- Clases:
	- ValidationUtil: helpers genericos de validacion.
	- BookValidator: validacion estructural y consistencia de libros.
	- UserValidator: formato de usuario y correo.
	- LoanValidator: consistencia de fechas, estados y relaciones.

### core.exception

- Rol: estandarizar errores de negocio y su traduccion HTTP.
- Clases:
	- ErrorResponse: contrato JSON de error.
	- GlobalExceptionHandler: traduccion central de excepciones.
	- BookNotAvailableException, LoanLimitExceededException, UserNotFoundException, ResourceNotFoundException.

### core.util

- Rol: utilidades transversales y constantes.
- Clases:
	- Constants: limites y formatos.
	- DateUtil: calculos de vencimiento/atraso.
	- IdGeneratorUtil: IDs con prefijos uniformes.

### security

- Rol: autenticacion y autorizacion.
- Clases:
	- SecurityConfig: reglas de acceso y filtro JWT.
	- JwtService: generar/validar tokens.
	- JwtAuthenticationFilter: autenticar request por header Authorization.

## Configuracion y Ejecucion

### Requisitos

- Java 21
- Maven Wrapper (incluido)

### Ejecutar en Windows

```bash
./mvnw.cmd spring-boot:run
```

### Ejecutar en Linux/macOS

```bash
./mvnw spring-boot:run
```

### Ejecutar pruebas

```bash
./mvnw.cmd test
```

### URLs de acceso

- API base: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Swagger legacy redirect: http://localhost:8080/swagger-ui.htm
- OpenAPI JSON: http://localhost:8080/api-docs
- Consola H2: http://localhost:8080/h2-console

### application.properties relevante

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

spring.datasource.url=jdbc:h2:mem:librarydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

security.jwt.secret=<base64-secret>
security.jwt.expiration-ms=3600000
security.auth.username=admin
security.auth.password=admin1234
```

## Endpoints Implementados

### Libros

- POST /api/books
- GET /api/books
- GET /api/books/{id}
- PATCH /api/books/{id}/inventory
- DELETE /api/books/{id}
- GET /api/books/{id}/available

### Usuarios

- POST /api/users
- GET /api/users
- GET /api/users/{id}
- PATCH /api/users/{id}
- DELETE /api/users/{id}

### Prestamos

- POST /api/loans
- GET /api/loans
- GET /api/loans/{id}
- PUT /api/loans/{id}/return
- GET /api/loans/user/{userId}
- GET /api/loans/user/{userId}/active

### Seguridad

- Rutas publicas: /auth/**, /swagger-ui**, /api-docs/**, /h2-console/**
- Rutas protegidas: cualquier otra

Nota importante: existe DTO de login (LoginRequest) y configuracion de seguridad para /auth/**, pero no hay controlador explicito de login en el codigo fuente actual.

## Flujos Funcionales Clave

### Flujo 1: registrar usuario

1. Cliente envia POST /api/users con CreateUserDTO.
2. UserController valida con @Valid.
3. UserMapper convierte DTO -> User.
4. UserService.registerUser valida duplicados de ID y email.
5. UserMapper convierte User -> UserDTO.
6. Respuesta 201 CREATED.

### Flujo 2: crear libro

1. Cliente envia POST /api/books con CreateBookDTO.
2. BookMapper crea entidad Book.
3. BookService.addBook valida duplicados y setea copias/disponibilidad.
4. Se retorna BookDTO.

### Flujo 3: crear prestamo

1. LoanController recibe CreateLoanDTO.
2. LoanService.createLoan ejecuta validaciones secuenciales:
	 - usuario existe
	 - libro existe
	 - limite de prestamos activos
	 - no prestamo duplicado activo (mismo usuario y libro)
	 - disponibilidad del libro
3. Se crea Loan con estado ACTIVE.
4. BookService.updateAvailability descuenta 1 copia.
5. Se retorna LoanDTO.

### Flujo 4: devolver libro

1. Cliente invoca PUT /api/loans/{id}/return.
2. LoanService.returnLoan valida existencia y estado ACTIVE.
3. Cambia a RETURNED y setea returnDate.
4. BookService.updateAvailability suma 1 copia.
5. Respuesta 200 OK.

### Flujo 5: seguridad JWT en cada request

1. JwtAuthenticationFilter lee Authorization: Bearer <token>.
2. JwtService extrae username y valida firma/expiracion.
3. Si es valido, se setea SecurityContext.
4. SecurityFilterChain permite o bloquea acceso.

## Flujo Entre Paquetes y Clases

Este bloque explica como se conectan los paquetes internamente en tiempo de ejecucion.

### Flujo tecnico general (request tipico)

1. El request entra por controller.
2. controller valida DTO con anotaciones Jakarta.
3. mapper transforma DTO a modelo de dominio.
4. service ejecuta reglas de negocio y coordina dependencias.
5. si hay error, se lanza excepcion de negocio.
6. GlobalExceptionHandler transforma la excepcion a ErrorResponse.
7. mapper transforma entidad a DTO de salida.
8. controller retorna ResponseEntity.

### Flujo de prestamo con interaccion de paquetes

1. controller.LoanController recibe CreateLoanDTO.
2. core.service.LoanService valida usuario con UserService.
3. core.service.LoanService valida libro con BookService.
4. core.service.LoanService aplica reglas:
	- limite maximo de prestamos activos
	- no duplicar prestamo activo mismo libro/usuario
	- disponibilidad de copias
5. core.model.Loan se crea con estado ACTIVE.
6. core.service.BookService actualiza inventario (change = -1).
7. controller.mapper.LoanMapper construye LoanDTO.
8. controller.LoanController responde HTTP 201.

### Flujo de error de validacion

1. DTO invalido llega al controller.
2. @Valid dispara MethodArgumentNotValidException.
3. core.exception.GlobalExceptionHandler captura la excepcion.
4. construye ErrorResponse.validationError(...).
5. responde HTTP 400 con estructura uniforme.

### Flujo de seguridad con JWT

1. security.JwtAuthenticationFilter intercepta request.
2. extrae Bearer token de Authorization.
3. security.JwtService valida firma y expiracion.
4. security.SecurityConfig define si la ruta es publica o protegida.
5. si autentica, request continua hacia controller.

## Explicacion Paso a Paso: Flujos con Codigo Real

En esta seccion se muestra el codigo real del proyecto con explicacion linea por linea del proposito y flujo. Se cubre los 3 flujos mas importantes: creacion de prestamo, autenticacion JWT y manejo de errores.

### Flujo 1: Crear un Prestamo (Flujo Complejo)

Este es el flujo mas importante pues integra validaciones, servicios multiples y cambios de estado.

#### Paso 1: Cliente envia POST request al controlador

El cliente hace:
```bash
POST /api/loans
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "userId": "USR-001",
  "bookId": "BK-001"
}
```

#### Paso 2: LoanController recibe y valida el DTO

Archivo: `controller/LoanController.java`

```java
@PostMapping
@Tag(name = "Loans", description = "Operaciones de prestamos")
@Operation(summary = "Crear un nuevo prestamo", description = "Crea un prestamo de un libro para un usuario")
@ApiResponse(responseCode = "201", description = "Prestamo creado exitosamente")
@ApiResponse(responseCode = "400", description = "DTO invalido")
@ApiResponse(responseCode = "409", description = "Conflicto: libro no disponible, limite alcanzado, etc")
public ResponseEntity<?> createLoan(@Valid @RequestBody CreateLoanDTO dto) {
    // PASO 2: @Valid dispara validacion de restricciones Jakarta en el DTO
    // CreateLoanDTO tiene:
    //   - userId: @NotBlank (no puede ser vacio)
    //   - bookId: @NotBlank (no puede ser vacio)
    //
    // Si el DTO es invalido, @Valid lanza MethodArgumentNotValidException
    // La excepcion es capturada por GlobalExceptionHandler (ver Paso 3 de errores)
    
    Loan loan = loanService.createLoan(dto.getUserId(), dto.getBookId());
    // PASO 2b: llamar al servicio para ejecutar logica de negocio
    
    LoanDTO response = loanMapper.toLoanDTO(loan);
    // PASO 2c: convertir entidad a DTO de salida (mapper)
    
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    // PASO 2d: retornar HTTP 201 con el prestamo creado
}
```

**Que sucede:**
- `@Valid` activa las validaciones de anotaciones Jakarta en CreateLoanDTO
- Si hay error, NO continua y va directamente a GlobalExceptionHandler
- Si todo es valido, se delega al servicio

#### Paso 3: LoanService ejecuta la logica de negocio principal

Archivo: `core/service/LoanService.java`

```java
@Service
public class LoanService {
    
    private final UserService userService;      // inyectado para validar usuario
    private final BookService bookService;      // inyectado para validar libro
    private final Map<String, Loan> loansMap;   // almacenamiento en memoria
    
    public Loan createLoan(String userId, String bookId) {
        // PASO 3.1: Validar que usuario existe
        User user = userService.getUserById(userId);
        // Si no existe, UserService lanza UserNotFoundException
        // -> GlobalExceptionHandler las maneja
        
        // PASO 3.2: Validar que libro existe
        Book book = bookService.getBookById(bookId);
        // Si no existe, BookService lanza ResourceNotFoundException
        
        // PASO 3.3: Validar limite maximo de prestamos activos
        long activeLoans = loansMap.values().stream()
            .filter(l -> l.getUser().getId().equals(userId))   // filtrar por usuario
            .filter(l -> l.getStatus() == LoanStatus.ACTIVE)   // filtrar solo activos
            .count();
        
        if (activeLoans >= Constants.MAX_ACTIVE_LOANS_PER_USER) {  // MAX_ACTIVE_LOANS_PER_USER = 3
            throw new LoanLimitExceededException(
                "Usuario " + userId + " excedio limite de " + Constants.MAX_ACTIVE_LOANS_PER_USER
            );
        }
        // Si el usuario ya tiene 3 prestamos activos, se lanza excepcion
        
        // PASO 3.4: Validar que no exista prestamo activo duplicado (mismo usuario+libro)
        boolean duplicateExists = loansMap.values().stream()
            .anyMatch(l -> l.getUser().getId().equals(userId)         // mismo usuario
                        && l.getBook().getId().equals(bookId)         // mismo libro
                        && l.getStatus() == LoanStatus.ACTIVE);       // sigue activo
        
        if (duplicateExists) {
            throw new IllegalArgumentException(
                "Ya existe un prestamo activo de " + bookId + " para usuario " + userId
            );
        }
        
        // PASO 3.5: Validar disponibilidad del libro
        if (book.getAvailable() <= 0) {
            throw new BookNotAvailableException.noCopiesAvailable(bookId);
        }
        // Si no hay copias disponibles, lanza excepcion
        
        // PASO 3.6: Crear nueva entidad Loan con estado inicial ACTIVE
        String loanId = IdGeneratorUtil.generateLoanId();  // genera "LOAN-001", "LOAN-002", etc
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setUser(user);
        loan.setBook(book);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLoanDate(LocalDateTime.now());                                           // fecha de hoy
        loan.setDueDate(DateUtil.calculateDueDate(Constants.MAX_LOAN_DAYS));              // hoy + 14 dias
        loan.setReturnDate(null);                                                         // aun no devuelto
        
        // PASO 3.7: Actualizar inventario del libro (disminuir disponibilidad)
        bookService.updateAvailability(bookId, -1);  // cambio = -1 (restar 1 copia)
        
        // PASO 3.8: Guardar prestamo en almacenamiento en memoria
        loansMap.put(loanId, loan);
        
        // PASO 3.9: Retornar prestamo creado hacia el controller
        return loan;
    }
}
```

**Que sucede:**
1. Se valida que usuario y libro existen
2. Se valida que no se supera el limite (máximo 3 prestamos activos)
3. Se valida que no hay duplicado activo (mismo usuario+libro no puede tener 2 prestamos activos)
4. Se valida que hay copias disponibles del libro
5. Se crea la entidad Loan con estado ACTIVE
6. Se decrementa el inventario del libro (resta 1 copia)
7. Se guarda en memoria y retorna

#### Paso 4: LoanMapper convierte Loan a LoanDTO

Archivo: `controller/mapper/LoanMapper.java`

```java
@Component
public class LoanMapper {
    
    public LoanDTO toLoanDTO(Loan loan) {
        // PASO 4: Mapear entidad Loan a DTO para salida HTTP
        return LoanDTO.builder()
            .id(loan.getId())                           // "LOAN-001"
            .book(bookMapper.toBookDTO(loan.getBook())) // convertir Book a BookDTO recursivamente
            .user(userMapper.toUserDTO(loan.getUser())) // convertir User a UserDTO recursivamente
            .status(loan.getStatus().toString())        // ACTIVE -> "ACTIVE"
            .loanDate(loan.getLoanDate())               // LocalDateTime
            .dueDate(loan.getDueDate())                 // LocalDateTime (fecha vencimiento)
            .returnDate(loan.getReturnDate())           // null (aun no devuelto)
            .build();
        // Retorna objeto DTO con toda la info del prestamo + objetos relacionados
    }
}
```

**Que sucede:**
- Se convierte la entidad Loan a DTO
- Los objetos relacionados (Book y User) tambien se convierten recursivamente
- El resultado es un objeto serializable a JSON

#### Paso 5: GlobalExceptionHandler captura cualquier error

Archivo: `core/exception/GlobalExceptionHandler.java`

Si en cualquier paso anterior falla (usuario no existe, libro no disponible, limite alcanzado, etc):

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Si ocurre BookNotAvailableException en Paso 3.5
    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<?> handleBookNotAvailable(BookNotAvailableException ex) {
        // Convertir excepcion de negocio a respuesta HTTP uniforme
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(409)                                    // HTTP 409 Conflict
            .message("Libro no disponible")
            .details(ex.getMessage())                      // detalles del error
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(409).body(errorResponse);
    }
    
    // Si ocurre LoanLimitExceededException en Paso 3.3
    @ExceptionHandler(LoanLimitExceededException.class)
    public ResponseEntity<?> handleLoanLimitExceeded(LoanLimitExceededException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(409)                                    // HTTP 409 Conflict
            .message("Limite de prestamos excedido")
            .details(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(409).body(errorResponse);
    }
    
    // Si ocurre MethodArgumentNotValidException (validacion DTO en Paso 2)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
            // Por ejemplo: "userId" -> "must not be blank"
        );
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(400)                                    // HTTP 400 Bad Request
            .message("Errores de validacion en DTO")
            .validationErrors(errors)
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(400).body(errorResponse);
    }
}
```

**Que sucede:**
- Cualquier excepcion lanzada se captura centralmente
- Se transforma a respuesta HTTP uniforme (ErrorResponse)
- El cliente recibe JSON estructurado con el error

#### Resumen visual del Flujo 1

```
Cliente
  |
  v (POST /api/loans con DTO)
LoanController
  | @Valid dispara validaciones Jakarta
  | CreateLoanDTO.userId, bookId son @NotBlank
  v
LoanService.createLoan()
  | Paso 3.1: validar usuario existe
  | Paso 3.2: validar libro existe
  | Paso 3.3: validar limite (<=3 activos)
  | Paso 3.4: validar no hay duplicado
  | Paso 3.5: validar disponibilidad (>0)
  | Paso 3.6-3.8: crear entidad y guardar
  v
LoanMapper.toLoanDTO()
  | convertir a DTO serializable
  v
ResponseEntity 201 CREATED
  |
  v
Cliente recibe: { id, book, user, status, loanDate, dueDate, returnDate }
```

---

### Flujo 2: Seguridad JWT (Autenticacion en cada request)

Este flujo ocurre para TODA peticion HTTP a rutas protegidas.

#### Paso 1: Cliente envia request con token Bearer

```bash
GET /api/loans/user/USR-001/active
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MzYwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDEsImlhdCI6MTcxNDg2MDAwMDB9.xxx
```

#### Paso 2: JwtAuthenticationFilter intercepta el request

Archivo: `security/JwtAuthenticationFilter.java`

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter: se ejecuta UNA VEZ por cada request (no en forwards internos)
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // PASO 2.1: Extraer header Authorization
        String authHeader = request.getHeader("Authorization");
        // Ejemplo: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        
        String token = null;
        String username = null;
        
        // PASO 2.2: Validar formato Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Header existe y comienza con "Bearer "
            token = authHeader.substring(7);  // extraer token sin "Bearer " (7 caracteres)
            // Ahora token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        }
        
        // PASO 2.3: Extraer username del token
        if (token != null) {
            try {
                username = jwtService.extractUsername(token);
                // username = "admin" (extraido del payload del JWT)
            } catch (Exception e) {
                // Si el token esta corrupto, expirado, etc, la excepcion es capturada
                // y no se autentica el usuario
                username = null;
            }
        }
        
        // PASO 2.4: Si se extrajo username y SecurityContext aun no tiene autenticacion
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // PASO 2.5: Validar que el token sea valido (firma, expiracion)
            if (jwtService.isTokenValid(token, username)) {
                // Token es genuino y no ha expirado
                
                // PASO 2.6: Crear objeto autenticado
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                // authToken contiene: principal="admin", credentials=null, authorities=[]
                
                // PASO 2.7: Guardar en SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                // Ahora Spring Security sabe que el usuario "admin" esta autenticado
            }
        }
        
        // PASO 2.8: Continuar la cadena de filtros
        filterChain.doFilter(request, response);
        // El request continua hacia el controlador si fue autenticado
        // o es bloqueado por SecurityFilterChain si no lo fue
    }
}
```

**Que sucede:**
1. Se extrae el header Authorization
2. Se valida que tenga formato "Bearer <token>"
3. Se extrae el username del token JWT (sin validar aun)
4. Se valida la firma e expiracion del token
5. Si es valido, se setea el usuario en SecurityContext
6. Spring Security permite o bloquea acceso segun la ruta

#### Paso 3: JwtService extrae y valida el token

Archivo: `security/JwtService.java`

```java
@Service
public class JwtService {
    
    @Value("${security.jwt.secret}")
    private String secret;  // "tu-secret-base64-aqui" (configurado en application.properties)
    
    @Value("${security.jwt.expiration-ms}")
    private long expiration;  // 3600000 ms = 1 hora
    
    // PASO 3.1: Extraer el username (subject) del token
    public String extractUsername(String token) {
        // JWT tiene 3 partes: header.payload.signature
        // Parsear sin validar firma primero:
        Claims claims = Jwts.parser()
            .verifyWith(Hmacs.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
            .build()
            .parseSignedClaims(token)  // si falla aqui, se lanza excepcion
            .getPayload();
        
        return claims.getSubject();  // "admin"
    }
    
    // PASO 3.2: Validar que el token no este expirado y tenga firma correcta
    public boolean isTokenValid(String token, String username) {
        try {
            // Parsear y validar firma
            Claims claims = Jwts.parser()
                .verifyWith(Hmacs.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            // Si llega aqui, la firma es correcta
            
            // Validar expiracion
            if (claims.getExpiration().before(new Date())) {
                return false;  // token expirado
            }
            
            // Validar que el username coincide
            if (!claims.getSubject().equals(username)) {
                return false;  // token no pertenece a este usuario
            }
            
            return true;  // token valido
            
        } catch (Exception e) {
            // Cualquier error en parseo o validacion = token invalido
            return false;
        }
    }
    
    // PASO 3.3: Generar un nuevo token (usado al hacer login)
    public String generateToken(String username) {
        // Crear claims (payload) del JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("iat", new Date());  // fecha de emision
        
        // Crear y firmar el token
        return Jwts.builder()
            .subject(username)                                  // "admin"
            .issuedAt(new Date())                               // ahora
            .expiration(new Date(System.currentTimeMillis() + expiration))  // ahora + 1 hora
            .signWith(Hmacs.hmacShaKeyFor(Decoders.BASE64.decode(secret))) // firmar con secret
            .compact();  // retornar string compacto (header.payload.signature)
    }
}
```

**Que sucede:**
1. Se parsea el JWT sin validar (extrae username)
2. Se valida la firma usando la secret key
3. Se valida que no esta expirado
4. Si todo paso, el token es genuino

#### Paso 4: SecurityConfig define que rutas son publicas/protegidas

Archivo: `security/SecurityConfig.java`

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) 
            throws Exception {
        
        return http
            // PASO 4.1: Desabilitar CSRF (API stateless no lo necesita)
            .csrf(csrf -> csrf.disable())
            
            // PASO 4.2: Desabilitar sesiones (stateless = sin cookies de sesion)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // PASO 4.3: Definir autorizaciones por ruta
            .authorizeHttpRequests(auth -> auth
                // Rutas PUBLICAS (no necesitan token)
                .requestMatchers("/auth/**").permitAll()                 // /auth/login, etc
                .requestMatchers("/swagger-ui/**").permitAll()          // Swagger UI
                .requestMatchers("/api-docs/**").permitAll()            // OpenAPI JSON
                .requestMatchers("/h2-console/**").permitAll()          // Consola H2
                .requestMatchers("/swagger-ui.htm").permitAll()         // Redirect compat
                
                // Rutas PROTEGIDAS (requieren token)
                .anyRequest().authenticated()                           // todas las demas
            )
            
            // PASO 4.4: Agregar filtro JWT a la cadena
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            // El filtro JWT se ejecuta ANTES de cualquier autenticacion por usuario/password
            
            .build();
    }
}
```

**Que sucede:**
1. Se especifica que /auth/**, /swagger-ui/**, /api-docs/**, /h2-console/** son publicas
2. Todas las demas rutas requieren autenticacion (token JWT valido)
3. El filtro JWT se ejecuta primero, antes que otros filtros

#### Paso 5: Si token es invalido o expira

```
Si el token NO es valido o esta EXPIRADO:

1. JwtService.isTokenValid() retorna false
2. No se setea SecurityContext
3. SecurityFilterChain bloquea acceso a ruta protegida
4. Respuesta HTTP 403 Forbidden

Respuesta:
HTTP/1.1 403 Forbidden

{
  "error": "Access Denied",
  "message": "Usuario no autenticado o token invalido"
}
```

#### Resumen visual del Flujo 2

```
Cliente envia request con header Authorization: Bearer <token>
  |
  v
JwtAuthenticationFilter.doFilterInternal()
  | extrae token del header
  v
JwtService.extractUsername(token)
  | parsea JWT sin validar
  v
JwtService.isTokenValid(token, username)
  | valida firma usando secret
  | valida que no esta expirado
  | valida que username coincide
  v
Si valido:
  | SecurityContext.setAuthentication(authToken)
  | request continua hacia controller
  v
Si invalido:
  | SecurityContext queda vacio
  | SecurityFilterChain bloquea acceso
  v
HTTP 403 Forbidden
```

---

### Flujo 3: Manejo Centralizado de Errores

Este flujo es transversal y ocurre cuando cualquier excepcion es lanzada en la aplicacion.

#### Ejemplo: Intento de prestar libro que no existe

```bash
POST /api/loans
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": "USR-001",
  "bookId": "BK-999"  # Libro no existe
}
```

#### Paso 1: BookService lanza excepcion

Archivo: `core/service/BookService.java`

```java
@Service
public class BookService {
    
    public Book getBookById(String id) {
        return booksMap.get(id);  // retorna null si no existe
        
        // Pero BookService NO retorna null directamente, lo valida:
        Book book = booksMap.get(id);
        if (book == null) {
            // PASO 1: Lanzar excepcion de negocio especifica
            throw new ResourceNotFoundException("Libro con id " + id + " no encontrado");
        }
        return book;
    }
}
```

**Que sucede:**
- Se valida que el libro existe
- Si no existe, se lanza ResourceNotFoundException (excepcion controlada)

#### Paso 2: Excepcion sube por la cadena

La excepcion viaja hacia arriba:
```
LoanController.createLoan()
  |
  v
LoanService.createLoan()
  | calls BookService.getBookById()  <- aqui se lanza ResourceNotFoundException
  | exception sube automaticamente
  v
JwtAuthenticationFilter
  | no la maneja, sigue subiendo
  v
Spring DispatcherServlet
  | no la maneja, sigue subiendo
  v
GlobalExceptionHandler
  | LA CAPTURA Y LA MANEJA
```

#### Paso 3: GlobalExceptionHandler maneja la excepcion

Archivo: `core/exception/GlobalExceptionHandler.java`

```java
@RestControllerAdvice  // Valida todas las excepciones en toda la app
public class GlobalExceptionHandler {
    
    // PASO 3: Metodo especifico para ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        
        // PASO 3.1: Crear respuesta uniforme
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(404)                                      // HTTP 404 Not Found
            .message("Recurso no encontrado")
            .details(ex.getMessage())                         // "Libro con id BK-999 no encontrado"
            .timestamp(LocalDateTime.now())                   // fecha/hora del error
            .build();
        
        // PASO 3.2: Retornar HTTP response con error
        return ResponseEntity.status(404).body(errorResponse);
    }
    
    // Metodo generico para excepciones no esperadas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(500)                                      // HTTP 500 Internal Server Error
            .message("Error interno del servidor")
            .details(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(500).body(errorResponse);
    }
}
```

**Que sucede:**
1. Se determina el tipo de excepcion lanzada
2. Se busca el @ExceptionHandler correspondiente
3. Se construye ErrorResponse uniforme
4. Se retorna HTTP response con codigo de estado apropiado

#### Paso 4: Cliente recibe respuesta de error

```json
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "status": 404,
  "message": "Recurso no encontrado",
  "details": "Libro con id BK-999 no encontrado",
  "timestamp": "2026-04-07T12:34:56",
  "validationErrors": {}
}
```

#### Ejemplo 2: Validacion de DTO invalido

Si el cliente envia:
```json
POST /api/loans
{
  "userId": "",        # vacio, viola @NotBlank
  "bookId": "BK-001"
}
```

Que sucede:
```
1. LoanController.createLoan(@Valid CreateLoanDTO dto)
   |
   v
2. @Valid valida anotaciones Jakarta en DTO
   | Encuentra que userId es @NotBlank pero esta vacio
   v
3. @Valid lanza MethodArgumentNotValidException
   | (no es excepcion de negocio, es de framework)
   v
4. GlobalExceptionHandler.handleValidationError()
   | captura MethodArgumentNotValidException
   v
5. Construye ErrorResponse con detalles de validacion
   |
   v
HTTP/1.1 400 Bad Request

{
  "status": 400,
  "message": "Errores de validacion en DTO",
  "validationErrors": {
    "userId": "must not be blank"
  },
  "timestamp": "2026-04-07T12:34:56"
}
```

#### Resumen visual del Flujo 3

```
Excepcion lanzada en cualquier capa
  |
  v
Spring captura excepcion
  | (cadena de metodos termina con excepcion no capturada)
  v
GlobalExceptionHandler
  | busca @ExceptionHandler para ese tipo
  |
  +-- ResourceNotFoundException --> HTTP 404
  +-- BookNotAvailableException --> HTTP 409
  +-- LoanLimitExceededException --> HTTP 409
  +-- MethodArgumentNotValidException --> HTTP 400
  +-- Exception (generico) --> HTTP 500
  v
Construye ErrorResponse uniforme
  |
  v
ResponseEntity con codigo de estado HTTP
  |
  v
Cliente recibe JSON estructurado con error
```

---

### Conexion de los 3 flujos en un caso real completo

Escenario: Usuario intenta crear prestamo, todo falla por validacion

```bash
# 1. Cliente envia request INVALIDO (userId vacio)
POST /api/loans
Authorization: Bearer token-valido
Content-Type: application/json

{ "userId": "", "bookId": "BK-001" }
```

**Que sucede internamente:**

```
1. HTTP request entra a servidor

2. JwtAuthenticationFilter.doFilterInternal() [FLUJO 2]
   - extrae token del header
   - valida JWT (si es valido, SecurityContext se autentica)
   - request continua

3. SecurityFilterChain [FLUJO 2]
   - valida que /api/loans requiere autenticacion
   - SecurityContext tiene autenticacion, permite acceso

4. LoanController.createLoan(@Valid CreateLoanDTO dto) [FLUJO 1]
   - @Valid dispara validaciones Jakarta

5. Validacion de DTO falla [FLUJO 3]
   - userId = "" viola @NotBlank
   - @Valid lanza MethodArgumentNotValidException

6. GlobalExceptionHandler.handleValidationError() [FLUJO 3]
   - captura MethodArgumentNotValidException
   - crea ErrorResponse con detalles de validacion

7. ResponseEntity 400 Bad Request retorna al cliente

8. Cliente recibe:
   {
     "status": 400,
     "message": "Errores de validacion en DTO",
     "validationErrors": { "userId": "must not be blank" },
     "timestamp": "2026-04-07T12:34:56"
   }
```

---

## Explicacion de Todas las Clases

En esta seccion se explica para que existe cada clase y que parte de codigo concentra su valor principal.

### Entrada y configuracion

- DoswLibraryApplication: punto de arranque de Spring Boot.
	- Parte clave: metodo main con SpringApplication.run.
	- Por que existe: inicializa todo el contexto IoC.

- OpenApiConfig: configura metadata OpenAPI y esquema bearerAuth.
	- Parte clave: bean OpenAPI con SecurityScheme HTTP bearer JWT.
	- Por que existe: centraliza documentacion y seguridad de Swagger.

### Controladores

- BookController: expone endpoints de libros.
	- Parte clave: updateInventory con operaciones SET/ADD/REMOVE.
	- Por que existe: orquesta request/response y delega en servicio.

- UserController: expone endpoints de usuarios.
	- Parte clave: updateUser con PATCH y merge parcial de datos.
	- Por que existe: API de ciclo de vida de usuarios.

- LoanController: expone endpoints de prestamos.
	- Parte clave: createLoan y returnLoan, mas listado summary opcional.
	- Por que existe: capa HTTP del proceso de prestamo.

- SwaggerRedirectController: redirige /swagger-ui.htm a /swagger-ui.html.
	- Parte clave: @GetMapping("/swagger-ui.htm") -> redirect.
	- Por que existe: compatibilidad con rutas legacy.

### DTOs de libros

- BookDTO: respuesta completa de libro.
	- Parte clave: id, title, author, copies, available.

- CreateBookDTO: entrada para crear libro.
	- Parte clave: validaciones @NotBlank, @Size, @Min.

- UpdateBookInventoryDTO: entrada para cambios de inventario.
	- Parte clave: enum InventoryOperation { SET, ADD, REMOVE }.

### DTOs de usuarios

- UserDTO: respuesta de usuario.
	- Parte clave: datos publicables del usuario.

- CreateUserDTO: alta de usuario.
	- Parte clave: validacion de email con @Email.

- UpdateUserDTO: actualizacion parcial de usuario.
	- Parte clave: campos opcionales para PATCH.

- LoginRequest: credenciales para autenticacion.
	- Parte clave: username/password.
	- Nota: hoy no existe controlador explicito que lo consuma.

### DTOs de prestamos

- CreateLoanDTO: request de nuevo prestamo.
	- Parte clave: bookId y userId obligatorios.

- LoanDTO: respuesta completa del prestamo.
	- Parte clave: objetos BookDTO y UserDTO anidados.

- LoanSummaryDTO: respuesta liviana para listados.
	- Parte clave: evita payload grande en consultas masivas.

### Mappers

- BookMapper: conversion Book <-> DTO y aplicacion de inventario.
	- Parte clave: updateInventory con switch SET/ADD/REMOVE.

- UserMapper: conversion User <-> DTO y update parcial.
	- Parte clave: updateEntity modifica solo campos no nulos.

- LoanMapper: conversion Loan -> LoanDTO y LoanSummaryDTO.
	- Parte clave: mapea relaciones (book y user) y enum status.

### Modelos de dominio

- Book: entidad de catalogo e inventario.
	- Parte clave: available depende de copies.

- User: entidad de usuario.
	- Parte clave: email unico a nivel de columna.

- Loan: entidad de prestamo con relaciones ManyToOne a Book/User.
	- Parte clave: status, loanDate y returnDate.

- LoanStatus: enum de estado.
	- Parte clave: ACTIVE y RETURNED.

### Servicios

- BookService: logica de inventario y consultas de libros.
	- Parte clave: updateAvailability evita copias negativas.
	- Por que existe: encapsula reglas de inventario.

- UserService: logica de alta, consulta y actualizacion de usuarios.
	- Parte clave: validacion de email unico.
	- Por que existe: protege integridad de usuarios.

- LoanService: logica central de prestamos.
	- Parte clave: createLoan valida limite (max 3), duplicados y stock.
	- Parte clave: returnLoan devuelve inventario y cambia estado.
	- Por que existe: concentra reglas de negocio complejas.

### Repositorios

- BookRepository, UserRepository, LoanRepository.
	- Parte clave: interfaces JpaRepository con metodos de consulta.
	- Por que existen: punto de persistencia para una evolucion a BD real.
	- Nota de avance: los servicios actuales usan estructura en memoria, no inyeccion directa de estos repositorios.

### Validadores

- ValidationUtil: utilidades genericas de validacion.
	- Parte clave: throwIfInvalid para concentrar errores.

- BookValidator: reglas semanticas de libros.
	- Parte clave: consistencia copies/available y formato ID.

- UserValidator: reglas semanticas de usuarios.
	- Parte clave: regex de email y restricciones de longitud.

- LoanValidator: reglas semanticas de prestamos.
	- Parte clave: fechas, transiciones de estado y relaciones validas.

### Excepciones

- ErrorResponse: contrato uniforme de errores.
	- Parte clave: builder + campos para detalles y validaciones.

- GlobalExceptionHandler: traduce excepciones a HTTP.
	- Parte clave: @ExceptionHandler por tipo y respuestas consistentes.

- ResourceNotFoundException: recurso generico no encontrado.
	- Parte clave: RuntimeException para 404 estandar.

- UserNotFoundException: usuario no encontrado.
	- Parte clave: factories byId/byEmail.

- BookNotAvailableException: no hay disponibilidad para prestar.
	- Parte clave: factories noCopiesAvailable y withDetails.

- LoanLimitExceededException: usuario supero maximo de prestamos.
	- Parte clave: factory withLimit.

### Seguridad

- SecurityConfig: cadena de seguridad y permisos de rutas.
	- Parte clave: SessionCreationPolicy.STATELESS y addFilterBefore(jwtFilter).
	- Parte clave: usuario en memoria configurable por properties.

- JwtService: generacion/validacion de token.
	- Parte clave: generateToken, extractUsername, isTokenValid.

- JwtAuthenticationFilter: autentica request por token Bearer.
	- Parte clave: setea SecurityContext si token es valido.

### Utilidades

- Constants: valores globales para negocio y validaciones.
	- Parte clave: MAX_ACTIVE_LOANS_PER_USER, MAX_LOAN_DAYS, prefijos de ID.

- DateUtil: reglas de fecha (vencimiento y atraso).
	- Parte clave: calculateDueDate, isLoanOverdue, daysUntilDue.

- IdGeneratorUtil: generacion uniforme de IDs.
	- Parte clave: generateBookId, generateUserId, generateLoanId.

### Tests

- BookServiceTest: cubre alta, disponibilidad, inventario y errores basicos.
- UserServiceTest: cubre alta, consulta, update, delete y duplicados.
- LoanServiceTest: cubre creacion/devolucion, limite, no disponibilidad y duplicados.
- DoswLibraryApplicationTests: smoke test del contexto minimo.

## Como Implementar y Extender el Proyecto

Esta guia muestra como implementar nuevas funcionalidades manteniendo el estilo del codigo existente.

### Patron de implementacion recomendado

1. Definir DTO de entrada/salida con validaciones Jakarta.
2. Crear o extender Mapper para conversiones.
3. Implementar reglas en Service.
4. Exponer endpoint en Controller.
5. Agregar cobertura en tests unitarios.
6. Documentar en OpenAPI (anotaciones @Operation, @ApiResponse).

### Ejemplo A: agregar una nueva operacion de negocio

Caso: agregar renovacion de prestamo.

1. Crear metodo en LoanService, por ejemplo renewLoan(String loanId).
2. Validar que el prestamo exista y este ACTIVE.
3. Aplicar regla (por ejemplo, maximo de renovaciones).
4. Actualizar fecha o metadata necesaria.
5. Exponer endpoint en LoanController: PUT /api/loans/{id}/renew.
6. Añadir tests para caso exitoso y errores.

### Ejemplo B: agregar nuevo recurso (Category)

1. Crear entidad Category.
2. Crear CategoryDTO, CreateCategoryDTO.
3. Crear CategoryMapper.
4. Crear CategoryService con reglas.
5. Crear CategoryController.
6. Crear CategoryServiceTest.

Plantilla minima de servicio:

```java
@Service
public class CategoryService {
		private final Map<String, Category> storage = new HashMap<>();

		public Category create(Category category) {
				if (storage.containsKey(category.getId())) {
						throw new IllegalArgumentException("Category already exists");
				}
				storage.put(category.getId(), category);
				return category;
		}
}
```

### Ejemplo C: implementar autenticacion explicita

Dado que existe LoginRequest y JwtService, se puede crear un AuthController:

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
		@PostMapping("/login")
		public ResponseEntity<?> login(@RequestBody LoginRequest request) {
				// 1) autenticar con AuthenticationManager
				// 2) generar token con JwtService
				// 3) retornar token
				return ResponseEntity.ok().build();
		}
}
```

### Buenas practicas para este proyecto

- Mantener logica de negocio solo en Service.
- Evitar logica compleja en Controller.
- Reutilizar ValidationUtil y validadores por dominio.
- Mantener mensajes de error consistentes via GlobalExceptionHandler.
- Escribir tests para cada nueva regla.

## Pruebas y Cobertura Actual

### Lo que ya esta cubierto

- BookService: reglas de inventario y disponibilidad.
- UserService: altas, consultas y unicidad.
- LoanService: flujo principal y reglas criticas.

### Huecos de prueba actuales

- Controladores HTTP con MockMvc.
- Seguridad JWT en integracion.
- GlobalExceptionHandler con tests dedicados.
- DTOs y mappers con pruebas unitarias especificas.

## Riesgos Tecnicos y Mejoras Recomendadas

### Riesgos detectados

- Secret JWT y credenciales admin en properties (hardcoded).
- Falta endpoint explicito de login pese a la configuracion de seguridad.
- Servicios actualmente en memoria, no conectados a repositorios JPA.

### Mejoras sugeridas

1. Mover secretos a variables de entorno o vault.
2. Implementar AuthController para /auth/login.
3. Migrar servicios a repositorios JPA para persistencia real.
4. Agregar tests de integracion (MockMvc + Spring Security Test).
5. Incorporar paginacion y filtros en listados.

## Glosario

Este glosario enlaza conceptos clave del proyecto y su contexto en el documento.

- [API REST](#termino-api-rest)
- [DTO](#termino-dto)
- [Entity](#termino-entity)
- [Service](#termino-service)
- [Controller](#termino-controller)
- [Mapper](#termino-mapper)
- [Validator](#termino-validator)
- [JWT](#termino-jwt)
- [OpenAPI](#termino-openapi)
- [Swagger UI](#termino-swagger-ui)
- [JPA](#termino-jpa)
- [H2](#termino-h2)
- [Exception Handler](#termino-exception-handler)

### Termino: API REST

Interfaz HTTP basada en recursos y verbos (GET, POST, PATCH, PUT, DELETE).

### Termino: DTO

Objeto de transferencia para entrada/salida HTTP, separado de la entidad interna.

### Termino: Entity

Clase de dominio persistible, anotada con JPA (@Entity, @Id, etc.).

### Termino: Service

Capa de negocio donde viven reglas y validaciones semanticas.

### Termino: Controller

Componente REST que recibe peticiones, valida y delega al Service.

### Termino: Mapper

Componente que convierte Entity <-> DTO.

### Termino: Validator

Componente especializado para validar reglas de negocio complejas.

### Termino: JWT

Token firmado para autenticacion stateless.

### Termino: OpenAPI

Especificacion de contrato de API consumida por Swagger UI.

### Termino: Swagger UI

Interfaz web para explorar y probar endpoints documentados.

### Termino: JPA

Abstraccion de persistencia objeto-relacional en Java.

### Termino: H2

Base de datos embebida/en memoria usada para desarrollo y pruebas.

### Termino: Exception Handler

Mecanismo centralizado para transformar excepciones en respuestas HTTP uniformes.