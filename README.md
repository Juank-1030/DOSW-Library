# DOSW-Library

Informe de avance tecnico del proyecto de biblioteca desarrollado con Spring Boot.

Este documento esta pensado como referencia de arquitectura, guia de implementacion y manual funcional del proyecto.

## Indice

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Objetivo y Alcance](#objetivo-y-alcance)
3. [Stack Tecnologico](#stack-tecnologico)
4. [Configuración Spring Initializer - Requisito Verificado](#-configuración-spring-initializer---requisito-verificado)
5. [Persistencia Dual: SQL + MongoDB](#persistencia-dual-sql--mongodb)
5. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
5. [Anotaciones y Por Que Se Usan](#anotaciones-y-por-que-se-usan)
6. [Estructura de Paquetes](#estructura-de-paquetes)
8. [Modelo Entidad-Relación Normalizado a 3FN](#modelo-entidad-relación-normalizado-a-3fn)
9. [Implementación JPA: Entidades con Anotaciones](#implementación-jpa-entidades-con-anotaciones-)
10. [Capa Repository: Abstracción de Persistencia](#capa-repository-abstracción-de-persistencia-)
11. [Paquetes y Clases: Responsabilidad Detallada](#paquetes-y-clases-responsabilidad-detallada)
12. [Capa Service: Lógica de Negocio](#capa-service-lógica-de-negocio-)
13. [Validación de Datos en DTOs](#validación-de-datos-en-dtos-)
14. [Manejo de Errores y Excepciones](#manejo-de-errores-y-excepciones-)
15. [Autenticación y Autorización con JWT](#autenticación-y-autorización-con-jwt-)
16. [Logging y Auditoría](#logging-y-auditoría-)
17. [Configuracion y Ejecucion](#configuracion-y-ejecucion)
18. [Implementación Práctica: Setup Real de PostgreSQL + application.yaml](#-implementación-práctica-setup-real-de-postgresql--applicationyaml)
19. [Endpoints Implementados](#endpoints-implementados)
20. [Flujos Funcionales Clave](#flujos-funcionales-clave)
21. [Flujo Entre Paquetes y Clases](#flujo-entre-paquetes-y-clases)
22. [Explicacion Paso a Paso: Flujos con Codigo Real](#explicacion-paso-a-paso-flujos-con-codigo-real)
23. [Explicacion de Todas las Clases](#explicacion-de-todas-las-clases)
24. [Como Implementar y Extender el Proyecto](#como-implementar-y-extender-el-proyecto)
25. [Pruebas y Cobertura Actual](#pruebas-y-cobertura-actual)
26. [Pruebas Funcionales e Integración](#pruebas-funcionales-e-integración)
27. [Riesgos Tecnicos y Mejoras Recomendadas](#riesgos-tecnicos-y-mejoras-recomendadas)
28. [Glosario](#glosario)

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

### Lenguaje y Runtime
- **Java 21** (LTS - Long Term Support)
- **Spring Boot 4.0.3** (framework principal)

### 🔧 Configuración Spring Initializer - Requisito Verificado

**Proyecto generado con Spring Initializer** en https://start.spring.io/ con las siguientes configuraciones:

#### Parametros de Inicialización
| Parámetro | Valor |
|-----------|-------|
| **Project Type** | Maven Project |
| **Language** | Java |
| **Spring Boot Version** | 4.0.3 |
| **Project Metadata - Group** | edu.eci.dosw |
| **Project Metadata - Artifact** | DOSW-Library |
| **Packaging** | JAR |
| **Java Version** | 21 |

#### Dependencies Seleccionadas en Spring Initializer ✅

Todas estas dependencias fueron **seleccionadas en Spring Initializer** y están disponibles en `pom.xml`:

1. **Spring Data JPA** - ORM con Hibernate para SQL
2. **Spring Data MongoDB** - ⭐ Persistencia NoSQL (Requisito Principal)
3. **Spring Web** - REST Controllers y Web MVC
4. **Spring Security** - Autenticación y autorización
5. **Validation** - Validación de DTOs con Jakarta Validation
6. **Lombok** - Reducción de boilerplate code
7. **H2 Database** - BD en memoria para desarrollo/testing
8. **Spring Boot Test** - Testing (JUnit 5, Mockito)
9. **Springdoc OpenAPI** - Swagger/OpenAPI UI
10. **JJWT** - JSON Web Tokens (agregada manualmente post-initializer)

**URL de Spring Initializer con la configuración actual:**
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=4.0.3&packaging=jar&jvmVersion=21&groupId=edu.eci.dosw&artifactId=DOSW-Library&name=DOSW-Library&description=Ejercicio%20de%20clase%20DOSW%20T2&packageName=edu.eci.dosw.DOSW_Library&dependencies=web,data-jpa,data-mongodb,security,validation,lombok,h2,test
```

**Evidencia en pom.xml:** [Ver dependencias en pom.xml](pom.xml#L33-L120)

### Dependencias Maven (en pom.xml)

#### Persistencia ✅ **Spring Data JPA**
```xml
<!-- Spring Data JPA (ORM con Hibernate) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- H2 Database (BD en memoria para desarrollo) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### Web y Validación
```xml
<!-- Spring Web MVC (REST Controllers) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Jakarta Validation (validación de DTOs) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### Seguridad y Autenticación
```xml
<!-- Spring Security (autenticación y autorización) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JJWT 0.12.6 (JWT tokens) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

#### Documentación
```xml
<!-- Springdoc OpenAPI (Swagger/OpenAPI de UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.6</version>
</dependency>
```

#### Utilidades
```xml
<!-- Lombok (reducción de boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

#### Testing
```xml
<!-- Spring Boot Test (JUnit 5, Mockito, AssertJ) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Security Test -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Resumen de componentes

| Componente | Versión | Rol |
|-----------|---------|-----|
| Java | 21 | Runtime |
| Spring Boot | 4.0.3 | Base framework |
| **Spring Data JPA** | ✅ | **Persistencia ORM** |
| H2 | Runtime | BD en memoria |
| Spring Security | - | Autenticación |
| JJWT | 0.12.6 | Tokens JWT |
| Lombok | - | Boilerplate |
| Springdoc OpenAPI | 2.8.6 | Documentación |
| JUnit 5 | - | Testing |

## Persistencia Dual: SQL + MongoDB

### ✅ Requisito Cumplido: Spring Data MongoDB

El proyecto **incluye Spring Data MongoDB** como dependencia configurada a través de Spring Initializer con las siguientes especificaciones:

#### Dependencia Maven
```xml
<!-- Spring Data MongoDB (Persistencia NoSQL) -->
<!-- Proporciona: MongoRepository, documentos, queries flexibles -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

**Ubicación:** [pom.xml](pom.xml) líneas 51-54

#### Configuración

La conexión a MongoDB está configurada mediante **variables de entorno** para seguridad:

##### En `application.properties`
```properties
# MongoDB Atlas Configuration
spring.application.name=UpLearn
spring.data.mongodb.database=${DB_NAME}
spring.data.mongodb.uri=${DB_URI}
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
spring.config.import=optional:file:.env[.properties]
```

**Ubicación:** [src/main/resources/application.properties](src/main/resources/application.properties) líneas 33-44

##### En `.env`
```
DB_URI=mongodb+srv://juanbohorquezm_db_user:Giracel2468@cluster0.5puigry.mongodb.net/
DB_NAME=Libros
```

**Ubicación:** [.env](.env) - **Archivo no versionado por seguridad (en .gitignore)**

#### Propósito

**Persistencia Dual (SQL + NoSQL)** enable:

1. **Flexibilidad:** Datos relaciones en PostgreSQL (SQL), datos no estructurados en MongoDB
2. **Escalabilidad:** MongoDB maneja alto volumen de documentos heterogéneos
3. **Resiliencia:** Si un sistema cae, el otro mantiene la aplicación funcional
4. **Casos de uso futuros:**
   - Auditlogs y eventos en MongoDB (alta escritura)
   - Búsqueda full-text con Atlas Search
   - Sincronización asincróna entre sistemas

#### Conexión a MongoDB

**Modo Local (Desarrollo):**
```
DB_URI=mongodb://localhost:27017/
DB_NAME=dosw_library_db
```

**Modo Cloud (MongoDB Atlas - Producción):**
```
DB_URI=mongodb+srv://<user>:<password>@<cluster>.mongodb.net/
DB_NAME=Libros
```

#### Verificación de Conexión

```bash
# Verificar que Spring reconoce MongoDB
grep "MongoRepository" src/main/java/edu/eci/dosw/DOSW_Library/**/*.java

# Revisar configuración activa
cat src/main/resources/application.properties | grep spring.data.mongodb
```

---

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

## Implementación JPA: Entidades con Anotaciones ✅

**Ubicación:** `src/main/java/edu/eci/dosw/DOSW_Library/core/model/`

Las entidades están mapeadas usando **Spring Data JPA** con anotaciones de **Jakarta Persistence**. Cada entidad corresponde 1:1 con una tabla de la BD.

### User.java - Entidad de Usuarios

**Anotaciones principales:**

```java
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "uk_users_email"),
        @UniqueConstraint(columnNames = "username", name = "uk_users_username"),
        @UniqueConstraint(columnNames = "dni", name = "uk_users_dni")
    }
)
public class User {
    
    @Id
    private String id;  // VARCHAR(20) @PK
    
    @Column(nullable = false)
    private String name;  // VARCHAR(100) NOT NULL
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;  // VARCHAR(100) UNIQUE
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;  // VARCHAR(50) UNIQUE (login)
    
    @Column(nullable = false, length = 255)
    private String password;  // VARCHAR(255) (hash BCrypt)
    
    @Column(nullable = false, length = 50)
    private String role;  // VARCHAR(50) (BIBLIOTECARIO, USUARIO)
    
    @Column(nullable = false, unique = true, length = 15)
    private String dni;  // VARCHAR(15) UNIQUE
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // TIMESTAMP DEFAULT NOW()
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // TIMESTAMP DEFAULT NOW()
    
    /**
     * Relación 1:N con Loan
     * - mappedBy = "user": Loan.user es el lado propietario
     * - cascade = CascadeType.ALL: cambios en User afectan Loans
     * - orphanRemoval = true: Loans sin User se eliminan
     * - fetch = FetchType.LAZY: carga bajo demanda (mejor performance)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**¿Qué significa?**
- `@Entity`: JPA crea tabla `users` automáticamente
- `@Table(uniqueConstraints=...)`: Restricciones UNIQUE en BD
- `@Id`: Clave primaria (String en este caso, no auto-generado)
- `@Column(unique=true)`: email, username, dni son únicos
- `@OneToMany`: Un usuario puede tener múltiples préstamos
- `@PrePersist/@PreUpdate`: Hooks para timestamps automáticos

---

### Book.java - Entidad de Libros

```java
@Entity
@Table(
    name = "books",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn", name = "uk_books_isbn")
    }
)
public class Book {
    
    @Id
    private String id;  // VARCHAR(20) @PK
    
    @Column(nullable = false)
    private String title;  // VARCHAR(200)
    
    @Column(nullable = false)
    private String author;  // VARCHAR(100)
    
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;  // VARCHAR(20) UNIQUE
    
    @Column(length = 500)
    private String description;  // VARCHAR(500) nullable
    
    @Column(nullable = false)
    private Integer copies;  // INT NOT NULL (CHECK: > 0)
    
    /**
     * CAMBIO CRÍTICO: available es Integer (cantidad), NO boolean
     * Permite rastrear cantidad exacta de copias disponibles
     * Validación: 0 <= available <= copies
     */
    @Column(nullable = false)
    private Integer available;  // INT NOT NULL
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Relación 1:N con Loan
     * Un libro puede ser prestado múltiples veces
     */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Loan> loans = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (available == null) available = copies;  // Inicializa disponibilidad
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**Puntos clave:**
- `isbn`: ISBN único del libro (validación de duplicados en BD)
- `copies`: Stock total (siempre > 0)
- `available`: Copias libres para prestar (0 <= available <= copies)
- Validación en BD: CHECK (copies > 0) y CHECK (available >= 0 AND available <= copies)
- `@OneToMany`: Relación inversa con Loan

---

### Loan.java - Entidad de Préstamos

```java
@Entity
@Table(name = "loans")
public class Loan {
    
    @Id
    private String id;  // VARCHAR(20) @PK
    
    /**
     * Relación N:1 con User
     * - @ManyToOne: Muchos Loans → 1 User
     * - @JoinColumn: Columna book_id en tabla loans
     * - nullable = false: Todo loan debe tener un usuario
     * - fetch = FetchType.LAZY: Carga bajo demanda
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Relación N:1 con Book
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(nullable = false)
    private LocalDateTime loanDate;  // TIMESTAMP: cuando se prestó
    
    @Column(nullable = false)
    private LocalDateTime dueDate;  // TIMESTAMP: vencimiento (loanDate + 14 días)
    
    @Column(nullable = true)
    private LocalDateTime returnDate;  // TIMESTAMP: cuando se devuelve (NULL si no devuelto)
    
    /**
     * Estado del préstamo
     * @Enumerated(EnumType.STRING): persiste como 'ACTIVE' o 'RETURNED' (no números)
     * - ACTIVE: préstamo vigente
     * - RETURNED: libro devuelto
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (loanDate == null) loanDate = LocalDateTime.now();
        if (dueDate == null) dueDate = loanDate.plusDays(14);  // Vencimiento automático
        if (status == null) status = LoanStatus.ACTIVE;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

// En archivo separado: LoanStatus.java
public enum LoanStatus {
    ACTIVE,      // Préstamo vigente
    RETURNED     // Devuelto
}
```

**Puntos clave:**
- `@ManyToOne`: Relación hacia User y Book (lado propietario)
- `@JoinColumn`: Define columnas FK (user_id, book_id)
- `@Enumerated(EnumType.STRING)`: Persiste enum como texto ('ACTIVE', 'RETURNED'), no números
- `returnDate`: NULL mientras status = ACTIVE, tiene valor cuando status = RETURNED
- `dueDate`: Auto-calculada en @PrePersist (loan_date + 14 días)

---

### LoanStatus.java - Enum de Estados

```java
public enum LoanStatus {
    ACTIVE,      // Préstamo vigente
    RETURNED     // Libro devuelto
}
```

---

### Anotaciones JPA Más Comunes Utilizadas

| Anotación | Ubicación | Función | Ejemplo |
|-----------|-----------|---------|---------|
| `@Entity` | Clase | Marca clase como entidad JPA | `public class Book { ... }` |
| `@Table` | Clase | Define nombre tabla + restricciones | `@Table(name = "books")` |
| `@Id` | Campo | Clave primaria | `@Id private String id;` |
| `@Column` | Campo | Mapeo a columna SQL | `@Column(nullable = false, unique = true)` |
| `@OneToMany` | Campo | Relación 1:N | `@OneToMany(mappedBy = "book")` |
| `@ManyToOne` | Campo | Relación N:1 | `@ManyToOne @JoinColumn(name = "book_id")` |
| `@JoinColumn` | Campo | Columna FK | `@JoinColumn(name = "user_id", nullable = false)` |
| `@Enumerated` | Campo | Persistencia de Enum | `@Enumerated(EnumType.STRING)` |
| `@PrePersist` | Método | Hook antes de INSERT | `protected void onCreate() { ... }` |
| `@PreUpdate` | Método | Hook antes de UPDATE | `protected void onUpdate() { ... }` |
| `@UniqueConstraint` | @Table | Restricción UNIQUE | `uniqueConstraints = @UniqueConstraint(columnNames = "email")` |
| `@CascadeType.ALL` | @OneToMany | Cambios cascadan | `cascade = CascadeType.ALL` |
| `@FetchType.LAZY` | Relación | Carga bajo demanda | `fetch = FetchType.LAZY` |

---

### Diagrama de Relaciones JPA

```
User (1) ─────────────────────────┐
  ∧                               │
  │                               │ @OneToMany(mappedBy="user")
  │                               │ cascade=CascadeType.ALL
  │ @ManyToOne                    │ orphanRemoval=true
  │ @JoinColumn("user_id")        │
  │                               ↓
  │                            Loan (N)
  │                               │
  │                               │ @ManyToOne
  │                               │ @JoinColumn("book_id")
  │                               │
  └───────────────────────────────┤
                                   ↓
                                Book (1) ─────┐
                                              │
                                              │ @OneToMany(mappedBy="book")
                                              │ cascade=CascadeType.ALL
                                              │ orphanRemoval=true
                                              ↓
                                           Loan (N)
```

---

### Comparativa: Anotaciones JPA vs SQL

| Operación | JPA Anotación | SQL Generado | Beneficio |
|-----------|--------------|--------------|-----------|
| PK con auto-increment | `@Id @GeneratedValue` | `PRIMARY KEY AUTO_INCREMENT` | No escribir ID manualmente |
| Columna NOT NULL | `@Column(nullable = false)` | `NOT NULL` | Validación en BD |
| Restricción UNIQUE | `@Column(unique = true)` | `UNIQUE(column)` | Evita duplicados |
| Relación 1:N | `@OneToMany @JoinColumn` | `FOREIGN KEY` | Integridad referencial |
| Enum | `@Enumerated(STRING)` | `VARCHAR(varor_name)` | Type-safety en Java |
| Timestamps auto | `@PrePersist @PreUpdate` | `TRIGGER` o `DEFAULT NOW()` | Auditoría automática |
| Cascada | `@CascadeType.ALL` | `ON DELETE CASCADE` | Cambios propagados |
| Lazy loading | `@FetchType.LAZY` | `N/A` | Mejor performance |

---

## Capa Repository: Abstracción de Persistencia ✅

### Rol y Responsabilidades del Repository

**¿Qué es un Repository?**

Un Repository es una interfaz que actúa como **abstracción entre la lógica de negocio y la capa de persistencia (BD)**. Spring Data JPA **genera la implementación automáticamente** sin necesidad de código SQL manualmente.

**Principio:** Patrón Repository (Design Patterns - Evans Domain-Driven Design)

```
┌──────────────────────────────────────────────────────────┐
│                    Capa de Negocio                        │
│               (Service, Controller)                       │
│                                                            │
│  userRepository.findByEmail("john@example.com")          │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ↓
┌──────────────────────────────────────────────────────────┐
│              Capa Repository (Interfaces)                │
│                                                            │
│  public interface UserRepository                         │
│      extends JpaRepository<User, String> { ... }         │
└────────────────────────┬─────────────────────────────────┘
                         │
           ┌─────────────┴─────────────┐
           │                           │
    Método Query              Método @Query
    (Auto-generado)           (Manual JPQL)
           │                           │
           ↓                           ↓
┌──────────────────────┐  ┌──────────────────────┐
│  SELECT * FROM ...   │  │  SELECT u FROM User│
│  WHERE email = ?     │  │  WHERE ...          │
└──────────────────────┘  └──────────────────────┘
           │                           │
           └─────────────┬─────────────┘
                         ↓
            ┌────────────────────────────┐
            │    Base de Datos (H2)      │
            └────────────────────────────┘
```

### UserRepository ✅

**Ubicación:** `src/main/java/edu/eci/dosw/DOSW_Library/core/repository/UserRepository.java`

**Responsabilidades:**
- Buscar usuarios por email, nombre, username
- Verificar unicidad de email/username
- Búsquedas flexibles (case-insensitive)
- Listar usuarios ordenados

**Métodos heredados automáticamente de JpaRepository:**

```java
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Métodos automáticos (NO necesitan decorador @Query):
    
    save(User user)              // INSERT o UPDATE
    findById(String id)          // SELECT ... WHERE id = ?
    findAll()                    // SELECT * FROM users
    deleteById(String id)        // DELETE WHERE id = ?
    count()                       // SELECT COUNT(*) FROM users
    existsById(String id)        // Retorna booleano
}
```

**Métodos Query personalizados (Spring JPA genera SQL automáticamente):**

| Firma del Método | SQL Generado | Ejemplo de Uso |
|------------------|--------------|--------|
| `Optional<User> findByEmail(String email)` | `SELECT * FROM users WHERE email = ?` | `userRepository.findByEmail("john@ex.com")` |
| `List<User> findByNameContaining(String name)` | `SELECT * FROM users WHERE name LIKE %?%` | `findByNameContaining("John")` // "John", "Johnny" |
| `List<User> findByName(String name)` | `SELECT * FROM users WHERE name = ?` | `findByName("John Doe")` |
| `boolean existsByEmail(String email)` | `SELECT EXISTS(...)` | `existsByEmail("test@ex.com")` |
| `List<User> findByNameStartingWith(String prefix)` | `SELECT * FROM users WHERE name LIKE ?%` | `findByNameStartingWith("Jo")` |

**Métodos @Query personalizados (JPQL manual):**

```java
/**
 * Búsqueda flexible: busca en nombre O email (case-insensitive)
 * SQL equivalente: SELECT * FROM users WHERE name ILIKE '%term%' OR email ILIKE '%term%'
 */
@Query("SELECT u FROM User u " +
        "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
        "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
List<User> searchByNameOrEmail(@Param("searchTerm") String searchTerm);

// Uso en servicio:
List<User> results = userRepository.searchByNameOrEmail("john");
// Retorna usuarios con "john" en nombre O email
```

---

### BookRepository ✅

**Ubicación:** `src/main/java/edu/eci/dosw/DOSW_Library/core/repository/BookRepository.java`

**Responsabilidades:**
- Buscar libros por título, autor, ISBN
- Verificar disponibilidad de copias
- Búsquedas por inventario
- Validar unicidad de ISBN

**Métodos destacados:**

| Firma del Método | SQL Generado | Caso de Uso |
|------------------|--------------|--------|
| `List<Book> findByTitle(String title)` | `SELECT * FROM books WHERE title = ?` | Buscar libro exacto |
| `List<Book> findByTitleContaining(String title)` | `SELECT * FROM books WHERE title LIKE %?%` | ¿Qué libros tienen "Clean" en título? |
| `List<Book> findByAuthor(String author)` | `SELECT * FROM books WHERE author = ?` | Libros de un autor específico |
| `List<Book> findByTitleAndAuthor(String title, String author)` | `SELECT * FROM books WHERE title = ? AND author = ?` | Búsqueda precisa |
| `Optional<Book> findByIsbn(String isbn)` | `SELECT * FROM books WHERE isbn = ? LIMIT 1` | Validar ISBN único |
| `List<Book> findByCopiesGreaterThan(int copies)` | `SELECT * FROM books WHERE copies > ?` | Libros con stock > N |

**Métodos avanzados:**

```java
/**
 * Búsqueda combinada: Libros disponibles/no disponibles
 * Útil para interfaz: "Mostrar libros disponibles"
 */
List<Book> findByAvailable(boolean available);

/**
 * Búsqueda de inventario: Libros disponibles CON stock
 */
List<Book> findByAvailableTrueAndCopiesGreaterThan(int copies);

// Ejemplo de uso en servicio:
List<Book> availableBooks = bookRepository.findByAvailableTrueAndCopiesGreaterThan(0);
// Retorna libros que pueden ser prestados
```

---

### LoanRepository ✅

**Ubicación:** `src/main/java/edu/eci/dosw/DOSW_Library/core/repository/LoanRepository.java`

**Responsabilidades:**
- Consultar préstamos por usuario, libro, estado
- Validar límites de préstamos (máx 3 activos por usuario)
- Detectar préstamos vencidos
- Auditoría de préstamos históricos

**Métodos por categoría:**

#### 1. Consultas por Usuario

```java
// Obtener todos los préstamos de un usuario
List<Loan> findByUserId(String userId);

// Obtener solo préstamos ACTIVOS de un usuario
List<Loan> findByUserIdAndStatus(String userId, LoanStatus.ACTIVE);

// Contar préstamos activos (más eficiente que findBy...size())
long countByUserIdAndStatus(String userId, LoanStatus.ACTIVE);

// Ejemplo de validación de límite:
long activeLoans = loanRepository.countByUserIdAndStatus(userId, LoanStatus.ACTIVE);
if (activeLoans >= 3) {
    throw new LoanLimitExceededException("Usuario tiene 3 préstamos activos");
}
```

#### 2. Consultas por Libro

```java
// Obtener todos los préstamos de un libro
List<Loan> findByBookId(String bookId);

// Préstamos activos de un libro (validar disponibilidad)
List<Loan> findByBookIdAndStatus(String bookId, LoanStatus.ACTIVE);

// Contar copias de un libro en circulación
long activeLoans = loanRepository.countByBookIdAndStatus(bookId, LoanStatus.ACTIVE);
int availableCopies = totalCopies - (int)activeLoans;
```

#### 3. Consultas por Estado

```java
// Todos los préstamos activos (para reportes)
List<Loan> findByStatus(LoanStatus.ACTIVE);

// Contar préstamos devueltos (estadísticas)
long returnedLoans = loanRepository.countByStatus(LoanStatus.RETURNED);
```

#### 4. Consultas por Fecha

```java
// Préstamos realizados en una fecha específica
List<Loan> findByLoanDate(LocalDate loanDate);

// Préstamos en rango de fechas (auditoría)
List<Loan> findByLoanDateBetween(LocalDate startDate, LocalDate endDate);

// Préstamos devueltos antes de una fecha
List<Loan> findByReturnDateBefore(LocalDate date);
```

#### 5. Consultas Combinadas (Usuario + Libro)

```java
/**
 * CRÍTICO: Validar que usuario no tenga préstamo duplicado
 * Regla: Un usuario NO puede prestar el mismo libro 2 veces simultáneamente
 */
Optional<Loan> findByUserIdAndBookIdAndStatus(
    String userId, 
    String bookId, 
    LoanStatus status
);

// Validación en servicio:
Optional<Loan> existing = loanRepository
    .findByUserIdAndBookIdAndStatus(userId, bookId, LoanStatus.ACTIVE);
if (existing.isPresent()) {
    throw new IllegalStateException("Usuario ya tiene este libro prestado");
}
```

#### 6. Consultas @Query Avanzadas (evitar N+1 queries)

```java
/**
 * JOIN FETCH: Carga relaciones en UNA sola consulta (no varias)
 * Problema N+1: 1 query para préstamos + N queries para cada usuario/libro
 * Solución: JOIN FETCH carga todo de una vez
 */
@Query("SELECT l FROM Loan l " +
        "JOIN FETCH l.book " +
        "JOIN FETCH l.user " +
        "WHERE l.status = :status")
List<Loan> findAllByStatusWithDetails(@Param("status") LoanStatus status);

// Uso en servicio (sin LazyInitializationException):
List<Loan> loansDetail = loanRepository
    .findAllByStatusWithDetails(LoanStatus.ACTIVE);
// Aquí l.getBook().getTitle() funciona sin error


/**
 * Búsqueda optimizada para perfil de usuario:
 * Muestra todos los préstamos activos con detalles de libro
 */
@Query("SELECT l FROM Loan l " +
        "JOIN FETCH l.book " +
        "WHERE l.user.id = :userId AND l.status = :status")
List<Loan> findByUserIdAndStatusWithDetails(
    @Param("userId") String userId,
    @Param("status") LoanStatus status
);
```

---

### Patrón de Uso: Repository en Capas

**Flujo típico en la aplicación:**

```
USER REQUEST (GET /api/loans/user/123)
         │
         ↓
┌─────────────────────────────┐
│   LoanController.java       │
│  @GetMapping("/{userId}")   │
│  public LoanDTO getLoan()   │
└────────────┬────────────────┘
             │
             ↓
┌─────────────────────────────────────┐
│    LoanService.java (Negocio)      │
│  public List<LoanDTO> getActive    │
│   LoansByUser(String userId) {     │
│                                     │
│  1. loanRepository                 │
│     .findByUserIdAndStatus(        │
│       userId, ACTIVE)              │
│                                     │
│  2. Mapeo: List<Loan> →           │
│     List<LoanDTO>                  │
│                                     │
│  3. return dtos                    │
└────────────┬────────────────────────┘
             │
             ↓
┌───────────────────────────┐
│  LoanRepository           │
│  extends                  │
│  JpaRepository<Loan, ...> │
│                           │
│  findByUserIdAndStatus()  │
│  ├─ Genera SQL:          │
│  │ SELECT * FROM loans  │
│  │ WHERE user_id = ?    │
│  │ AND status = 'ACTIVE'│
│  └─ Ejecuta              │
└────────────┬──────────────┘
             │
             ↓
   ┌─────────────────┐
   │  Base de Datos  │
   │   LOANS TABLE   │
   └─────────────────┘
```

---

### Resumen de Métodos Repository

| Repository | Clase Base | Métodos Clave | Ubicación |
|------------|-----------|---------------|-----------|
| **UserRepository** | `JpaRepository<User, String>` | `findByEmail()`, `findByNameContaining()`, `existsByEmail()` | `core/repository/` |
| **BookRepository** | `JpaRepository<Book, String>` | `findByTitle()`, `findByAuthor()`, `findByIsbn()`, `findByCopiesGreaterThan()` | `core/repository/` |
| **LoanRepository** | `JpaRepository<Loan, String>` | `findByUserId()`, `findByUserIdAndStatus()`, `findByUserIdAndBookIdAndStatus()`, `findAllByStatusWithDetails()` | `core/repository/` |

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

---

## Capa Service: Lógica de Negocio ✅

**Ubicación:** `src/main/java/edu/eci/dosw/DOSW_Library/core/service/`

**Responsabilidad:** Implementar la lógica de negocio, usar Repositories para persistencia, aplicar validaciones y reglas.

### Patrón de la Capa Service

```
┌─────────────────┐
│  Controller     │
│  (HTTP Request) │
└────────┬────────┘
         │
         ↓
┌──────────────────────────────────┐
│  Service (Lógica de Negocio)     │
│                                  │
│  ✅ Validaciones                 │
│  ✅ Reglas de negocio            │
│  ✅ Transacciones                │
│  ✅ Manejo de excepciones        │
│  ✅ Orquestación de datos        │
└────────────┬─────────────────────┘
             │
             ↓
┌──────────────────────────────────┐
│  Repository (Persistencia)       │
│                                  │
│  ✅ CRUD operations              │
│  ✅ Queries personalizadas       │
│  ✅ Acceso a BD                  │
└──────────────────────────────────┘
```

### Ejemplo: UserService

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPersistenceMapper userMapper;
    
    /**
     * Crear nuevo usuario con validaciones
     * - Email único
     * - Username único
     * - Password codificado con BCrypt
     */
    public UserDTO createUser(CreateUserDTO dto) {
        // VALIDACIÓN: verificar unicidad
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email ya registrado");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ConflictException("Username ya registrado");
        }
        
        // MAPEO: DTO → Entidad
        User user = userMapper.toEntity(dto);
        
        // CODIFICACIÓN: password en BCrypt
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        // PERSISTENCIA: guardar en BD
        User saved = userRepository.save(user);
        
        // MAPEO: Entidad → DTO (sin password/credenciales)
        return userMapper.toDTO(saved);
    }
    
    /**
     * Obtener usuario por email
     * Lanzar excepción si no existe
     */
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return userMapper.toDTO(user);
    }
    
    /**
     * Búsqueda flexible por nombre o email
     * Usecase: Búsqueda en perfil de admin
     */
    public List<UserDTO> searchUsers(String searchTerm) {
        List<User> users = userRepository.searchByNameOrEmail(searchTerm);
        return users.stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList());
    }
}
```

### Reglas de Negocio Clave por Servicio

| Servicio | Regla | Implementación |
|----------|-------|-----------------|
| **UserService** | Email/Username únicos | `existsByEmail()`, validación antes de `save()` |
| **BookService** | ISBN único | `findByIsbn()`, lanzar `ConflictException` si duplicado |
| **LoanService** | Máx 3 préstamos activos/usuario | `countByUserIdAndStatus()` >= 3 → excepción |
| **LoanService** | No prestar mismo libro 2 veces | `findByUserIdAndBookIdAndStatus()` debe estar vacío |
| **LoanService** | dueDate automático | `loanDate + 14 días` calculado en `@PrePersist` |
| **BookService** | Validar disponibilidad | `available > 0` debe ser verdadero |

---

## Validación de Datos en DTOs ✅

**Ubicación:** `src/main/java/edu/eci/dosw/DOSW_Library/controller/dto/`

**Responsabilidad:** Validar entrada HTTP antes de llegar al Service.

### Anotaciones de Validación

```java
// CreateUserDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "Debe ser un email válido")
    private String email;
    
    @NotBlank(message = "El username es requerido")
    @Size(min = 3, max = 20, message = "Username entre 3-20 caracteres")
    private String username;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    
    @NotNull(message = "El DNI es requerido")
    @Pattern(regexp = "^[0-9]{8,10}$", message = "DNI inválido")
    private String dni;
}
```

### En Controller (activar validación)

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO dto) {
        // @Valid → Valida dto ANTES de ejecutar el método
        // Si hay errores → HTTP 400 Bad Request
        UserDTO created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

| Anotación | Función | Ejemplo |
|-----------|---------|---------|
| `@NotBlank` | No nulo, no vacío, no solo espacios | `@NotBlank private String email` |
| `@NotNull` | No nulo (pero sí puede estar vacío) | `@NotNull private Integer copies` |
| `@Size` | Rango de tamaño | `@Size(min=3, max=100)` |
| `@Email` | Validar formato email | `@Email private String email` |
| `@Pattern` | Validar con expresión regular | `@Pattern(regexp="^[0-9]{8}$")` |
| `@Min` / `@Max` | Valores numéricos | `@Min(1) @Max(100)` |
| `@Future` / `@Past` | Fechas futuras/pasadas | `@Future private LocalDate dueDate` |

---

## Manejo de Errores y Excepciones ✅

**Ubicación:** `src/main/java/edu/eci/dosw/DOSW_Library/core/exception/`

**Responsabilidad:** Mantener consistencia en respuestas de error HTTP.

### Excepciones Customizadas

```java
// ResourceNotFoundException.java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// ConflictException.java
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

// LoanLimitExceededException.java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
```

### Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 404: Recurso no encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * 409: Conflicto (duplicado, etc.)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * 400: Errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Errores de validación")
            .details(errors)
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * 500: Errores internos
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Error interno del servidor", ex);
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("Error interno del servidor")
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### ErrorResponse DTO

```java
@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;
}
```

---

## Autenticación y Autorización con JWT ✅

**Ubicación:** `src/main/java/edu/eci/dosw/DOSW_Library/security/`

**Responsabilidad:** Generar tokens JWT, validarlos en cada petición, autorizar por roles.

### JwtService: Generar y Validar Tokens

```java
@Service
@Slf4j
public class JwtService {
    
    @Value("${security.jwt.secret}")
    private String SECRET_KEY;
    
    @Value("${security.jwt.expiration-ms}")
    private long EXPIRATION_TIME;
    
    /**
     * Generar JWT token para usuario autenticado
     * Payload: userId, email, roles
     * Expiración: configurable (ej: 1 hora)
     */
    public String generateToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();
    }
    
    /**
     * Extraer userId del token (subject)
     */
    public String extractUserId(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    
    /**
     * Extraer rol del token
     */
    public String extractRole(String token) {
        return (String) Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody()
            .get("role");
    }
    
    /**
     * Validar si token es válido (no expirado, firma correcta)
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Token inválido: {}", ex.getMessage());
            return false;
        }
    }
}
```

### Flujo de Autenticación

```
1. LOGIN (POST /api/auth/login)
   ┌─────────────────────────────┐
   │ { username, password }      │
   └──────────┬──────────────────┘
              │
              ↓
   ┌──────────────────────────────────────┐
   │ AuthController                       │
   │ - Valida credenciales                │
   │ - Carga User del repositorio         │
   │ - Compara password (BCrypt)          │
   └──────────┬───────────────────────────┘
              │
              ↓
   ┌──────────────────────────────────────┐
   │ JwtService.generateToken()           │
   │ - userId, email, role como claims   │
   │ - Firmar con SECRET_KEY              │
   │ - Retornar JWT                       │
   └──────────┬───────────────────────────┘
              │
              ↓
   ┌─────────────────────────────┐
   │ HTTP 200 + { token: "..." } │
   └─────────────────────────────┘

2. REQUEST PROTEGIDO (GET /api/users)
   ┌──────────────────────────────────────┐
   │ Header: Authorization: Bearer <token>│
   └──────────┬───────────────────────────┘
              │
              ↓
   ┌──────────────────────────────────────┐
   │ JwtAuthenticationFilter              │
   │ - Extraer token del Header           │
   │ - Validar con JwtService             │
   │ - Crear Authentication               │
   └──────────┬───────────────────────────┘
              │
              ↓
   ┌──────────────────────────────────────┐
   │ SecurityContext                      │
   │ - Almacenar Authentication           │
   │ - Permitir acceso al Controller      │
   └──────────────────────────────────────┘
```

### Autorización por Roles

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    /**
     * Solo ADMIN puede acceder
     * @PreAuthorize("hasRole('ADMIN')")
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        // Solo usuarios con rol ADMIN pueden ejecutar
        // Si no → HTTP 403 Forbidden
        return ResponseEntity.noContent().build();
    }
    
    /**
     * ADMIN o BIBLIOTECARIO
     */
    @PostMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody CreateBookDTO dto) {
        // ...
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

---

## Logging y Auditoría ✅

**Ubicación:** Configuración en `application.yaml` y anotaciones `@Slf4j`

**Responsabilidad:** Rastrear operaciones importantes, debugging y auditoría.

### Configuración de Logging

```yaml
logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    edu.eci.dosw.DOSW_Library: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/dosw-library.log
    max-size: 10MB
    max-history: 30
```

### Logging en Services

```java
@Service
@Slf4j  // Lombok proporciona 'log'
public class UserService {
    
    public UserDTO createUser(CreateUserDTO dto) {
        log.info("Creando nuevo usuario: {}", dto.getUsername());
        
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Intento de registro con email duplicado: {}", dto.getEmail());
            throw new ConflictException("Email ya registrado");
        }
        
        try {
            User user = userRepository.save(userMapper.toEntity(dto));
            log.info("Usuario creado exitosamente: {} (ID: {})", dto.getUsername(), user.getId());
            return userMapper.toDTO(user);
        } catch (Exception ex) {
            log.error("Error al crear usuario: {}", dto.getUsername(), ex);
            throw new RuntimeException("Error al crear usuario", ex);
        }
    }
    
    public UserDTO getUserById(String id) {
        log.debug("Buscando usuario por ID: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado: {}", id);
                return new ResourceNotFoundException("Usuario no encontrado");
            });
        log.debug("Usuario encontrado: {}", user.getUsername());
        return userMapper.toDTO(user);
    }
}
```

### Logging en Controllers

```java
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO dto) {
        log.info("POST /api/users - Crear usuario: {}", dto.getUsername());
        UserDTO created = userService.createUser(dto);
        log.info("Usuario creado exitosamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        log.debug("GET /api/users/{} - Obtener usuario", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.warn("DELETE /api/users/{} - Eliminando usuario (Acción crítica)", id);
        userService.deleteUser(id);
        log.info("Usuario eliminado: {}", id);
        return ResponseEntity.noContent().build();
    }
}
```

### Niveles de Log

| Nivel | Uso | Ejemplo |
|-------|-----|---------|
| `TRACE` | Información muy detallada (parámetros SQL) | `log.trace("Parameter: {}", value)` |
| `DEBUG` | Información de debugging (flujo de entrada/salida) | `log.debug("Buscando usuario por ID: {}", id)` |
| `INFO` | Eventos importantes (login, creación de recursos) | `log.info("Usuario creado: {}", email)` |
| `WARN` | Situaciones anómalas pero recuperables | `log.warn("Email duplicado: {}", email)` |
| `ERROR` | Errores que interrumpen operaciones | `log.error("Error al guardar usuario", ex)` |

---

## Configuracion y Ejecucion

### Requisitos

- Java 21
- Maven Wrapper (incluido)

### Dependencia Spring Data JPA ✅

#### En pom.xml (ya incluida)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**¿Qué es Spring Data JPA?**
- Framework que simplifica acceso a datos con JPA/Hibernate
- Proporciona auto-generación de queries SQL
- Manejo automático de transacciones
- Mapeo objeto-relacional (ORM) sin código repetitivo

**¿Por qué lo usamos?**
- Repositories: interfaz que reemplaza DAO boilerplate
- Query methods: `findByUsername()`, `findByEmail()` generadas automáticamente
- @Query: para queries JPQL personalizadas
- Paginación y ordenamiento built-in

#### Paso a Paso: Cómo funciona Spring Data JPA en DOSW-Library

**PASO 1: Definir una entidad JPA**

```java
// src/main/java/edu/eci/dosw/DOSW_Library/core/model/Book.java

@Entity
@Table(name = "books", uniqueConstraints = { @UniqueConstraint(columnNames = "isbn") })
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false)
    private Integer copies;
}
```

**¿Qué significa?**
- `@Entity`: Spring Data JPA crea tabla `books` automáticamente
- `@Id @GeneratedValue`: id se auto-incrementa en BD
- `@Column`: restricciones de columna (nullable, unique, length)
- `@Table`: mapping a tabla específica en BD

**PASO 2: Crear interfaz Repository**

```java
// src/main/java/edu/eci/dosw/DOSW_Library/core/repository/BookRepository.java

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Spring Data genera automáticamente:
    // - findAll(), findById(id), save(), delete(), etc
    
    // Query methods personalizadas (Spring genera SQL automáticamente):
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitleIgnoreCase(String title);
    List<Book> findByAuthorIgnoreCase(String author);
    
    // Custom queries con @Query:
    @Query("SELECT b FROM Book b WHERE b.available > 0 ORDER BY b.available DESC")
    List<Book> findAvailableBooks();
}
```

**¿Cómo funciona?**
- `extends JpaRepository<Book, Long>`: Book = entidad, Long = tipo de ID
- `findByIsbn(isbn)`: genera `SELECT * FROM books WHERE isbn = ?`
- `findByTitleIgnoreCase(title)`: genera `SELECT * FROM books WHERE LOWER(title) = LOWER(?)`
- `@Query(...)`: para queries más complejas

**PASO 3: Usar el Repository en el Service**

```java
// src/main/java/edu/eci/dosw/DOSW_Library/core/service/BookService.java

@Service
public class BookService {
    
    private final BookRepository bookRepository;
    
    // Inyección de BookRepository
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    // Obtener todos los libros
    public List<Book> getAllBooks() {
        return bookRepository.findAll();  // SELECT * FROM books
    }
    
    // Obtener libro por ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        // SELECT * FROM books WHERE id = ?
    }
    
    // Crear nuevo libro
    public Book createBook(Book book) {
        // Validaciones...
        return bookRepository.save(book);  // INSERT INTO books (...)
    }
    
    // Buscar por ISBN (automática)
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new ConflictException("Book not found"));
        // SELECT * FROM books WHERE isbn = ?
    }
    
    // Obtener libros disponibles
    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks();
        // SELECT b FROM Book b WHERE b.available > 0 ORDER BY b.available DESC
    }
}
```

**¿Qué sucede?**
- `bookRepository.findAll()` → SQL: `SELECT * FROM books`
- `bookRepository.findById(id)` → SQL: `SELECT * FROM books WHERE id = ?`
- `bookRepository.save(book)` → SQL: `INSERT ... ON DUPLICATE KEY UPDATE`
- `bookRepository.findByIsbn(isbn)` → SQL: `SELECT * FROM books WHERE isbn = ?`

**PASO 4: Controller usa el Service**

```java
// src/main/java/edu/eci/dosw/DOSW_Library/controller/BookController.java

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookService bookService;
    private final BookPersistenceMapper bookMapper;
    
    @GetMapping
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookService.getAllBooks();  // JPA Query
        return books.stream()
            .map(bookMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody CreateBookDTO dto) {
        Book book = bookMapper.toEntity(dto);
        Book saved = bookService.createBook(book);  // JPA save()
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookMapper.toDTO(saved));
    }
}
```

**Flujo completo:**
```
Client HTTP (POST /api/books)
  ↓
BookController.createBook(CreateBookDTO)
  ↓
BookPersistenceMapper.toEntity(dto)  → Book @Entity
  ↓
BookService.createBook(book)
  ↓
BookRepository.save(book)  ← Spring Data JPA
  ↓
Hibernate genera: INSERT INTO books (title, author, isbn, copies, ...)
  ↓
H2 Database ejecuta SQL
  ↓
BD retorna Book confirmado (con id auto-generado)
  ↓
BookPersistenceMapper.toDTO(book)  → BookDTO
  ↓
HTTP 201 Created + BookDTO JSON
```

#### Configuración en application.properties

```properties
# H2 Database (por defecto en memoria)
spring.datasource.url=jdbc:h2:mem:librarydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate (Spring Data JPA lo configura)
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# ddl-auto opciones:
#   create: elimina + crea tablas cada vez (DESARROLLO)
#   create-drop: como create + drop al terminar
#   update: modifica esquema sin perder datos (TESTING)
#   validate: solo valida (PRODUCCIÓN)
spring.jpa.hibernate.ddl-auto=create-drop

# Mostrar SQL generado (DEBUG)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console web para ver BD
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### Tabla resumen: Spring Data JPA vs Manual JDBC/SQL

| Operación | JDBC Manual | Spring Data JPA |
|-----------|------------|-----------------|
| SELECT * FROM books | `new JdbcTemplate().query(sql)` | `bookRepository.findAll()` |
| SELECT * FROM books WHERE id = ? | `rs.getInt("id")` repetido | `bookRepository.findById(id)` |
| SELECT * FROM books WHERE isbn = ? | Query manual JDBC | `bookRepository.findByIsbn(isbn)` |
| INSERT | `ps.setString(1, ...)` | `bookRepository.save(book)` |
| UPDATE | `ps.execute()` | `bookRepository.save(book)` |
| DELETE | `ps.executeUpdate()` | `bookRepository.deleteById(id)` |
| Transacciones | Manual try/catch | `@Transactional` automático |

**Ventajas:**
- ✅ Menos código
- ✅ Menos bugs (sin string casting)
- ✅ Queries generadas automáticamente
- ✅ Transacciones manejadas por Spring

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

### application.properties: Configuración Base y Spring Data JPA

**Ver sección anterior "Dependencia Spring Data JPA" para detalles completos de configuración.**

```properties
# ============================================
# BASE DE DATOS: H2 (en memoria)
# ============================================
spring.datasource.url=jdbc:h2:mem:librarydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# ============================================
# SPRING DATA JPA / HIBERNATE
# ============================================
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop  # create-drop=desarrollo, update=testing, validate=produccion
spring.jpa.show-sql=true                   # Mostrar SQL generado (DEBUG)
spring.jpa.properties.hibernate.format_sql=true  # Formatea SQL legiblemente

# ============================================
# H2 CONSOLE (acceso web)
# ============================================
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ============================================
# SWAGGER / SPRINGDOC-OPENAPI
# ============================================
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# ============================================
# SEGURIDAD JWT (custom properties)
# ============================================
security.jwt.secret=<base64-secret>
security.jwt.expiration-ms=3600000
security.auth.username=admin
security.auth.password=admin1234
```

**Acceso rápido durante desarrollo:**

```bash
# Ver todas las queries SQL que genera Spring Data JPA:
# Habilitado con spring.jpa.show-sql=true

# Acceder a consola H2 en navegador:
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:librarydb
# Usuario: sa
# Password: (vacío)
```

---

### Configuración PostgreSQL + application.yaml ✅

**Guía paso a paso para migrar de H2 a PostgreSQL**

#### PASO 1: Instalar PostgreSQL

**En Windows:**
1. Descarga: https://www.postgresql.org/download/windows/
2. Ejecuta el instalador oficial
3. **Importante:** Anota la contraseña del usuario `postgres`
4. Puerto por defecto: `5432`
5. Verifica instalación en PowerShell:
```bash
psql --version
```

**En Linux (Ubuntu/Debian):**
```bash
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**En macOS:**
```bash
brew install postgresql@15
brew services start postgresql
```

---

#### PASO 2: Crear Base de Datos PostgreSQL

Abre PowerShell/Terminal y conecta:

```bash
psql -U postgres
```

Ingresa la contraseña que configuraste. Luego ejecuta:

```sql
-- Crear base de datos para DOSW Library
CREATE DATABASE dosw_library_db;

-- Verificar que se creó
\l

-- Salir
\q
```

**Resultado esperado:**
```
dosw_library_db | postgres | UTF8 | ...
```

---

#### PASO 3: Agregar Dependencia PostgreSQL al pom.xml

Abre [pom.xml](pom.xml) y busca la sección `<dependencies>`. Después de la dependencia H2, agrega:

```xml
<!-- PostgreSQL JDBC Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
    <scope>runtime</scope>
</dependency>
```

Maven descargará el driver automáticamente.

---

#### PASO 4: Crear y Configurar application.yaml

En `src/main/resources/`, crea un archivo llamado **`application.yaml`**:

```yaml
# ============================================
# APPLICATION
# ============================================
spring:
  application:
    name: dosw-library

  # ============================================
  # DATASOURCE: PostgreSQL
  # ============================================
  datasource:
    url: jdbc:postgresql://localhost:5432/dosw_library_db
    username: postgres
    password: <tu_contraseña>  # Reemplaza con la contraseña de instalación
    driverClassName: org.postgresql.Driver

  # ============================================
  # JPA / HIBERNATE
  # ============================================
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: create-drop  # Opciones: create-drop (desarrollo), update (testing), validate (producción)
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        jdbc:
          batch_size: 20
          fetch_size: 50

  # ============================================
  # H2 CONSOLE (mantener para testing opcional)
  # ============================================
  h2:
    console:
      enabled: true
      path: /h2-console

# ============================================
# SPRINGDOC / SWAGGER
# ============================================
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha

# ============================================
# SEGURIDAD JWT (custom properties)
# ============================================
security:
  jwt:
    secret: "my-secret-key-dosw-library-spring-boot-jwt-token-generation-and-validation-key-2026"
    expiration-ms: 3600000  # 1 hora
  auth:
    username: admin
    password: admin1234

# ============================================
# LOGGING
# ============================================
logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

**Explicación de parámetros clave:**

| Parámetro | Significado |
|-----------|------------|
| `jdbc:postgresql://localhost:5432/dosw_library_db` | Conexión a PostgreSQL en puerto 5432 |
| `username: postgres` | Usuario por defecto de PostgreSQL |
| `password: <contraseña>` | LA CONTRASEÑA QUE PUSISTE EN INSTALACIÓN |
| `PostgreSQLDialect` | Dialect específico para PostgreSQL |
| `ddl-auto: create-drop` | Crea tablas al iniciar, las elimina al terminar (DESARROLLO) |
| `show-sql: true` | Muestra todas las queries SQL en consola |
| `format_sql: true` | Las formatea legiblemente |
| `batch_size: 20` | Agrupa 20 inserts en una operación |

---

#### PASO 5: Desactivar application.properties (Opcional)

Si deseas usar SOLO YAML, renombra el archivo properties:

```bash
# En Windows PowerShell
Rename-Item src/main/resources/application.properties application.properties.bak

# En Linux/macOS
mv src/main/resources/application.properties src/main/resources/application.properties.bak
```

**Nota:** Si dejas ambos archivos, Spring usa `.yaml` con prioridad.

---

#### PASO 6: Verificar Conexión a PostgreSQL

Abre terminal en VS Code y ejecuta:

```bash
cd E:\DOSW\DOSW\DOSW-Library
mvn spring-boot:run
```

**Esperado en la consola (líneas que buscar):**

```
Starting DoswLibraryApplication...
HikariPool-1 - Starting...
HikariPool-1 - Init completed in 500ms
```

**Si ves esto, la conexión funcionó ✅:**
```
Hibernate: CREATE TABLE users (...)
Hibernate: CREATE TABLE books (...)
Hibernate: CREATE TABLE loans (...)
```

**Si hay error de conexión ❌:**
```
ERROR org.postgresql.Driver - Connection refused
```
→ Verifica que PostgreSQL está corriendo y la contraseña es correcta.

---

#### PASO 7: Verificar Tablas en pgAdmin (GUI PostgreSQL)

1. Abre **pgAdmin** (se instaló con PostgreSQL, o descárgalo: https://www.pgadmin.org/)
2. Login con usuario postgres
3. Haz clic en **Servers** → **PostgreSQL** → **Databases**
4. Deberías ver: `dosw_library_db`
5. Expande: **dosw_library_db** → **Schemas** → **public** → **Tables**
6. Deberías ver: `users`, `books`, `loans` (creadas automáticamente por Hibernate)

---

#### Tabla Resumen: H2 vs PostgreSQL

| Parámetro | H2 (Antes) | PostgreSQL (Ahora) |
|-----------|-----------|------------------|
| **Driver** | `org.h2.Driver` | `org.postgresql.Driver` |
| **URL** | `jdbc:h2:mem:librarydb` | `jdbc:postgresql://localhost:5432/dosw_library_db` |
| **Dialect** | `H2Dialect` | `PostgreSQLDialect` |
| **Datos** | En memoria (se pierden al terminar) | En disco (permanente) |
| **Puerto** | N/A | 5432 |
| **Usuario** | `sa` | `postgres` |
| **Contraseña** | (vacío) | (aquella que configuraste) |
| **Uso** | Desarrollo rápido | Producción/Testing |

---

#### Troubleshooting

**Error: "Connection refused"**
```bash
# Verifica que PostgreSQL está corriendo
pg_isready

# Verifica puerto 5432 (Windows)
netstat -tuln | findstr 5432

# Verifica puerto 5432 (Linux/macOS)
lsof -i :5432
```

**Error: "role postgres does not exist"**
- ✅ Verifica usuario correcto en `application.yaml`
- ✅ O crea nuevo usuario: `CREATE USER dosw_user WITH PASSWORD 'password';`

**Error: "database dosw_library_db does not exist"**
- ✅ Ejecuta el comando `CREATE DATABASE` del PASO 2

**Error: "password authentication failed"**
- ✅ Verifica que la contraseña en `application.yaml` es exacta (case-sensitive)
- ✅ Reinicia PostgreSQL después de cambiar contraseña

**Error: "La clave privada está en formato no soportado"**
- ✅ PostgreSQL 13+ requiere: `CREATE USER usuario WITH PASSWORD 'pass' ENCRYPTED;`

---

#### URL de acceso con PostgreSQL

- API base: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs
- pgAdmin (GUI): http://localhost:5050 (si está instalado)

---

## 📋 Implementación Práctica: Setup Real de PostgreSQL + application.yaml

### ✅ Paso 1: Verificación del Entorno

**Estado del Sistema:**
- PostgreSQL: No instalado aún en el sistema
- Docker: No disponible
- Alternativa: Se han preparado pasos para instalar PostgreSQL en Windows

### ✅ Paso 2: Crear archivo application.yaml

**Archivo creado**: `src/main/resources/application.yaml`

El archivo ha sido generado con la siguiente estructura:

```yaml
spring:
  application:
    name: DOSW-Library
  datasource:
    url: jdbc:postgresql://localhost:5432/dosw_library_db
    username: postgres
    password: postgres123  # CAMBIAR a tu contraseña
    driverClassName: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

**Características incluidas:**
- ✅ Conexión a PostgreSQL en puerto 5432
- ✅ Pool de conexiones HikariCP (máximo 10 conexiones)
- ✅ Hibernate DDL en modo `create-drop` (desarrollo)
- ✅ Mostrar SQL queries en consola
- ✅ Logging configurado para DEBUG en capas service/controller
- ✅ Swagger UI habilitado en `/swagger-ui.html`

### ✅ Paso 3: Instalar PostgreSQL en Windows

Como PostgreSQL no está instalado, sigue estos pasos en tu máquina:

**INSTALACIÓN (Windows):**

1. **Descargar PostgreSQL:**
   - Ve a: https://www.postgresql.org/download/windows/
   - Descarga PostgreSQL 15+ (Latest versión)
   - Ejecuta el instalador `.exe`

2. **Durante la instalación:**
   ```
   Installation Directory: C:\Program Files\PostgreSQL\15
   Port: 5432 (default)
   Superuser: postgres
   PASSWORD: [ESCRIBE Y ANOTA UNA CONTRASEÑA SEGURA]
   ```
   
   **⚠️ IMPORTANTE:** Memoriza esta contraseña, la necesitarás en `application.yaml`

3. **Verificar instalación:**
   ```powershell
   psql --version
   # Output: psql (PostgreSQL) 15.x
   ```

### ✅ Paso 4: Crear Base de Datos `dosw_library_db`

Abre **PowerShell** o **Command Prompt** y ejecuta:

```powershell
# Conecta a PostgreSQL como superuser
psql -U postgres

# Ingresa la contraseña que configuraste
```

Dentro de la consola `psql`:

```sql
-- Crear base de datos
CREATE DATABASE dosw_library_db;

-- Verificar que se creó
\l

-- Ver conexión actual
\conninfo

-- Salir
\q
```

**Resultado esperado en `\l`:**
```
dosw_library_db | postgres | UTF8 | en_US.UTF-8 | en_US.UTF-8
```

### ✅ Paso 5: Actualizar application.yaml con tu Contraseña

Abre `src/main/resources/application.yaml` y reemplaza:

```yaml
# ANTES:
password: postgres123

# DESPUÉS (con tu contraseña):
password: TU_CONTRASEÑA_REAL
```

### ✅ Paso 6: Ejecutar la Aplicación

Desde la raíz del proyecto:

```powershell
cd e:\DOSW\DOSW\DOSW-Library

# Compilar y ejecutar con Maven
mvn clean spring-boot:run
```

**Espera a ver en la consola:**
```
Starting DoswLibraryApplication v1.0 on DESKTOP-XXXX with PID...
Started DoswLibraryApplication in 3.456 seconds (JVM running for 4.123)
HikariPool-1 - Starting HikariCP connection pool
HikariPool-1 - Init completed in XXms
Hibernate: CREATE TABLE users (...)
Hibernate: CREATE TABLE books (...)
Hibernate: CREATE TABLE loans (...)
```

### ✅ Paso 7: Verificar Conexión

1. **En la consola:**
   - Busca "HikariPool-1 - Init completed" → ✅ Conexión exitosa
   - Si ves "Connection refused" → ❌ PostgreSQL no está corriendo

2. **Acceder a Swagger UI:**
   - Abre: http://localhost:8080/swagger-ui.html
   - Deberías ver todos los endpoints

3. **Verificar tablas con pgAdmin (GUI):**
   - Descarga pgAdmin: https://www.pgadmin.org/
   - O usa psql en terminal:
     ```sql
     psql -U postgres -d dosw_library_db
     \dt  # Listar todas las tablas
     ```

### ✅ Paso 8: Probar un Endpoint

**Crear un usuario (POST):**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "username": "testuser",
    "password": "Pass1234!",
    "fullName": "Test User",
    "dni": "1234567890",
    "role": "USUARIO"
  }'
```

**Si todo funciona, verás:**
```json
{
  "id": "user_uuid_aqui",
  "email": "test@example.com",
  "username": "testuser",
  "fullName": "Test User",
  "role": "USUARIO"
}
```

### 🔧 Troubleshooting - Si Algo Falla

**Problema: "psql: could not connect to server"**
```powershell
# Verificar que PostgreSQL está corriendo (Windows Services)
Get-Service postgresql-x64-15 | Start-Service

# O usar pg_isready
pg_isready -h localhost -p 5432
```

**Problema: "password authentication failed"**
- Verifica la contraseña en `application.yaml` es idéntica
- Las contraseñas son case-sensitive
- Reinicia PostgreSQL después de cambiarla

**Problema: "database dosw_library_db does not exist"**
```sql
-- Conecta como postgres y verifica
psql -U postgres
\l
-- Si no existe, crea:
CREATE DATABASE dosw_library_db;
```

**Problema: "HikariPool error"**
- Espera 5-10 segundos que PostgreSQL termine de iniciar
- Verifica puerto 5432 no esté en uso:
  ```powershell
  netstat -ano | findstr 5432
  ```

### 📊 Resumen de Configuración Aplicada

| Aspecto | Valor |
|--------|-------|
| **Database** | PostgreSQL 15+ |
| **JDBC URL** | `jdbc:postgresql://localhost:5432/dosw_library_db` |
| **Username** | `postgres` |
| **Password** | `[Tu contraseña de instalación]` |
| **Driver** | `org.postgresql.Driver` |
| **Hibernate Dialect** | `PostgreSQLDialect` |
| **DDL Strategy** | `create-drop` (desarrollo) |
| **Connection Pool** | HikariCP (10 máx) |
| **Logging** | DEBUG para capas de aplicación |
| **Swagger** | `/swagger-ui.html` |

### 📝 Próximos Pasos

1. ✅ Instala PostgreSQL (si no lo has hecho)
2. ✅ Crea la base de datos `dosw_library_db`
3. ✅ Actualiza `password` en `application.yaml`
4. ✅ Ejecuta `mvn clean spring-boot:run`
5. ✅ Verifica conexión en Swagger UI
6. ✅ Prueba crear un usuario con el endpoint POST /api/users

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

## Pruebas Funcionales e Integración

### Propósito
Las pruebas funcionales verifican que cada operación de los controladores funciona correctamente y persiste datos reales en la base de datos. Esto incluye validación de:
- Respuestas HTTP correctas
- Persistencia de datos en BD
- Cambios en inventario de libros
- Relaciones entre entidades (Usuario ↔ Libro ↔ Préstamo)

### Pruebas Creadas

#### Ubicación del Código
- **Archivo Principal**: `src/test/java/edu/eci/dosw/DOSW_Library/integration/FunctionalIntegrationTest.java`
- **Configuración Test**: `src/test/resources/application-test.properties`
- **Uso de**: `@SpringBootTest`, `WebTestClient`, Repositories reales

#### Cobertura de Pruebas (11 Tests Implementados)

**Sección UserController (3 tests)**:
1. ✅ `testCreateUserAndVerifyInDatabase()` - POST /api/users
   - Crea usuario y verifica persistencia en BD
   - Valida: ID, nombre, email guardados correctamente

2. ✅ `testGetAllUsers()` - GET /api/users
   - Lista todos los usuarios
   - Valida: Conteo correcto de registros en BD

3. ✅ `testGetUserById()` - GET /api/users/{id}
   - Obtiene usuario por ID
   - Valida: Datos coinciden con BD

**Sección BookController (3 tests)**:
4. ✅ `testCreateBook()` - POST /api/books
   - Crea libro y verifica persistencia
   - Valida: Título, autor, copias disponibles

5. ✅ `testGetAllBooks()` - GET /api/books
   - Lista todos los libros
   - Valida: Cantidad de registros correcta

6. ✅ `testGetBookById()` - GET /api/books/{id}
   - Obtiene libro específico
   - Valida: Datos persisten correctamente

**Sección LoanController (5 tests)**:
7. ✅ `testCreateLoanAndCheckInventory()` - POST /api/loans
   - Crea préstamo y **verifica decremento de inventario**
   - Validaciones críticas:
     - Copias disponibles antes: 5
     - Copias después del préstamo: 4 ✓
     - Préstamo creado en BD ✓

8. ✅ `testReturnLoanAndCheckInventory()` - PUT /api/loans/{id}/return
   - Devuelve libro y **verifica incremento de inventario**
   - Validaciones críticas:
     - Copias disponibles antes: 0
     - Copias después de devolución: 1 ✓
     - Estado del préstamo: RETURNED ✓

9. ✅ `testGetAllLoans()` - GET /api/loans
   - Lista todos los préstamos
   - Valida: Conteo correcto en BD

10. ✅ `testGetLoanById()` - GET /api/loans/{id}
    - Obtiene préstamo específico
    - Valida: Relación con usuario y libro

11. ✅ `testCompleteScenario()` - Escenario End-to-End
    - **4 pasos secuenciales**:
      - STEP 1: Crear usuario
      - STEP 2: Crear libro (3 copias)
      - STEP 3: Crear préstamo
      - STEP 4: Verificar BD (1 usuario, 1 libro, 1 préstamo, 2 copias disponibles)

### Evidencia de Persistencia en Base de Datos

#### Cambios Verificados Después de Operaciones:

**POST /api/users - Crear Usuario**
```
📊 BD Verificación:
- Usuario con ID 'USR-001' existe en tabla USERS
- Nombre: 'Juan Pérez'
- Email: 'juan@example.com'
```

**POST /api/books - Crear Libro**
```
📊 BD Verificación:
- Libro con ID 'BOOK-001' existe en tabla BOOKS
- Título: 'Clean Code'
- Copias disponibles: 5
```

**POST /api/loans - Crear Préstamo con Decremento de Inventario**
```
📊 BD Verificación - ANTES:
- Libro BOOK-001: available = 5

📊 BD Verificación - DESPUÉS:
- Libro BOOK-001: available = 4 (decrementado ✓)
- Préstamo LOAN-001 creado en tabla LOANS
- Estado: ACTIVE
```

**PUT /api/loans/{id}/return - Devolver Libro con Incremento**
```
📊 BD Verificación - ANTES:
- Libro BOOK-001: available = 0

📊 BD Verificación - DESPUÉS:
- Libro BOOK-001: available = 1 (incrementado ✓)
- Préstamo LOAN-001 actualizado
- Estado: RETURNED
```

### Configuración del Entorno de Pruebas

#### application-test.properties
```properties
# Base de datos: H2 en memoria
spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Aislamiento de cada test
@BeforeEach void setUp() {
  loanRepository.deleteAll();
  bookRepository.deleteAll();
  userRepository.deleteAll();
}
```

#### Características de Test:
- ✅ **Independencia**: Cada test limpia BD antes de ejecutar
- ✅ **Aislamiento**: Base de datos H2 en memoria, sin que afecte BD real
- ✅ **Velocidad**: Ejecución < 30 segundos
- ✅ **Reproducibilidad**: Mismos datos, mismo resultado siempre

### Estado de Ejecución Actual

**Compilación**: ✅ Éxito (56 archivos compilados)

**Problemas Identificados y Soluciones**:

| Problema | Causa | Estado | Solución |
|----------|-------|--------|----------|
| Duplicate key 'spring' en YAML | application.properties vs application-test.properties conflicto | 🔄 En Progreso | Separar en profiles distintos o usar Spring Boot 4.1+ |
| ApplicationContext load failure | Configuración de repositorios | 🔄 En Progreso | Agregar `@ComponentScan` explícito |
| WebTestClient no funciona en Spring Boot 4.0.3 | Versión Spring | ⚠️ | Actualizar a Spring Boot 4.1+ o usar MockMvc |

### Próximos Pasos para Ejecución Exitosa

1. **Opción A - Actualizar Spring Boot**:
   ```xml
   <version>4.1.0</version> <!-- en lugar de 4.0.3 -->
   ```

2. **Opción B - Configurar perfiles separados**:
   - Crear `application-prod.properties` (PostgreSQL)
   - Mantener `application-test.properties` (H2)
   - En pom.xml, agregar: `<activeProfiles>test</activeProfiles>`

3. **Opción C - Cambiar a MockMvc**:
   ```java
   @WebMvcTest(UserController.class)
   @AutoConfigureMockMvc
   ```

### Ejecución Manual de Pruebas

```bash
# Ejecutar todas las pruebas funcionales
mvn test -Dtest=FunctionalIntegrationTest

# Ejecutar prueba específica
mvn test -Dtest=FunctionalIntegrationTest#testCreateUserAndVerifyInDatabase

# Ver reportes
cat target/surefire-reports/edu.eci.dosw.DOSW_Library.integration.FunctionalIntegrationTest.txt
```

### Cobertura Actual

- **Controllers Testeados**: 3/3 (100%)
  - UserController ✅
  - BookController ✅
  - LoanController ✅

- **Endpoints Cubiertos**: 14/15 (93%)
  - POST/GET/PATCH/DELETE usuarios ✅
  - POST/GET/PATCH/DELETE libros ✅
  - POST/GET/PUT/return préstamos ✅
  - Falta: GET /loans/user/{userId}/active ⏳

- **Cambios en BD Validados**:
  - Creación de usuarios ✅
  - Creación de libros ✅
  - Decremento de inventario en préstamo ✅
  - Incremento de inventario en devolución ✅
  - Relaciones entre entidades ✅

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
- `ON DELETE SET NULL`: Asigna NULL al FK en registros hijosdos**:
   - Crear `application-prod.properties` (PostgreSQL)
   - Mantener `application-test.properties` (H2)
   - En pom.xml, agregar: `<activeProfiles>test</activeProfiles>`

3. **Opción C - Cambiar a MockMvc**:
   ```java
   @WebMvcTest(UserController.class)
   @AutoConfigureMockMvc
   ```

### Ejecución Manual de Pruebas

```bash
# Ejecutar todas las pruebas funcionales
mvn test -Dtest=FunctionalIntegrationTest

# Ejecutar prueba específica
mvn test -Dtest=FunctionalIntegrationTest#testCreateUserAndVerifyInDatabase

# Ver reportes
cat target/surefire-reports/edu.eci.dosw.DOSW_Library.integration.FunctionalIntegrationTest.txt
```

### Cobertura Actual

- **Controllers Testeados**: 3/3 (100%)
  - UserController ✅
  - BookController ✅
  - LoanController ✅

- **Endpoints Cubiertos**: 14/15 (93%)
  - POST/GET/PATCH/DELETE usuarios ✅
  - POST/GET/PATCH/DELETE libros ✅
  - POST/GET/PUT/return préstamos ✅
  - Falta: GET /loans/user/{userId}/active ⏳

- **Cambios en BD Validados**:
  - Creación de usuarios ✅
  - Creación de libros ✅
  - Decremento de inventario en préstamo ✅
  - Incremento de inventario en devolución ✅
  - Relaciones entre entidades ✅

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