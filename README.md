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
7. [Modelo Entidad-Relación Normalizado a 3FN](#modelo-entidad-relación-normalizado-a-3fn)
8. [Paquetes y Clases: Responsabilidad Detallada](#paquetes-y-clases-responsabilidad-detallada)
9. [Configuracion y Ejecucion](#configuracion-y-ejecucion)
10. [Endpoints Implementados](#endpoints-implementados)
11. [Flujos Funcionales Clave](#flujos-funcionales-clave)
12. [Flujo Entre Paquetes y Clases](#flujo-entre-paquetes-y-clases)
13. [Explicacion Paso a Paso: Flujos con Codigo Real](#explicacion-paso-a-paso-flujos-con-codigo-real)
14. [Explicacion de Todas las Clases](#explicacion-de-todas-las-clases)
15. [Como Implementar y Extender el Proyecto](#como-implementar-y-extender-el-proyecto)
16. [Pruebas y Cobertura Actual](#pruebas-y-cobertura-actual)
17. [Riesgos Tecnicos y Mejoras Recomendadas](#riesgos-tecnicos-y-mejoras-recomendadas)
18. [Glosario](#glosario)

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

- **Capa de presentacion**: controladores REST, DTOs y mappers (`controller`)
- **Capa de negocio**: servicios, validadores, excepciones (`core.service`)
- **Capa de dominio**: modelos de negocio que SON entidades JPA (`core.model` con @Entity)
- **Capa de persistencia**: repositorios JPA y DAOs para queries complejas (`persistence`)
- **Capa transversal**: seguridad JWT, configuracion OpenAPI, utilidades (`security`, `config`)

### Principio de diseno aplicado

Cada clase existe para resolver una responsabilidad concreta:

- Controllers: exponen HTTP y delegan
- Services: aplican reglas de negocio
- Mappers (persistence.mapper): convierten en ambas direcciones: DTO ↔ Model ↔ Entity
- Validators: concentran validacion semantica
- Exception Handler: estandariza errores
- Security: autentica y autoriza

### Arquitectura de capas (vea Estructura de Paquetes)

```
┌─────────────────────────────────────────────────────┐
│  HTTP LAYER (Client: navegador, postman, otra app) │
└──────────────────┬──────────────────────────────────┘
                   │ JSON Request/Response
                   ↓
┌─────────────────────────────────────────────────────┐
│  PRESENTATION LAYER (controller)                    │
│  - BookController, UserController, LoanController  │
│  - Exponen DTOs y manejan routing HTTP              │
└──────────────────┬──────────────────────────────────┘
                   │ Mapeo: DTO → Model/Domain
                   ↓
┌─────────────────────────────────────────────────────┐
│  MAPPER LAYER (persistence.mapper) ✅ CONSOLIDADO │
│  - BookPersistenceMapper, UserPersistenceMapper,     │
│    LoanPersistenceMapper                            │
│  - Convierten: DTO ↔ Model ↔ Entity (4-tier)        │
└──────────────────┬──────────────────────────────────┘
                   │ Objetos de dominio
                   ↓
┌─────────────────────────────────────────────────────┐
│  BUSINESS LOGIC LAYER (core.service)                │
│  - BookService, UserService, LoanService            │
│  - Aplican reglas de negocio y validaciones         │
│  - Orquestan persistencia y cross-domain logic      │
└──────────────────┬──────────────────────────────────┘
                   │ Modelos de dominio validados
                   ↓
┌─────────────────────────────────────────────────────┐
│  PERSISTENCE LAYER (persistence.*)                  │
│  ┌─────────────────────────────────────────────┐   │
│  │ core.model.*: Book, User, Loan              │   │
│  │ - SON entidades JPA (@Entity, @Table)       │   │
│  │ - Modelos de dominio + persistencia en uno  │   │
│  │ - No hay transformacion Entity ↔ Model      │   │
│  └─────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────┐   │
│  │ persistence.repository: JpaRepository       │   │
│  │ - Queries CRUD automaticas + custom queries │   │
│  │ - findById(), save(), findByUsername(), etc │   │
│  └─────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────┐   │
│  │ persistence.dao: BookDAO, UserDAO, LoanDAO  │   │
│  │ - Operaciones batch y queries complejas     │   │
│  │ - decrementAvailableCopies(), renovarLoan() │   │
│  └─────────────────────────────────────────────┘   │
└──────────────────┬──────────────────────────────────┘
                   │ SQL Queries
                   ↓
┌─────────────────────────────────────────────────────┐
│  DATABASE LAYER (H2 en memoria)                     │
│  - Tablas: books, users, loans                      │
│  - Constraints: FK, UNIQUE, CHECK, NOT NULL        │
│  - Normalizacion: 3FN en todas las tablas           │
└─────────────────────────────────────────────────────┘

CROSS-CUTTING CONCERNS:
  - security.*: JWT authentication, authorization
  - config.*: OpenAPI, bean definitions
  - core.exception.*: Global error handler
  - core.util.*: Constants, DateUtil, IdGeneratorUtil
```

**Flujo típico de un POST /api/loans:**

```
[Cliente HTTP]
    ↓ POST JSON (user_id, book_id)
[LoanController.createLoan()]
    ↓ @Valid CreateLoanDTO (validacion Jakarta)
[LoanPersistenceMapper.toDomain()] → Loan model (@Entity)
    ↓ Loan object (dominio + JPA entity)
[LoanService.createLoan()] → aplica reglas
    ↓ Loan validado (dominio)
[LoanRepository.save(loan)]
    ↓ INSERT en tabla loans (BD via JPA/Hibernate)
[H2 Database]
    ↓ COMMIT (transaccion)
[LoanRepository.findById()] → Loan desde BD
    ↓ Loan confirmado (dominio)
[LoanPersistenceMapper.toDTO()] → LoanDTO
    ↓ HTTP 201 Created + LoanDTO JSON
[Cliente HTTP] ← Response
```

**Diferencia clave con arquitecturas anteriores:**
- ✅ ANTES: DTO → Model → Entity → BD (3 clases)
- ✅ AHORA: DTO → Model (@Entity) → BD (2 clases)
- El Model ES la entidad JPA, no hay mapeo redundante Model ↔ Entity

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
|       |-- ❌ ELIMINADA (consolidada en persistence/mapper)
|-- core
|   |-- model                        (Entidades JPA + Modelos de dominio)
|   |   |-- Book.java               (@Entity, @Table("books"))
|   |   |-- User.java               (@Entity, @Table("users"))
|   |   |-- Loan.java               (@Entity, @Table("loans"))
|   |   |-- LoanStatus.java         (Enum)
|   |-- service
|   |   |-- BookService.java
|   |   |-- UserService.java
|   |   |-- LoanService.java
|   |-- repository             (En core, no en persistence - Aqui van los JpaRepository)
|   |   |-- BookRepository.java      (extends JpaRepository<Book, String>)
|   |   |-- UserRepository.java      (extends JpaRepository<User, String>)
|   |   |-- LoanRepository.java      (extends JpaRepository<Loan, String>)
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
|-- persistence
    |-- dao                 (Data Access Objects para queries complejas)
        |-- BookDAO.java    (Queries batch, reportes sobre libros)
        |-- UserDAO.java    (Queries batch, reportes sobre usuarios)
        |-- LoanDAO.java    (Queries batch, reportes sobre prestamos)
|-- security
    |-- SecurityConfig.java
    |-- JwtService.java
    |-- JwtAuthenticationFilter.java
```

**Nota arquitectonica importante:**
- ✅ `core.model` = Entidades JPA (@Entity) + Modelos de dominio (dual propósito)
- ✅ `core.repository` = Spring Data JpaRepository (simples CRUD + custom queries)
- ✅ `persistence.dao` = Complex queries, batch operations, reportes
- ❌ `persistence.entity` = ELIMINADO (redundante con core.model)
- ❌ `persistence.mapper` = ELIMINADO (no hay transformación Entity ↔ Model)

La arquitectura fue simplificada eliminando duplicación: los modelos de dominio SON las entidades JPA.

**Modelo Entidad-Relación Normalizado a 3FN:**

![DOSW Library - Modelo Entidad Relación 3FN](images/Diagrama%20ER%203FN.png)

### Entidades Principales

#### 1. USER (Usuarios)

**Proposito:** Almacenar información de usuarios con roles diferenciados.

| Atributo | Tipo | Restricción | Descripción |
|----------|------|------------|-------------|
| `id` | VARCHAR(20) | PK | Identificador único (USR-001, USR-002...) |
| `name` | VARCHAR(100) | NOT NULL | Nombre completo |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL | Email para contacto/recuperación |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL | **NUEVO**: Username para login |
| `password` | VARCHAR(255) | NOT NULL | **NUEVO**: Hash BCrypt de contraseña |
| `role` | ENUM | NOT NULL | **NUEVO**: BIBLIOTECARIO o USUARIO |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Fecha de registro |
| `updated_at` | TIMESTAMP | DEFAULT NOW() | Última actualización |

**Roles:**
- `BIBLIOTECARIO`: Gestiona libros y usuarios, ve todos los préstamos
- `USUARIO`: Solicita y devuelve prestamos, ve solo sus prestamos

---

#### 2. BOOK (Libros)

**Proposito:** Catálogo de libros e inventario de ejemplares.

| Atributo | Tipo | Restricción | Descripción |
|----------|------|------------|-------------|
| `id` | VARCHAR(20) | PK | Identificador único (BK-001, BK-002...) |
| `title` | VARCHAR(200) | NOT NULL | Título del libro |
| `author` | VARCHAR(100) | NOT NULL | Autor |
| `copies` | INT | NOT NULL, CHECK (>0) | **CRÍTICO**: Stock total (ejemplares totales) |
| `available` | INT | NOT NULL, CHECK (≥0 ≤ copies) | **CAMBIO**: Cantidad disponible (antes era BOOLEAN) |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Cuando se agregó el libro |
| `updated_at` | TIMESTAMP | DEFAULT NOW() | Última actualización |

**Cambio importante:** `available` cambió de BOOLEAN a INT

- ❌ **Antes (incorrecto)**: `available = TRUE/FALSE` → solo dice "hay o no hay"
- ✅ **Ahora (correcto)**: `available = INT` → cantidad exacta de copias disponibles

**Invariante:**
```
copies >= available >= 0
Si disponibles < 0 → ERROR (violación de lógica)
Si disponibles > copies → ERROR (más disponibles que stock total)
```

---

#### 3. LOAN (Préstamos)

**Proposito:** Registro de préstamos de libros a usuarios.

| Atributo | Tipo | Restricción | Descripción |
|----------|------|------------|-------------|
| `id` | VARCHAR(20) | PK | Identificador único (LOAN-001...) |
| `user_id` | VARCHAR(20) | FK → USER, NOT NULL | Referencia al usuario que pide prestado |
| `book_id` | VARCHAR(20) | FK → BOOK, NOT NULL | Referencia al libro prestado |
| `loan_date` | TIMESTAMP | NOT NULL | Fecha de préstamo |
| `due_date` | TIMESTAMP | NOT NULL | **NUEVO**: Fecha de vencimiento (loan_date + 14 días) |
| `return_date` | TIMESTAMP | NULL | Fecha de devolución (NULL si aún no devuelto) |
| `status` | ENUM | NOT NULL | ACTIVE o RETURNED |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Registro del préstamo |
| `updated_at` | TIMESTAMP | DEFAULT NOW() | Última modificación |

**Estados del Préstamo:**
- `ACTIVE`: Préstamo vigente, libro no devuelto
- `RETURNED`: Libro devuelto, return_date tiene fecha

**Regla crítica:** Transición de estado
```
ACTIVE → RETURNED (cuando se devuelve el libro)
Si status = ACTIVE → return_date DEBE ser NULL
Si status = RETURNED → return_date DEBE tener fecha
```

---

### Relaciones (1:N)

#### Relación 1: User ↔ Loan

```
User (1) ----< Loan (N)
  ↓
Un usuario puede tener 0 o más préstamos
Un préstamo pertenece a exactamente 1 usuario
```

**Integridad referencial:**
```sql
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
-- Si se elimina usuario → sus préstamos se eliminan automáticamente
```

---

#### Relación 2: Book ↔ Loan

```
Book (1) ----< Loan (N)
  ↓
Un libro puede ser prestado 0 o más veces
Un préstamo es de exactamente 1 libro
```

**Integridad referencial:**
```sql
FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
-- Si se elimina libro → sus préstamos se eliminan automáticamente
```

---

### Normalización a 3FN

#### Primera Forma Normal (1FN)
✅ **CUMPLE**: Todos los atributos son atómicos

- No hay grupos repetidos
- `role` es ENUM (valor único, no lista)
- Todos los valores son indivisibles

#### Segunda Forma Normal (2FN)
✅ **CUMPLE**: Todos los atributos no-clave dependen completamente de la PK

**Ejemplo - Tabla USER:**
```
PK = { id }

Cada atributo depende SOLO de id:
- name → {id}
- email → {id}
- role → {id}
- NO hay dependencias parciales
```

#### Tercera Forma Normal (3FN)
✅ **CUMPLE**: Sin dependencias transitivas

**Lo que evitamos:**
```
❌ INCORRECTO (violaría 3FN):
Loan {
    id, user_id, user_name, user_email,
    book_id, book_title, book_author, ...
}
Problema: user_name depende transitivamente de user_id (no de id de Loan)

✅ CORRECTO (cumple 3FN):
Loan {
    id, user_id, book_id, loan_date, due_date, status
}
Los datos del usuario se acceden vía JOIN con User table
Los datos del libro se acceden vía JOIN con Book table
```

---

### Resumen de Cambios vs Versión Primera

| Elemento | Primera Versión | Versión Actual | Por qué |
|----------|-----------------|----------------|---------|
| User.username | ❌ No existía | ✅ VARCHAR(50) UNIQUE | Requerido para login |
| User.password | ❌ No existía | ✅ VARCHAR(255) BCrypt | Autenticación segura |
| User.role | ❌ Solo "USER" hardcodeado | ✅ ENUM(BIBLIOTECARIO, USUARIO, ADMIN) | Autorización por roles |
| Book.available | ❌ BOOLEAN | ✅ INT | Cantidad exacta de copias |
| Loan.due_date | ❌ No existía | ✅ TIMESTAMP | Fecha de vencimiento |
| Loan.returnDate | ❌ LocalDate | ✅ LocalDateTime | Precisión de tiempo |
| Timestamps | ❌ No existían | ✅ created_at, updated_at | Auditoría |
| persistence.entity | ✅ Existían | ❌ **ELIMINADOS** | core.model YA son @Entity |
| persistence.mapper | ✅ Existían | ❌ **ELIMINADOS** | No hay transformación Entity↔Entity |
| LoanDTO.dueDate | ❌ No existía | ✅ LocalDateTime | Mapeo completo de Loan |
| CreateUserDTO | ✅ (id, name, email) | ✅ (+ username, password, role) | Campos requeridos para login |
| BookPersistenceMapper | ✅ Consolidado | ✅ Funcionalidad completa | Conversión correcta de tipos |

---

**Arquitectura Simplificada (Refactor Final):**

**Antes (Redundante - DOS CAPAS DE MAPPERS):**
```
DTO ↔ (controller.mapper) ↔ Model ↔ (persistence.mapper) ↔ Entity → Repository → BD
```

**Ahora (Limpio - MAPPERS CONSOLIDADOS):**
```
DTO ↔ (persistence.mapper consolidado) ↔ Model(@Entity) ↔ Repository → BD
```

**Cambios realizados:**
- ✅ BO1BookPersistenceMapper: Ahora maneja DTO ↔ Book ↔ BookEntity  
- ✅ UserPersistenceMapper: Ahora maneja DTO ↔ User ↔ UserEntity
- ✅ LoanPersistenceMapper: Ahora maneja DTO ↔ Loan ↔ LoanEntity  
- ❌ **ELIMINADA** carpeta `controller/mapper` (redundante)

**Beneficios:**
- ✅ Menos leyes de LOC (3 mappers en lugar de 6)
- ✅ Mappers en la capa correcta (persistence, no controller)
- ✅ Menos complejidad (una sola transformación por entidad)
- ✅ Fácil de mantener

---

### Validaciones en la Base de Datos

```sql
-- Integridad de INVENTARIO
CHECK (copies > 0)                      -- Stock inicial > 0
CHECK (available >= 0 AND available <= copies)  -- Cantidad válida

-- Integridad de FECHAS
CHECK (loan_date <= due_date)           -- loan_date ≤ due_date

-- Integridad de ESTADO
CHECK (
  (status = 'ACTIVE' AND return_date IS NULL)
  OR (status = 'RETURNED' AND return_date IS NOT NULL)
)

-- Unicidad
UNIQUE (email)                          -- Email único por usuario
UNIQUE (username)                       -- Username único por usuario
```

---

## Paquetes y Clases: Responsabilidad Detallada

### Paquete principal

- Rol: punto de arranque y frontera del escaneo de Spring.
- Clase:
	- DoswLibraryApplication: inicia toda la aplicacion.

### config ✅ IMPLEMENTADO

**Rol:** Configuración transversal de la aplicación

**Clases implementadas:**

#### **OpenApiConfig** (@Configuration)
```
Responsabilidad: Documentación OpenAPI/Swagger

Configura:
  ├─ @Bean OpenAPI para metadata global
  │  ├─ Info: título, descripción, versión (1.0.0)
  │  ├─ License: información de licencia
  │  └─ Contact: información del desarrollador
  │
  ├─ SecurityScheme para JWT Bearer
  │  ├─ Type: HTTP
  │  ├─ Scheme: Bearer
  │  ├─ bearerFormat: JWT
  │  └─ Description: Token JWT requerido
  │
  └─ SecurityRequirement global
     └─ Todas las rutas requieren JWT (excepto públicas)

Problemas resueltos ✅:
  - ❌ Redirección /swagger-ui.htm → /swagger-ui.html
  - ✅ SwaggerRedirectController mitiga este problema
  - ✅ OpenAPI schema expone correctamente Bearer JWT
```

#### **SecurityConfig** (@Configuration)
```
Responsabilidad: Configuración de Spring Security + JWT

Configura:
  ├─ @Bean PasswordEncoder (BCrypt para passwords)
  │  └─ Hashing seguro de credenciales
  │
  ├─ @Bean SecurityFilterChain (@EnableMethodSecurity)
  │  ├─ Rutas públicas: POST /api/auth/login, POST /api/users
  │  ├─ Rutas protegidas: todo lo demás
  │  ├─ Filtro JWT: JwtAuthenticationFilter inyectado
  │  ├─ CORS: permite origenes específicos
  │  └─ CSRF: deshabilitado (stateless API)
  │
  └─ @Bean AuthenticationManager (inyección para login)

Problemas resueltos ✅:
  - ❌ Dependencias circulares: SecurityConfig → JwtAuthenticationFilter
  - ✅ Inyección de filtro en SecurityFilterChain (no en constructor)
  - ✅ Filter chain acceso a beans necesarios sin ciclos
```

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

### persistence.mapper ✅ CONSOLIDADO (Refactor Final 7 de abril 2026)

**Cambio Arquitectónico:** Se consolidó toda la funcionalidad de mappers en la capa `persistence`, eliminando la redundancia de `controller.mapper`.

**Razón del cambio:**
- ❌ ANTES: Dos niveles de mappers (controller/mapper y persistence/mapper) duplicaban lógica
- ✅ AHORA: Un único mapper por entidad que maneja 4 capas de transformación

**Responsabilidad:** Traducción en ambas direcciones entre DTO ↔ Model ↔ Entity JPA

**Patrón de 4 capas aplicado a cada mapper:**

1. **PERSISTENCE LAYER** (toDomain/toEntity Entity ↔ Model)
   - Convierte entidades JPA (BookEntity, UserEntity, LoanEntity) a modelos de dominio
   - Maneja mappings de enums (LoanStatus, UserRole)
   - Incluye timestamps (createdAt, updatedAt)

2. **API LAYER** (toDTO/toDTOList Model → DTO)
   - Convierte modelos de dominio a DTOs para respuestas HTTP
   - Incluye logging detallado (DEBUG, TRACE, WARN)
   - Manejo de nulls seguro

3. **REQUEST LAYER** (toEntity CreateDTO → Model)
   - Convierte DTOs de entrada a modelos de dominio
   - Aplica lógica de inicialización (ej: available = copies en Book)
   - Validación de campos opcionales (roles por defecto, etc)

4. **UPDATE OPERATIONS** (updateInventory, updateEntity)
   - Métodos especiales para actualizaciones parciales
   - Conjectura de cambios e historiales

**Clases implementadas:**

#### **BookPersistenceMapper** (~230 líneas)
```
Métodos:
  ┌─ PERSISTENCE (Entity ↔ Model)
  ├─ toDomain(BookEntity): Book
  └─ toEntity(Book): BookEntity
  
  ┌─ API RESPONSE (Model → DTO)
  ├─ toDTO(Book): BookDTO
  └─ toDTOList(List<Book>): List<BookDTO>
  
  ┌─ REQUEST (DTO → Model)
  └─ toEntity(CreateBookDTO): Book
  
  ┌─ INVENTORY OPERATIONS
  └─ updateInventory(Book, UpdateBookInventoryDTO): void
       ├─ SET: establece cantidad absoluta
       ├─ ADD: incrementa
       └─ REMOVE: decrementa (con validación negativa)
```

**Características especiales:**
- Conversión de tipo: `available` (Integer en model) → `available > 0` (booleano en DTO)
- Validación: impide decrementos por debajo de 0
- Logging con niveles (DEBUG para conversiones, INFO para operaciones, WARN para anomalías)

#### **UserPersistenceMapper** (~170 líneas)
```
Métodos:
  ┌─ PERSISTENCE (Entity ↔ Model)
  ├─ toDomain(UserEntity): User
  │  └─ Convierte UserRole.ADMIN → "ADMIN" (String en model)
  └─ toEntity(User): UserEntity
     └─ Convierte "ADMIN" → UserRole.ADMIN (enum)
  
  ┌─ API RESPONSE (Model → DTO)
  ├─ toDTO(User): UserDTO
  │  └─ NO expone username, password, role (seguridad)
  └─ toDTOList(List<User>): List<UserDTO>
  
  ┌─ REQUEST (DTO → Model)
  └─ toEntity(CreateUserDTO): User
     └─ Role por defecto: "USUARIO" si no se proporciona
  
  ┌─ PARTIAL UPDATES
  └─ updateEntity(User, UpdateUserDTO): void
     └─ Solo actualiza name y email (credenciales protegidas)
```

**Características especiales:**
- Seguridad: DTOs de respuesta nunca exponen credenciales
- Flexibilidad: CreateDTO incluye todas las credenciales, pero toDTO() las oculta
- Enum handling: manejo bidirecicional de UserRole

#### **LoanPersistenceMapper** (~190 líneas)
```
Métodos:
  ┌─ PERSISTENCE (Entity ↔ Model)
  ├─ toDomain(LoanEntity): Loan
  │  └─ Convierte entidades anidadas (book, user)
  │  └─ Convierte LoanStatus enum
  └─ toEntity(Loan): LoanEntity
     └─ Mapea relaciones @ManyToOne (user, book)
  
  ┌─ API RESPONSE COMPLETO (Model → DTO)
  ├─ toDTO(Loan): LoanDTO
  │  ├─ Incluye BookDTO completo (con título, autor, etc)
  │  ├─ Incluye UserDTO completo (sin credenciales)
  │  └─ Manejo de LazyInitializationException para relaciones
  └─ toDTOList(List<Loan>): List<LoanDTO>
  
  ┌─ API RESPONSE RESUMIDO (Model → SummaryDTO)
  ├─ toSummaryDTO(Loan): LoanSummaryDTO
  │  ├─ Solo IDs y títulos (sin objetos anidados)
  │  └─ Conversión LocalDateTime → LocalDate ✅ CORREGIDO
  └─ toSummaryDTOList(List<Loan>): List<LoanSummaryDTO>
```

**Características especiales:**
- Relaciones anidadas: depende de BookPersistenceMapper y UserPersistenceMapper
- Dos versiones de DTOs: completo vs resumido
- Error handling: LazyInitializationException → RuntimeException descriptiva
- **CORRECCIÓN APLICADA (7 abril):** `loan.getLoanDate().toLocalDate()` en SummaryDTO

---

### controller ✅ ACTUALIZADO

**Cambios realizados:**
- Todos los controllers actualizados para usar mappers consolidados de `persistence.mapper`
- ❌ ELIMINADA carpeta controller/mapper (redundante)

**Actualización de imports:**

```java
// ❌ ANTES
import edu.eci.dosw.DOSW_Library.controller.mapper.BookMapper;
private final BookMapper bookMapper;
public BookController(BookService bookService, BookMapper bookMapper) { ... }

// ✅ AHORA
import edu.eci.dosw.DOSW_Library.persistence.mapper.BookPersistenceMapper;
private final BookPersistenceMapper bookMapper;
public BookController(BookService bookService, BookPersistenceMapper bookMapper) { ... }
```

**Clases actualizadas:**
- BookController: `BookMapper` → `BookPersistenceMapper` ✅
- UserController: `UserMapper` → `UserPersistenceMapper` ✅
- LoanController: `LoanMapper` → `LoanPersistenceMapper` ✅

---

### core.validator ✅ CORREGIDO (7 de abril)

**Cambios realizados:** Se corrigieron inconsistencias de tipos en validadores

#### **BookValidator**
**CORRECCIONES:**
- ❌ `book.isAvailable()` no existe (método no encontrado)
- ✅ Cambiado a `book.getAvailable()` (retorna Integer, no boolean)
- ❌ Lógica comparaba booleano con Integer
- ✅ Corregida: `available` debe ser == copies (cuando available > 0, hay copias)

```java
// ❌ ANTES
boolean isAvailable = book.isAvailable();  // No existe este método
if (shouldBeAvailable != isAvailable) { ... }

// ✅ DESPUÉS
int isAvailable = book.getAvailable();  // Integer
if (shouldBeAvailable != isAvailable) { ... }
```

#### **LoanValidator**
**CORRECCIONES:**
- ❌ `loan.getLoanDate()` retorna `LocalDateTime` pero se trataba como `LocalDate`
- ✅ Métodos `isLoanOverdue()` y `getDaysRemaining()` actualizados a `LocalDateTime`

```java
// ❌ ANTES
LocalDate dueDate = loan.getLoanDate().plusDays(MAX_LOAN_DAYS);  // Error de tipo
boolean overdue = LocalDate.now().isAfter(dueDate);

// ✅ DESPUÉS
LocalDateTime dueDate = loan.getLoanDate().plusDays(MAX_LOAN_DAYS);  // Correcto
boolean overdue = LocalDateTime.now().isAfter(dueDate);
```

**Clases validadores:**
- ✅ ValidationUtil: SIN CAMBIOS (ya correcto)
- ✅ UserValidator: SIN CAMBIOS (ya correcto)
- ✅ BookValidator: CORREGIDO (tipos y lógica)
- ✅ LoanValidator: CORREGIDO (LocalDate → LocalDateTime)

---

### persistence.mapper ✅ CONSOLIDADO

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

### core.exception ✅ IMPLEMENTADO

**Rol:** Manejo centralizado de errores y excepciones personalizadas

**Clases implementadas:**

#### **ResourceNotFoundException**
- Extiende `RuntimeException`
- Lanzada cuando: No se encuentra recurso (Book, User, Loan)
- Uso típico: `getBookById()`, `getUserById()`, `getLoanById()`
- Código HTTP: 404 Not Found

```java
throw new ResourceNotFoundException("Book not found with id: " + id);
```

#### **ConflictException**
- Extiende `RuntimeException`
- Lanzada cuando: Conflicto de datos (DNI duplicado, ISBN duplicado, operación inválida)
- Uso típico: Validación en CreateDTO, actualización de inventario inválida
- Código HTTP: 409 Conflict

```java
throw new ConflictException("User with DNI " + dni + " already exists");
```

#### **ValidationException**
- Extiende `RuntimeException`
- Lanzada cuando: Validación fallida (campo inválido, regla de negocio violada)
- Uso típico: Validadores en `core.validator`
- Código HTTP: 400 Bad Request

```java
throw new ValidationException("Book title cannot be empty");
```

#### **GlobalExceptionHandler** (@RestControllerAdvice)
- Manejo centralizado de excepciones
- Mapeo automático →  ResponseEntity con código HTTP correcto
- Logging de errores
- Respuesta estándar: `{ "error": "...", "timestamp": "...", "status": 404 }`

---

### core.model 📊 MODELOS DE DOMINIO

**Rol:** Representación de entidades de negocio (SON entidades JPA)

**Clases implementadas:**

#### **Book** (@Entity)
```
Atributos:
  ├─ id: Long (única identificación)
  ├─ title: String (título libro)
  ├─ author: String (autor)
  ├─ isbn: String (ISBN único)
  ├─ description: String (descripción)
  ├─ publicationDate: LocalDate (fecha publicación)
  ├─ copies: Integer (total copias biblioteca)
  ├─ available: Integer (copias disponibles > 0)
  ├─ createdAt: LocalDateTime (timestamp creación)
  └─ updatedAt: LocalDateTime (última actualización)

Métodos:
  ├─ Getters/Setters
  ├─ equals()/hashCode() - Por ID único
  └─ toString() - Representación legible
```

**Validaciones de dominio:**
- ISBN: formato único en base de datos
- title: obligatorio, no vacío
- author: obligatorio, no vacío
- copies: debe ser > 0
- available: siempre ≤ copies

#### **User** (@Entity)
```
Atributos:
  ├─ id: Long (única identificación)
  ├─ name: String (nombre usuario)
  ├─ email: String (email único)
  ├─ username: String (username único para login)
  ├─ password: String (hash bcrypt)
  ├─ dni: String (DNI único)
  ├─ role: String ("ADMIN" o "USUARIO")
  ├─ createdAt: LocalDateTime (timestamp creación)
  └─ updatedAt: LocalDateTime (última actualización)

Métodos:
  ├─ Getters/Setters
  ├─ equals()/hashCode() - Por ID único
  ├─ hasRole(String): boolean
  └─ toString() - Representación legible (sin password)
```

**Validaciones de dominio:**
- email: formato válido, único
- username: único, 3-20 caracteres
- password: hash bcrypt (no plaintext)
- dni: único
- role: "ADMIN" ó "USUARIO"

#### **Loan** (@Entity)
```
Atributos:
  ├─ id: Long (única identificación)
  ├─ user: User (usuario que toma préstamo)
  ├─ book: Book (libro prestado)
  ├─ loanDate: LocalDateTime (fecha inicio préstamo)
  ├─ dueDate: LocalDateTime (fecha vencimiento)
  ├─ returnDate: LocalDateTime (fecha devolución real, null si activo)
  ├─ status: LoanStatus (ACTIVE / RETURNED / OVERDUE)
  ├─ fine: BigDecimal (multa si aplica)
  ├─ createdAt: LocalDateTime (timestamp creación)
  └─ updatedAt: LocalDateTime (última actualización)

Métodos:
  ├─ Getters/Setters
  ├─ equals()/hashCode() - Por ID único
  ├─ isOverdue(): boolean
  ├─ getDaysRemaining(): long
  ├─ markAsReturned(LocalDateTime): void
  ├─ calculateFine(): BigDecimal
  └─ toString() - Representación legible
```

**Validaciones de dominio:**
- loanDate: no puede ser futura
- returnDate: solo si status = RETURNED
- status: dependiente de fechas (OVERDUE si dueDate < now)
- fine: solo si OVERDUE/RETURNED con atraso

---

### core.repository ✅ IMPLEMENTADO

**Rol:** Acceso a datos persistentes (JPA/Hibernate)

**Interfaces implementadas:**

#### **BookRepository** (extends JpaRepository<Book, Long>)
```
Métodos custom:
  ├─ findByIsbn(String): Optional<Book> - Por ISBN único
  ├─ findByTitleIgnoreCase(String): List<Book> - Búsqueda por título
  ├─ findByAuthorIgnoreCase(String): List<Book> - Búsqueda por autor
  ├─ findAvailableBooks(): List<Book> - Solo con copias > 0
  └─ updateInventory(id, operation, quantity): void - Actualizar disponibilidad
```

#### **UserRepository** (extends JpaRepository<User, Long>)
```
Métodos custom:
  ├─ findByEmail(String): Optional<User> - Por email único
  ├─ findByUsername(String): Optional<User> - Por username único
  ├─ findByDni(String): Optional<User> - Por DNI único
  ├─ findByRole(String): List<User> - Por rol
  └─ existsByEmailOrUsernameOrDni(email, username, dni): boolean - Validación duplicados
```

#### **LoanRepository** (extends JpaRepository<Loan, Long>)
```
Métodos custom:
  ├─ findByUserId(Long): List<Loan> - Préstamos por usuario
  ├─ findByBookId(Long): List<Loan> - Préstamos de libro
  ├─ findActiveByUserId(userId): List<Loan> - Status ACTIVE
  ├─ findOverdueLoans(): List<Loan> - Vencidos
  └─ findByStatus(LoanStatus): List<Loan> - Por estado
```

---

### core.service ✅ IMPLEMENTADO

**Rol:** Lógica de negocio principal

**Clases implementadas:**

#### **BookService** (~280 líneas)
```
Métodos públicos:
  ├─ getAllBooks(): List<Book>
  ├─ getBookById(Long): Book
  ├─ createBook(CreateBookDTO): Book
  ├─ updateBook(Long, UpdateBookDTO): Book
  ├─ deleteBook(Long): void
  ├─ searchBooks(String): List<Book>
  └─ updateInventory(Long, UpdateBookInventoryDTO): void
     ├─ SET: fija cantidad exacta
     ├─ ADD: incrementa
     └─ REMOVE: decrementa
```

**Lógica de negocio:**
- ✅ ISBN único validado
- ✅ available nunca > copies
- ✅ No se permite REMOVE que deje cantidad negativa
- ✅ Logging detallado (DEBUG en búsquedas, INFO en cambios)

#### **UserService** (~240 líneas)
```
Métodos públicos:
  ├─ getAllUsers(): List<User>
  ├─ getUserById(Long): User
  ├─ getUserByEmail(String): User
  ├─ getUserByUsername(String): User
  ├─ getUserByDni(String): User
  ├─ createUser(CreateUserDTO): User
  ├─ updateUser(Long, UpdateUserDTO): User
  ├─ deleteUser(Long): void
  └─ findByRole(String): List<User>
```

**Lógica de negocio:**
- ✅ Email, username, DNI únicos
- ✅ Password hash con bcrypt (nunca en plaintext)
- ✅ Rol por defecto "USUARIO" si no se especifica
- ✅ DTOs de respuesta nunca exponen credenciales

#### **LoanService** (~320 líneas)
```
Métodos públicos:
  ├─ getAllLoans(): List<Loan>
  ├─ getLoanById(Long): Loan
  ├─ getLoansByUserId(Long): List<Loan>
  ├─ getLoansByBookId(Long): List<Loan>
  ├─ getActiveLoans(userId): List<Loan>
  ├─ getOverdueLoans(): List<Loan>
  ├─ createLoan(CreateLoanDTO): Loan
  │  └─ Validar: usuario existe, libro existe, copias disponibles
  ├─ returnLoan(Long, LocalDateTime): Loan
  │  └─ Calcular multa si está vencido
  └─ markAsOverdue(Long): Loan
     └─ Status = OVERDUE, calcular fine
```

**Lógica de negocio:**
- ✅ Validación: usuario y libro existen
- ✅ Validación: libro tiene copias disponibles
- ✅ Auto-actualización de status (OVERDUE si vencido)
- ✅ Cálculo de multas: $1 por día vencido
- ✅ Decremento de available al crear loan
- ✅ Incremento de available al devolver

---

### core.util ✅ IMPLEMENTADO

**Rol:** Utilidades transversales

**Clases implementadas:**

#### **DateUtil**
```
Métodos:
  ├─ now(): LocalDateTime - Timestamp actual
  ├─ isExpired(LocalDateTime): boolean - Comparación con now
  ├─ addDays(LocalDateTime, long): LocalDateTime
  ├─ getDaysBetween(LocalDateTime, LocalDateTime): long
  └─ toLocalDate(LocalDateTime): LocalDate
```

#### **ValidationUtil** (~100 líneas)
```
Métodos validación:
  ├─ isValidEmail(String): boolean - Regex
  ├─ isValidISBN(String): boolean - Formato numérico
  ├─ isValidDNI(String): boolean - No vacío
  ├─ isNotEmpty(String): boolean
  ├─ isPositive(Integer): boolean
  └─ throwIfInvalid(condition, message): void
```

### core.validator ✅ CORREGIDO (7 de abril)

**Rol:** Validaciones de dominio complejas

**Clases implementadas:**

#### **BookValidator** (~120 líneas) ✅ CORREGIDO
```
Métodos:
  ├─ validateCreateDto(CreateBookDTO): void
  │  └─ title, author, isbn, copies > 0 obligatorios
  ├─ validateUpdateDto(UpdateBookDTO, existingBook): void
  │  └─ Solo validar campos que se actualizan
  ├─ validateBook(Book): void
  │  └─ ISBN único, available ≤ copies
  └─ validateInventoryOperation(operation, quantity, available): void
     └─ REMOVE no deja negativo
```

**Correcciones aplicadas (7 abril):**
- `book.getAvailable()` retorna Integer (no boolean)
- Validación: `available > 0` indica que hay copias

#### **UserValidator** (~100 líneas)
```
Métodos:
  ├─ validateCreateDto(CreateUserDTO): void
  ├─ validateUpdateDto(UpdateUserDTO): void
  ├─ validateUser(User): void
  └─ validateRoleValue(String): boolean
```

#### **LoanValidator** (~130 líneas) ✅ CORREGIDO
```
Métodos:
  ├─ validateCreateDto(CreateLoanDTO): void
  ├─ validateLoan(Loan): void
  ├─ isLoanOverdue(): boolean - Compara LocalDateTime
  ├─ getDaysRemaining(): long - Cálculo desde dueDate
  └─ validateReturnOperation(Loan, returnDate): void
```

**Correcciones aplicadas (7 abril):**
- `loanDate` es LocalDateTime (no LocalDate)
- `dueDate` es LocalDateTime calculado desde loanDate

---

### persistence.entity ✅ CONSOLIDADO EN core.model

**Cambio arquitectónico:** Las clases Entity (@Entity) fueron consolidadas en `core.model`. No existe carpeta `persistence.entity` redundante.

**Razón:**
- ❌ ANTES: Duplicación Book (domain model) y BookEntity (JPA entity)
- ✅ AHORA: Una sola clase Book (@Entity) que es dominio + persistencia

**Clases en core.model (SON @Entity):**

#### **Book** (@Entity)
```java
@Entity
@Table(name = "books", uniqueConstraints = { @UniqueConstraint(columnNames = "isbn") })
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "publication_date")
    private LocalDate publicationDate;
    
    @Column(nullable = false)
    private Integer copies;  // Total de copias (>= 0)
    
    @Column(nullable = false)
    private Integer available;  // Copias disponibles (>= 0, <= copies)
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();
    
    // Getters, Setters, equals(), hashCode()
}
```

**Restricciones:**
- isbn: UNIQUE en BD
- copies, available: INTEGER NOT NULL
- title, author: VARCHAR NOT NULL
- available ≤ copies (validación en servicio)

#### **User** (@Entity)
```java
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "dni")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;  // BCrypt hash
    
    @Column(nullable = false, unique = true, length = 15)
    private String dni;
    
    @Column(nullable = false, length = 20)
    private String role;  // "ADMIN" o "USUARIO"
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();
    
    // Getters, Setters, equals(), hashCode()
}
```

**Restricciones:**
- email, username, dni: UNIQUE en BD
- password: VARCHAR NOT NULL (siempre hash BCrypt, nunca plaintext)
- role: VARCHAR(20) NOT NULL, CHECK role IN ('ADMIN', 'USUARIO')

#### **Loan** (@Entity)
```java
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "loan_date", nullable = false)
    private LocalDateTime loanDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;
    
    @Column(name = "return_date", nullable = true)
    private LocalDateTime returnDate;  // NULL si aún no devuelto
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;  // ACTIVE, RETURNED, OVERDUE
    
    @Column(nullable = false)
    private BigDecimal fine = BigDecimal.ZERO;  // Multa si aplica
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Getters, Setters, equals(), hashCode()
}

public enum LoanStatus {
    ACTIVE,      // Préstamo vigente
    RETURNED,    // Devuelto a tiempo
    OVERDUE      // Vencido, no devuelto
}
```

**Restricciones:**
- user_id, book_id: FOREIGN KEY
- loanDate: TIMESTAMP NOT NULL, debe ser ≤ ahora
- returnDate: TIMESTAMP NULL (solo si RETURNED/OVERDUE)
- status: VARCHAR(20) CHECK status IN ('ACTIVE', 'RETURNED', 'OVERDUE')

---

### persistence.repository ✅ IMPLEMENTADO

**Rol:** Interfaz JpaRepository con queries custom

**Responsabilidad:**
- CRUD automatizado (save, findById, findAll, delete)
- Query methods automáticas (findBy*, findByIdIn, etc)
- @Query para JPQL/SQL personalizadas
- Paginación y ordenamiento

#### **BookRepository** (extends JpaRepository<Book, Long>)
```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Query methods automáticas
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitleIgnoreCase(String title);
    List<Book> findByAuthorIgnoreCase(String author);
    
    // Custom query: libros con disponibilidad
    @Query("SELECT b FROM Book b WHERE b.available > 0 ORDER BY b.available DESC")
    List<Book> findAvailableBooks();
    
    // Búsqueda combinada
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(String keyword);
    
    // Paginación
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
```

#### **UserRepository** (extends JpaRepository<User, Long>)
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByDni(String dni);
    List<User> findByRole(String role);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
           "FROM User u WHERE u.email = :email OR u.username = :username OR u.dni = :dni")
    boolean existsByEmailOrUsernameOrDni(String email, String username, String dni);
}
```

#### **LoanRepository** (extends JpaRepository<Loan, Long>)
```java
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    List<Loan> findByUserId(Long userId);
    List<Loan> findByBookId(Long bookId);
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansByUserId(Long userId);
    
    @Query("SELECT l FROM Loan l WHERE l.status = 'OVERDUE'")
    List<Loan> findOverdueLoans();
    
    List<Loan> findByStatus(LoanStatus status);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status = 'ACTIVE'")
    long countActiveLoansForUser(Long userId);
}
```

**Todas las operaciones usan JpaRepository + query methods automáticas. No hay DAO separada.**

### Flujo de datos: persistence layer

**Lectura de datos (HTTP GET) ✅ SIMPLIFICADO:**
```
[Client HTTP]
  ↓ GET /api/books/1
[BookController.getBookById(1)]
  ↓
[BookService.getBookById(1)]
  ↓
[BookRepository.findById(1)]  ← JpaRepository query method
  ↓
[H2 Database SELECT * FROM books WHERE id=1]
  ↓
[Book @Entity (dominio + JPA)]  ← core.model.Book = Entity + Model
  ↓
[BookPersistenceMapper.toDTO(book)]  ← Convierte a BookDTO
  ↓
[BookDTO JSON]
  ↓ HTTP 200 OK
[Client HTTP]
```

**Escritura de datos (HTTP POST) ✅ SIMPLIFICADO:**
```
[Client HTTP]
  ↓ POST /api/books (CreateBookDTO JSON)
[BookController.createBook(@Valid CreateBookDTO)]
  ↓ @Valid dispara validaciones Jakarta
[BookPersistenceMapper.toEntity(createDTO)]  ← DTO → Book model
  ↓
[Book @Entity validado]
  ↓
[BookService.createBook(book)]  ← Aplica reglas de negocio
  ├─ Verificar ISBN único ✅
  ├─ Validar copies > 0 ✅
  ├─ Guardar timestamps ✅
  └─ SET id = null (auto-generado)
  ↓
[BookRepository.save(book)]  ← @Entity directo, sin conversión Entity
  ↓
[H2 Database INSERT INTO books (...) VALUES (...)]
  ↓ COMMIT transacción
[Book confirmado desde BD]
  ↓
[BookPersistenceMapper.toDTO(book)]  ← Convierte a BookDTO
  ↓
[BookDTO JSON response]
  ↓ HTTP 201 Created
[Client HTTP]
```

**Ventaja de arquitectura consolidada:**
- ❌ ANTES: DTOCreateBookDTO → Book model → BookEntity → BD (3 clases)
- ✅ AHORA: CreateBookDTO → Book @Entity (@Entity + model en uno) → BD (2 clases)
- **NO hay conversión redundante entity ↔ model**

### Configuracion JPA/Hibernate

**En application.properties:**
```properties
# H2 database (en memoria)
spring.datasource.url=jdbc:h2:mem:dosw_db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop  # En desarrollo: auto-genera schema
spring.jpa.show-sql=true  # Logs de SQL (DEBUG)
spring.jpa.properties.hibernate.format_sql=true  # SQL formateado

# H2 Console (acceso web a la BD)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Acceso a la consola H2:**
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:dosw_db`
- Usuario: `sa`
- Contraseña: (vacía)

### Ciclo de vida de una Entity

```
1. TRANSIENT (nuevo objeto, sin @Id desde BD):
   BookEntity book = new BookEntity();  // En memoria, no vinculada

2. PERSISTENT (vinculada a sesion JPA, cambios trackeados):
   repository.save(book);
   // JPA ahora monitorea cambios a book

3. DETACHED (objeto fue guardado pero sesion cerro):
   // Si cierras EntityManager y mantienes referencia a book
   // Los cambios a book YA NO se sincronizan con BD

4. REMOVED (marcada para eliminacion):
   repository.delete(book);
   // En el flush, DELETE se ejecuta
```

### security ✅ IMPLEMENTADO

**Rol:** Autenticación y autorización basada en JWT

**Clases implementadas:**

#### **JwtService** (@Component)
```
Responsabilidad: Generación y validación de tokens JWT

Métodos:
  ├─ generateToken(username: String): String
  │  ├─ Crea JWT con claims (username, issuedAt, expiration)
  │  ├─ Firma con HMAC-SHA256 (secret key)
  │  └─ Expiration: 24 horas por defecto
  │
  ├─ extractUsername(token: String): String
  │  └─ Extrae claim "sub" del token
  │
  ├─ validateToken(token: String): boolean
  │  ├─ Verifica firma (secret key)
  │  ├─ Verifica no expirado (exp < now)
  │  └─ Maneja ParseException si token inválido
  │
  └─ isTokenExpired(token: String): boolean
     └─ Compara exp claim con LocalDateTime.now()

Seguridad:
  - ✅ Secret key almacenada en properties (no hardcoded)
  - ✅ Algoritmo: HMAC-SHA256 (HS256)
  - ✅ Claims: username, issuedAt, expiration
```

#### **JwtAuthenticationFilter** (extends OncePerRequestFilter)
```
Responsabilidad: Interceptar requests e inyectar autenticación

Método de ejecución:
  ├─ doFilterInternal() por cada request
  │  ├─ Lee header "Authorization: Bearer <token>"
  │  ├─ Extrae token (substring después de "Bearer ")
  │  ├─ JwtService.validateToken(token)
  │  ├─ Si válido:
  │  │  ├─ JwtService.extractUsername(token)
  │  │  ├─ UserRepository.findByUsername(username)
  │  │  ├─ Crea Authentication (UsernamePasswordAuthenticationToken)
  │  │  └─ SecurityContext.setAuthentication()
  │  │
  │  └─ Si inválido o no existe token:
  │     └─ SecurityContext queda vacío
  │
  └─ filterChain.doFilter() → siguiente filtro

Ciclo de vida:
  - ✅ Se ejecuta ANTES de @RestController
  - ✅ Modifica SecurityContext para autorización posterior
  - ✅ No bloquea request (FilterChain decide)
```

**Flujo de autenticación JWT:**

```
1. Cliente en login (POST /api/auth/login)
   └─ Envía username + password (plaintext)

2. AuthController.login() verifica credenciales
   ├─ UserRepository.findByUsername(username)
   ├─ PasswordEncoder.matches(password, hashedPassword)
   └─ Si OK → JwtService.generateToken(username)

3. Cliente recibe token JWT
   └─ Guarda en localStorage o memory

4. Cliente en requests públicos
   └─ POkus sin bearer token (permitido)

5. Cliente en requests protegidos
   ├─ Envía Authorization: Bearer <token>
   ├─ JwtAuthenticationFilter intercepta
   ├─ Valida token
   ├─ SecurityContext tiene Usuario autenticado
   └─ @PreAuthorize/@RolesAllowed lo permiten/bloquean

Problemas resueltos ✅:
  - ❌ Filtro inyectado en constructor SecurityConfig → ciclo circular
  - ✅ Filtro inyectado en SecurityFilterChain bean
  - ❌ Validación manual de roles en controllers
  - ✅ @PreAuthorize("hasRole('ADMIN')") centralizado
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

### Flujo 1: registrar usuario ✅

1. Cliente envia POST /api/users con CreateUserDTO (JSON).
2. UserController.createUser(@Valid CreateUserDTO) valida con Jakarta.
3. UserPersistenceMapper.toEntity(createDTO) → User @Entity.
4. UserService.createUser(user) valida:
   - Email único ✅
   - Username único ✅
   - DNI único ✅
   - Password hasheado con BCrypt ✅
   - Rol por defecto "USUARIO" si no se especifica ✅
5. UserRepository.save(user) → INSERT en H2.
6. UserPersistenceMapper.toDTO(user) → UserDTO (sin credenciales).
7. HTTP 201 CREATED + UserDTO JSON.

### Flujo 2: crear libro ✅

1. Cliente envia POST /api/books con CreateBookDTO (JSON).
2. BookController.createBook(@Valid CreateBookDTO) valida con Jakarta.
3. BookPersistenceMapper.toEntity(createDTO) → Book @Entity.
4. BookService.createBook(book) valida:
   - ISBN único ✅
   - copies > 0 ✅
   - Sin duplicados en BD ✅
5. BookRepository.save(book) → INSERT en H2.
6. BookPersistenceMapper.toDTO(book) → BookDTO.
7. HTTP 201 CREATED + BookDTO JSON.

### Flujo 3: crear préstamo ✅

1. Cliente envia POST /api/loans con CreateLoanDTO (user_id, book_id).
2. LoanController.createLoan(@Valid CreateLoanDTO) valida con Jakarta.
3. LoanPersistenceMapper.toEntity(createDTO) → Loan @Entity.
4. LoanService.createLoan(loan) ejecuta validaciones secuenciales:
   - Usuario existe ✅ (UserRepository.findById)
   - Libro existe ✅ (BookRepository.findById)
   - Libro tiene copias disponibles ✅ (book.getAvailable() > 0)
   - Decrementa available del libro ✅
   - Define loanDate = NOW y dueDate = NOW + 14 días ✅
   - Status = ACTIVE ✅
5. LoanRepository.save(loan) → INSERT en H2.
6. BookRepository.save(book) → UPDATE libro (available--).
7. LoanPersistenceMapper.toDTO(loan) → LoanDTO (con Book + User anidados).
8. HTTP 201 CREATED + LoanDTO JSON.

### Flujo 4: devolver libro ✅

1. Cliente invoca PUT /api/loans/{id}/return.
2. LoanController.returnLoan(id, returnDate) valida con @Valid.
3. LoanService.returnLoan(id, returnDate) ejecuta:
   - Verifica loan existe ✅ (LoanRepository.findById)
   - Verifica status = ACTIVE ✅
   - Valida returnDate no es futura ✅
   - Incrementa available del libro ✅ (book.getAvailable()++)
   - Calcula multa si está vencido ✅ (fine = MAX(0, días_vencidos * $1))
   - Cambia status = RETURNED si a tiempo, OVERDUE si vencido ✅
   - Setea returnDate = ahora ✅
4. LoanRepository.save(loan) → UPDATE en H2.
5. BookRepository.save(book) → UPDATE libro (available++).
6. LoanPersistenceMapper.toDTO(loan) → LoanDTO (con fine calculada).
7. HTTP 200 OK + LoanDTO JSON.

### Flujo 5: seguridad JWT en cada request ✅

1. Cliente envia Authorization: Bearer <token> en header.
2. JwtAuthenticationFilter.doFilterInternal() intercepta el request.
3. JwtService.extractUsername(token) extrae username del token.
4. JwtService.validateToken(token) verifica:
   - Firma válida (con secret key) ✅
   - No expirado (exp claim < now) ✅
5. Si validación OK:
   - UserRepository.findByUsername(username) obtiene User ✅
   - SecurityContext.setAuthentication(UsernamePasswordAuthenticationToken) ✅
   - Permite continuar con el request ✅
6. Si validación FALLA:
   - SecurityContext queda vacío ✅
   - SecurityFilterChain lo bloquea (401 Unauthorized) ✅
7. Controller y service ejecutan con Authorization verificada.

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
7. persistence.mapper.LoanPersistenceMapper construye LoanDTO y LoanSummaryDTO.
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

⚠️ **NOTA IMPORTANTE (7 de abril 2026):** Esta sección contiene ejemplos educativos del flujo conceptual. El código real del proyecto usa:
- ✅ `LoanPersistenceMapper.toDTO()` en lugar de `loanMapper.toLoanDTO()`
- ✅ IDs como `Long` (auto-generados por DB) en lugar de strings
- ✅ `LoanRepository.save(loan)` (JpaRepository) en lugar de `loansMap.put()`
- ✅ Entidades `@Entity` consolidadas en `core.model` (no hay Entity separada)
- ✅ GlobalExceptionHandler + excepciones personalizadas (ResourceNotFoundException, ConflictException, ValidationException)

Los flujos y validaciones conceptuales son correctos; solo los detalles técnicos han sido optimizados.

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

### Mappers ✅ CONSOLIDADOS

- **BookPersistenceMapper:** Conversión Book ↔ DTO + operaciones de inventario
  - Parte clave: 4-tier mapping (Entity, DTO, CreateDTO, UpdateDTO) + updateInventory(SET/ADD/REMOVE)
  - Por qué existe: centralizar transformaciones DTO ↔ Model en persistence layer

- **UserPersistenceMapper:** Conversión User ↔ DTO + actualización parcial
  - Parte clave: updateEntity() modifica solo campos no nulos, DTOs no exponen password/credenciales
  - Por qué existe: serialización segura + lógica de merge de datos

- **LoanPersistenceMapper:** Conversión Loan → LoanDTO y LoanSummaryDTO
  - Parte clave: mapea relaciones anidadas (Book y User) + enum LoanStatus
  - Por qué existe: dos niveles de detalle: completo vs liviano para listados

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
- [Modelo Entidad-Relación (ER)](#termino-modelo-er)
- [Normalización 3FN](#termino-normalizacion-3fn)
- [Clave Primaria (PK)](#termino-clave-primaria)
- [Clave Foránea (FK)](#termino-clave-foranea)
- [Integridad Referencial](#termino-integridad-referencial)

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

### Termino: Modelo ER

Representación gráfica de las entidades, atributos y relaciones en una base de datos relacional. En DOSW-Library:
- Entidades: USER, BOOK, LOAN
- Relaciones: User ↔ Loan (1:N), Book ↔ Loan (1:N)
- Atributos con tipos, restricciones y claves

Ver: [Modelo Entidad-Relación Normalizado a 3FN](#modelo-entidad-relación-normalizado-a-3fn)

### Termino: Normalizacion 3FN

**3FN (Tercera Forma Normal)** es el nivel máximo de normalización de bases de datos relacionales que garantiza:

**1FN - Primera Forma Normal:** Todos los atributos son atómicos (indivisibles)

**2FN - Segunda Forma Normal:** Cumple 1FN + sin dependencias parciales

**3FN - Tercera Forma Normal:** Cumple 2FN + sin dependencias transitivas

En DOSW-Library, el modelo cumple estrictamente 3FN:
- ✅ No hay atributos multivaluados (1FN)
- ✅ Todos los datos no-clave dependen de la PK (2FN)
- ✅ No hay datos redundantes en tablas (3FN)

Beneficio: Evita anomalías en inserciones, actualizaciones y eliminaciones.

Ver: [Modelo Entidad-Relación Normalizado a 3FN](#modelo-entidad-relación-normalizado-a-3fn)

### Termino: Clave Primaria

**Primary Key (PK)** es un atributo o conjunto de atributos que identifica únicamente cada registro en una tabla.

En DOSW-Library:
- User.id (ej: "USR-001")
- Book.id (ej: "BK-001")
- Loan.id (ej: "LOAN-001")

Garantiza: No habrá dos registros con la misma clave primaria.

### Termino: Clave Foranea

**Foreign Key (FK)** es un atributo que referencia la clave primaria de otra tabla, creando relaciones entre tablas.

En DOSW-Library:
- Loan.user_id → User.id (relación 1:N)
- Loan.book_id → Book.id (relación 1:N)

Garantiza: Integridad referencial (no puede haber préstamo sin usuario/libro válido).

### Termino: Integridad Referencial

Propiedad que asegura que los valores en una clave foránea siempre referenciarn registros válidos de la tabla padre.

En DOSW-Library:
```sql
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
-- Si se elimina usuario → sus préstamos se eliminan automáticamente
```

Opciones:
- `ON DELETE CASCADE`: Elimina registros hijos
- `ON DELETE RESTRICT`: Impide eliminar si hay hijos
- `ON DELETE SET NULL`: Asigna NULL al FK en registros hijos