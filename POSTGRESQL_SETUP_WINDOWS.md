# 📦 Guía de Instalación PostgreSQL - Windows

## Estado Actual del Proyecto

✅ **Lo que ya está listo:**
- Archivo `application.yaml` creado en `src/main/resources/`
- Configuración de PostgreSQL completa (solo falta la contraseña)
- Sección de setup práctico agregada al README
- Script de verificación incluido

❌ **Lo que necesita instalación manual:**
- PostgreSQL 15+ en tu máquina Windows
- Crear la base de datos `dosw_library_db`

---

## Paso 1: Descargar PostgreSQL

1. Ve a: **https://www.postgresql.org/download/windows/**
2. Haz clic en **Download the installer** (versión 15 o superior)
3. Elige la versión según tu sistema:
   - Windows x86-64 (recomendado para máquinas modernas)
   - Windows x86-32 (si tu sistema es 32 bits)

**Archivo esperado:**
```
PostgreSQL-15.x-1-windows-x64.exe  (aproximadamente 150 MB)
```

---

## Paso 2: Ejecutar el Instalador

1. Haz doble clic en el archivo `.exe`
2. Si pide permisos de administrador, haz clic en **Sí**
3. Sigue el wizard:

### En la pantalla "Setup - PostgreSQL"
- Elige ruta de instalación (por defecto está bien):
  ```
  C:\Program Files\PostgreSQL\15
  ```
- Haz clic **Next**

### En la pantalla "Select Components"
- ✅ Server (debe estar seleccionado)
- ✅ pgAdmin 4 (herramienta gráfica, RECOMENDADA)
- ✅ Command Line Tools
- ✅ Development Libraries
- Haz clic **Next**

### En la pantalla "Data Directory"
- Ruta por defecto está bien:
  ```
  C:\Program Files\PostgreSQL\15\data
  ```
- Haz clic **Next**

### ⚠️ PANTALLA IMPORTANTE: "Password"
**Aquí configuras la contraseña del usuario `postgres`:**

```
Password:  [ESCRIBE UNA CONTRASEÑA SEGURA]
Re-enter password: [REPITE LA MISMA]
```

**Requisitos:**
- Mínimo 8 caracteres
- Incluye mayúsculas, minúsculas, números
- Ejemplo: `Dosw.Library.2026!`

**⚠️ IMPORTANTE:** 
- 📝 **ANOTA ESTA CONTRASEÑA EN UN LUGAR SEGURO**
- La usarás en `application.yaml`
- Si olvidas la contraseña, deberás reinstalar PostgreSQL

- Haz clic **Next**

### En la pantalla "Port"
- Puerto por defecto: **5432** ← NO CAMBIES ESTO
- Haz clic **Next**

### En la pantalla "Locale"
- Elige tu idioma / región
- El default está bien
- Haz clic **Next**

### En la pantalla "Pre Installation Summary"
- Revisa que todo esté correcto
- Haz clic **Next**

### En la pantalla "Ready to Install"
- Haz clic **Install** y espera (toma 5-10 minutos)
- Cuando termine, desactiva "Launch Stack Builder"
- Haz clic **Finish**

---

## Paso 3: Verificar la Instalación

Abre **PowerShell** o **Command Prompt** y ejecuta:

```powershell
psql --version
```

**Resultado esperado:**
```
psql (PostgreSQL) 15.x
```

Si ves esto, la instalación fue exitosa ✅

---

## Paso 4: Crear la Base de Datos

Abre **PowerShell** y ejecuta:

```powershell
psql -U postgres
```

Te pedirá contrasña. **Ingresa la contraseña que configuraste.**

Deberías ver el prompt `postgres=#`

Ahora ejecuta los comandos SQL:

```sql
-- Crear la base de datos para DOSW Library
CREATE DATABASE dosw_library_db;

-- Verificar que se creó
\l

-- Salir
\q
```

**Resultado esperado en `\l`:**
```
Name              | Owner    | Encoding | Collate         | Ctype          
dosw_library_db   | postgres | UTF8     | en_US.UTF-8     | en_US.UTF-8
```

