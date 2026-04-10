# Guía para Ejecutar DOSW-Library con MongoDB

## 📋 Requisitos Previos

1. **MongoDB Running:** Asegurar que MongoDB local está ejecutando en `localhost:27017`
   ```bash
   # Windows: Inicia MongoDB (si está instalado)
   mongod
   
   # O usa Docker
   docker run -d -p 27017:27017 --name mongodb mongo:latest
   ```

2. **Java 21 LTS:** Verificar que esté instalado
   ```bash
   java -version
   ```

3. **Maven:** Para compilar y ejecutar
   ```bash
   mvn -version
   ```

## 🚀 Paso 1: Compilar el Proyecto

```bash
cd e:\DOSW\DOSW\DOSW-Library
mvn clean compile
```

**Resultado esperado:** `BUILD SUCCESS` (0 errores)

## 🚀 Paso 2: Ejecutar la Aplicación con Perfil MongoDB

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mongo"
```

**Logs esperados:**
```
The following profiles are active: mongo
Started DoswLibraryApplication in X.XXX seconds
Swagger UI available at: http://localhost:8080/swagger-ui.html
```

## 🧪 Paso 3: Hacer Login (Obtener JWT Token)

**Request:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin1234"
  }'
```

**Response esperado (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": "admin",
  "username": "admin",
  "role": "LIBRARIAN",
  "status": "ACTIVE",
  "expiresIn": 3600000
}
```

## 📊 Paso 4: Verificar Datos en MongoDB

Conectar a MongoDB y verificar que se crearon usuarios (credenciales de seguridad):

```bash
# Abrir MongoDB shell
mongosh
```

```javascript
// Cambiar a base de datos
use dosw_library_db

// Ver usuarios creados
db.users.find().pretty()

// Ver estructura del documento User
db.users.findOne()
```

**Documento esperado en MongoDB:**
```json
{
  "_id": ObjectId("..."),
  "id": "admin",
  "name": "Administrator",
  "email": "admin@dosw.edu.co",
  "username": "admin",
  "passwordHash": "$2a$10$...", // Contraseña hasheada con BCrypt
  "role": "LIBRARIAN",
  "status": "ACTIVE",
  "createdAt": ISODate("2026-04-10T..."),
  "updatedAt": ISODate("2026-04-10T...")
}
```

## 📚 Paso 5: Crear un Libro (Usando Token JWT)

**Request:**
```bash
# Guardar el token del login
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Crear libro como LIBRARIAN
curl -X POST http://localhost:8080/api/books \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "978-0132350884",
    "quantity": 5
  }'
```

**Response esperado (201 Created):**
```json
{
  "id": "BK-001",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "quantity": 5,
  "available": 5,
  "createdAt": "2026-04-10T15:30:45"
}
```

## 📖 Paso 6: Verificar Libro en MongoDB

```javascript
// Ver libros creados
db.books.find().pretty()

// Ver un libro específico
db.books.findOne({ "isbn": "978-0132350884" })
```

**Documento esperado:**
```json
{
  "_id": ObjectId("..."),
  "id": "BK-001",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "quantity": 5,
  "available": 5,
  "createdAt": ISODate("2026-04-10T15:30:45.000Z"),
  "updatedAt": ISODate("2026-04-10T15:30:45.000Z")
}
```

## 🧑‍💼 Paso 7: Crear Préstamo

**Request:**
```bash
# Crear un préstamo
curl -X POST http://localhost:8080/api/loans \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "admin",
    "bookId": "BK-001"
  }'
```

**Response esperado (201 Created):**
```json
{
  "id": "LOAN-001",
  "user": {
    "id": "admin",
    "name": "Administrator",
    "email": "admin@dosw.edu.co"
  },
  "book": {
    "id": "BK-001",
    "title": "Clean Code",
    "author": "Robert C. Martin"
  },
  "loanDate": "2026-04-10T15:30:45",
  "dueDate": "2026-05-08T15:30:45",
  "status": "ACTIVE"
}
```

## 📊 Paso 8: Verificar Préstamo en MongoDB

```javascript
// Ver préstamos
db.loans.find().pretty()

// Ver un préstamo específico con datos embebidos
db.loans.findOne({ "id": "LOAN-001" })
```

**Documento esperado (Estrategia HÍBRIDA):**
```json
{
  "_id": ObjectId("..."),
  "id": "LOAN-001",
  "userId": "admin",
  "bookId": "BK-001",
  "loanDate": ISODate("2026-04-10T15:30:45.000Z"),
  "dueDate": ISODate("2026-05-08T15:30:45.000Z"),
  "returnDate": null,
  "status": "ACTIVE",
  "createdAt": ISODate("2026-04-10T15:30:45.000Z"),
  "updatedAt": ISODate("2026-04-10T15:30:45.000Z"),
  "history": []
}
```

## 🔓 Paso 9: Error 401 - Sin Token

**Request:**
```bash
# Intentar crear libro SIN token
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title": "Test"}'
```

**Response esperado (401 Unauthorized):**
```json
{
  "error": "Full authentication is required to access this resource",
  "timestamp": "2026-04-10T15:35:00.000Z",
  "status": 401
}
```

## 🔐 Paso 10: Error 403 - Token Válido pero Rol Insuficiente

Si crearamos un usuario con rol USER e intentamos crear un libro:

```bash
# User intenta POST /api/books (solo LIBRARIAN puede)
curl -X POST http://localhost:8080/api/books \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "Test"}'
```

**Response esperado (403 Forbidden):**
```json
{
  "error": "Access Denied",
  "timestamp": "2026-04-10T15:36:00.000Z",
  "status": 403
}
```

## 📋 Verificación de Perfiles

### Verificar que MongoDB Profile está Activo

Ver en los logs:
```
The following profiles are active: mongo
Detected MongoDB auto-index creation enabled
```

### Cambiar a PostgreSQL

Si quieres probar con PostgreSQL:

1. Modifica `application.yaml`:
   ```yaml
   spring:
     profiles:
       active: relational  # Cambiar de 'mongo' a 'relational'
   ```

2. Reinicia la aplicación:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=relational"
   ```

## ✅ Checklist de Verificación

- [ ] MongoDB está ejecutando en localhost:27017
- [ ] Compilación exitosa (`mvn clean compile`)
- [ ] Aplicación inicia sin errores
- [ ] Login exitoso (retorna JWT token)
- [ ] Token contiene rol y username
- [ ] Datos de usuarios en MongoDB con contraseña hasheada
- [ ] Crear libro exitoso (con token LIBRARIAN)
- [ ] Datos de libros en MongoDB
- [ ] Crear préstamo exitoso
- [ ] Datos de préstamos en MongoDB con estrategia HÍBRIDA
- [ ] Error 401 sin token
- [ ] Error 403 con token pero rol insuficiente
- [ ] Perfil 'mongo' activo en logs

## 🛠️ Troubleshooting

### Error: "connect ECONNREFUSED 127.0.0.1:27017"
→ MongoDB no está ejecutando. Inicia: `mongod`

### Error: "No matching Spring Data modules found"
→ Perfil no está activado. Verifica `application.yaml`

### Error: "Cannot resolve symbol 'LoanRepositoryMongoImpl'"
→ Recompila: `mvn clean compile`

### Token no se genera
→ Verifica que `security.jwt.secret` esté en `application.properties`

---

**Documentado:** 10 de Abril 2026
**Versión:** 1.0 - MongoDB Profile
