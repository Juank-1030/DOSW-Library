

---

## 🆕 Endpoints de Autenticación Completos

### 1️⃣ POST /auth/login - Obtener Token JWT

**Propósito:** Autenticar un usuario y obtener un token JWT

**Request:**
```bash
POST https://localhost:8443/auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "user1234"
}
```

**Response 201 Created:**
```json
{
  "token": "eyJhbGciOiJIUzUx...",
  "tokenType": "Bearer",
  "userId": "user",
  "username": "user",
  "role": "USER",
  "status": "ACTIVE",
  "expiresIn": 3600000
}
```

---

### 2️⃣ POST /auth/register - Registrar Nuevo Usuario

**Propósito:** Crear nueva cuenta de usuario

**Request (Usuario USER):**
```bash
POST https://localhost:8443/auth/register
Content-Type: application/json

{
  "username": "newuser123",
  "password": "SecurePassword456!",
  "name": "John Doe",
  "email": "john@example.com"
}
```

**Response 201 Created:**
```json
{
  "userId": "USR-1712605445123",
  "username": "newuser123",
  "role": "USER",
  "message": "User registered successfully. You can now login."
}
```

---

### 3️⃣ DELETE /auth/users/{userId} - Eliminar Usuario

**Propósito:** Eliminar usuario del sistema (solo LIBRARIAN)

**Request:**
```bash
DELETE https://localhost:8443/auth/users/USR-001
Authorization: Bearer <token_librarian>
```

**Response 204 No Content** (exitoso, sin body)

**En Swagger UI:**
1. Click "Authorize" (🔓) en esquina superior derecha
2. Pega token LIBRARIAN obtenido de POST /auth/login
3. Busca DELETE /auth/users/{userId}
4. Click "Try it out"
5. Ingresa userId (ej: USR-001)
6. Click "Execute"
7. Verifica HTTP 204 No Content

---

## 📊 Resumen de Endpoints de Autenticación

| Endpoint | Método | Autenticación | Descripción |
|----------|--------|---------------|------------|
| /auth/login | POST | Pública ✅ | Obtener token JWT |
| /auth/register | POST | Pública ✅ | Registrar nuevo usuario |
| /auth/users/{userId} | DELETE | LIBRARIAN 🔒 | Eliminar usuario |

**Total:** 3 endpoints - 2 públicos + 1 protegido

