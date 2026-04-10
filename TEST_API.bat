@echo off
REM Script de Prueba de DOSW-Library API
REM Requiere: curl, jq (opcional), MongoDB corriendo

setlocal enabledelayedexpansion

echo.
echo ============================================
echo  DOSW-Library API - Script de Prueba
echo ============================================
echo.

REM Configuración
set BASE_URL=http://localhost:8080
set USERNAME=admin
set PASSWORD=admin1234

echo [1/6] PASO 1: Hacer Login...
echo.

REM Hacer login
for /f "tokens=*" %%a in ('curl -s -X POST %BASE_URL%/auth/login -H "Content-Type: application/json" -d "{\"username\":\"%USERNAME%\",\"password\":\"%PASSWORD%\"}" 2^>nul') do (
    set RESPONSE=%%a
)

echo Response: !RESPONSE!
echo.

REM Extraer token (nota: esto es simplificado, requeriría jq en producción)
REM En Windows CMD es complicado parsear JSON, por eso mostramos el response completo
echo [2/6] PASO 2: Extracto del token (ver arriba en "token")
echo Guardaremos token manualmente para siguientes pruebas
echo.

echo [3/6] PASO 3: Crear Libro (ejemplo con token placeholder)
echo.
echo Para crear un libro, usa:
echo.
echo TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
echo curl -X POST %BASE_URL%/api/books ^
echo   -H "Authorization: Bearer !TOKEN!" ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\",\"isbn\":\"978-0132350884\",\"quantity\":5}"
echo.

echo [4/6] PASO 4: Listar Libros (sin token, público)
echo.
curl -s -X GET %BASE_URL%/api/books -H "Content-Type: application/json"
echo.
echo.

echo [5/6] PASO 5: Verificar MongoDB
echo.
echo Para verificar en MongoDB, ejecuta:
echo.
echo mongosh
echo use dosw_library_db
echo db.users.find().pretty()
echo db.books.find().pretty()
echo.

echo [6/6] PASO 6: Prueba de Error 401 (sin token)
echo.
curl -s -X POST %BASE_URL%/api/books ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Test\"}"
echo.
echo.

echo ============================================
echo  Pruebas Completadas
echo ============================================
echo.
echo Documentación: Ver RUN_APP.md
echo.

endlocal
pause