---

## Paso 5: Actualizar application.yaml con tu Contraseña

Abre en VS Code el archivo:
```
src/main/resources/application.yaml
```

Busca la línea:
```yaml
password: postgres123
```

Reemplázala con tu contraseña:
```yaml
password: Dosw.Library.2026!
```

**Ejemplo completo:**
```yaml
datasource:
  url: jdbc:postgresql://localhost:5432/dosw_library_db
  username: postgres
  password: Dosw.Library.2026!  ← TU CONTRASEÑA AQUÍ
  driverClassName: org.postgresql.Driver
```

---

## Paso 6: Ejecutar la Aplicación

En PowerShell, navega al directorio del proyecto:

```powershell
cd e:\DOSW\DOSW\DOSW-Library
```

Luego ejecuta:

```powershell
mvn clean spring-boot:run
```

**Espera a ver en la consola:**
```
Started DoswLibraryApplication in X.XXX seconds
HikariPool-1 - Init completed
Hibernate: CREATE TABLE users (...)
Hibernate: CREATE TABLE books (...)
Hibernate: CREATE TABLE loans (...)
```

Si ves esto, **¡TODO ESTÁ FUNCIONANDO! ✅**

---

## Paso 7: Verificar Acceso

Abre tu navegador y ve a:

```
http://localhost:8080/swagger-ui.html
```

Deberías ver la interfaz Swagger con todos los endpoints:
- POST /api/users
- POST /api/books
- POST /api/loans
- etc.

---

## 🔧 Solución de Problemas

### ❌ "psql not recognized"
```powershell
# PostgreSQL no está en el PATH
# Solución: Agrega manualmente a Path en Windows:
# 1. Panel de Control → Sistema → Variables de entorno
# 2. Busca "Path"
# 3. Agrega: C:\Program Files\PostgreSQL\15\bin
# 4. Reinicia PowerShell
```

### ❌ "Connection refused"
```powershell
# PostgreSQL no está corriendo
# Solución: Inicia el servicio de Windows
Get-Service postgresql-x64-15 | Start-Service

# O en Windows Services:
# 1. Presiona Win+R
# 2. Escribe: services.msc
# 3. Busca: postgresql-x64-15
# 4. Haz clic derecho → Start
```

### ❌ "password authentication failed"
- Verifica que la contraseña en `application.yaml` sea correcta
- Las contraseñas son **case-sensitive**
- Si olvidaste la contraseña:
  ```powershell
  # En PowerShell como administrador
  net stop postgresql-x64-15
  # Desinstala y reinstala PostgreSQL
  ```

### ❌ "database dosw_library_db does not exist"
```powershell
psql -U postgres
# Ingresa contraseña

# En la terminal psql:
CREATE DATABASE dosw_library_db;
\q
```

---

## ✅ Lista de Verificación Final

- [ ] PostgreSQL instalado (`psql --version` funciona)
- [ ] PostgreSQL corriendo (puedes conectarte con `psql -U postgres`)
- [ ] Base de datos `dosw_library_db` creada (`\l` la muestra)  
- [ ] `src/main/resources/application.yaml` existe y tiene tu contraseña
- [ ] Ejecutas `mvn clean spring-boot:run` sin errores
- [ ] Ves el mensaje "Hibernate: CREATE TABLE" en la consola
- [ ] Puedes acceder a http://localhost:8080/swagger-ui.html
- [ ] Pruebas un endpoint POST /api/users con éxito

---

## 🎉 ¡Listo!

Una vez completes estos pasos, tu aplicación DOSW-Library estará funcionando con:
- ✅ PostgreSQL como base de datos persistente
- ✅ Hibernate creando tablas automáticamente
- ✅ API REST con Swagger UI
- ✅ JWT para autenticación
- ✅ Validación de datos con Jakarta Validation

**Para pausar la ejecución:** Presiona `Ctrl + C` en PowerShell
