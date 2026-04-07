# DOSW-Library

Informe de avance tecnico del proyecto de biblioteca desarrollado con Spring Boot.

Este documento esta pensado como referencia de arquitectura, guia de implementacion y manual funcional del proyecto.

## Indice

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Objetivo y Alcance](#objetivo-y-alcance)
3. [Stack Tecnologico](#stack-tecnologico)
4. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
5. [Estructura de Paquetes](#estructura-de-paquetes)
6. [Configuracion y Ejecucion](#configuracion-y-ejecucion)
7. [Endpoints Implementados](#endpoints-implementados)
8. [Flujos Funcionales Clave](#flujos-funcionales-clave)
9. [Explicacion de Todas las Clases](#explicacion-de-todas-las-clases)
10. [Como Implementar y Extender el Proyecto](#como-implementar-y-extender-el-proyecto)
11. [Pruebas y Cobertura Actual](#pruebas-y-cobertura-actual)
12. [Riesgos Tecnicos y Mejoras Recomendadas](#riesgos-tecnicos-y-mejoras-recomendadas)
13. [Glosario](#glosario)

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