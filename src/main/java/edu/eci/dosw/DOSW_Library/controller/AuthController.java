package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.AuthResponse;
import edu.eci.dosw.DOSW_Library.controller.dto.LoginRequest;
import edu.eci.dosw.DOSW_Library.controller.dto.RegisterRequest;
import edu.eci.dosw.DOSW_Library.core.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y autorización.
 * 
 * <p>
 * <b>Endpoints disponibles:</b>
 * </p>
 * <ul>
 * <li>POST /auth/login - Autenticar usuario y obtener JWT</li>
 * <li>POST /auth/register - Registrar nuevo usuario (USER por defecto)</li>
 * <li>DELETE /auth/users/{userId} - Eliminar usuario (solo LIBRARIAN)</li>
 * </ul>
 * 
 * <p>
 * <b>Usuarios disponibles para testing:</b>
 * </p>
 * <ul>
 * <li><code>user / user1234</code> → Rol: USER (lectura básica)</li>
 * <li><code>admin / admin1234</code> → Rol: LIBRARIAN (permisos completos)</li>
 * </ul>
 * 
 * <p>
 * <b>Métodos de autenticación soportados:</b>
 * </p>
 * <ul>
 * <li>Username + Password → JWT Token (Bearer)</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con registro y eliminación de usuarios
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API para autenticación y autorización")
public class AuthController {

        private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

        private final AuthService authService;

        public AuthController(AuthService authService) {
                this.authService = authService;
        }

        /**
         * Autentica un usuario con sus credenciales (username + password).
         * 
         * <p>
         * <b>Proceso:</b>
         * </p>
         * <ol>
         * <li>Recibe credenciales (username, password)</li>
         * <li>Valida contra BD (usuarios en memoria)</li>
         * <li>Genera token JWT con roles incluidos en los claims</li>
         * <li>Retorna token + información del usuario</li>
         * </ol>
         * 
         * <p>
         * <b>Ejemplo 1: Login con usuario USER</b>
         * </p>
         * 
         * <pre>
         * POST /auth/login
         * Content-Type: application/json
         * 
         * {
         *   "username": "user",
         *   "password": "user1234"
         * }
         * 
         * Response 200 OK:
         * {
         *   "token": "eyJhbGciOiJIUzI1NiJ9...",
         *   "tokenType": "Bearer",
         *   "userId": "user",
         *   "username": "user",
         *   "role": "USER",
         *   "status": "ACTIVE",
         *   "expiresIn": 3600000
         * }
         * </pre>
         * 
         * <p>
         * <b>Ejemplo 2: Login con usuario LIBRARIAN</b>
         * </p>
         * 
         * <pre>
         * POST /auth/login
         * Content-Type: application/json
         * 
         * {
         *   "username": "admin",
         *   "password": "admin1234"
         * }
         * 
         * Response 200 OK:
         * {
         *   "token": "eyJhbGciOiJIUzI1NiJ9...",
         *   "tokenType": "Bearer",
         *   "userId": "admin",
         *   "username": "admin",
         *   "role": "LIBRARIAN",
         *   "status": "ACTIVE",
         *   "expiresIn": 3600000
         * }
         * </pre>
         * 
         * <p>
         * <b>Para llamadas posteriores, usar:</b>
         * </p>
         * 
         * <pre>
         * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         * </pre>
         * 
         * <p>
         * <b>Nota sobre el JWT:</b> El token contiene los roles del usuario en los
         * claims
         * bajo la clave "roles" para permitir verificación de autorización sin
         * consultar
         * a la BD en cada request.
         * </p>
         * 
         * @param loginRequest Objeto con credenciales (username, password)
         * @return ResponseEntity con AuthResponse (token + user info)
         * 
         * 
         * @throws org.springframework.security.authentication.BadCredentialsException
         *                                                                             Si
         *                                                                             credenciales
         *                                                                             son
         *                                                                             inválidas
         * @throws edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException
         *                                                                             Si
         *                                                                             usuario
         *                                                                             no
         *                                                                             existe
         *                                                                             en
         *                                                                             BD
         */
        @PostMapping("/login")
        @Operation(summary = "Autenticar usuario", description = "Autentica un usuario con username y password, retorna token JWT")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token generado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Credenciales inválidas o campos vacíos"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
                logger.info("Login attempt for user: {}", loginRequest.getUsername());

                try {
                        // Autentico y genero token
                        AuthResponse response = authService.authenticate(loginRequest);

                        logger.info("Successful login for user: {} | Role: {} | Status: {}",
                                        response.getUsername(), response.getRole(), response.getStatus());

                        return ResponseEntity
                                        .status(HttpStatus.OK)
                                        .body(response);

                } catch (org.springframework.security.authentication.BadCredentialsException e) {
                        logger.warn("Failed login attempt for user: {} - Bad credentials",
                                        loginRequest.getUsername());
                        return ResponseEntity
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .body(AuthResponse.builder()
                                                        .build());

                } catch (Exception e) {
                        logger.error("Error during login for user: {} | Error: {}",
                                        loginRequest.getUsername(), e.getMessage(), e);
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build();
                }
        }

        /**
         * Registra un nuevo usuario en el sistema.
         * 
         * <p>
         * <b>Proceso:</b>
         * </p>
         * <ol>
         * <li>Recibe datos de registro (username, password, rol, nombre, email)</li>
         * <li>Valida que el username no exista</li>
         * <li>Encripta la contraseña con BCrypt</li>
         * <li>Crea el usuario con rol USER por defecto (LIBRARIAN requiere
         * permisos)</li>
         * <li>Retorna información del usuario registrado</li>
         * </ol>
         * 
         * <p>
         * <b>Ejemplo: Registrar usuario USER</b>
         * </p>
         * 
         * <pre>
         * POST /auth/register
         * Content-Type: application/json
         * 
         * {
         *   "username": "newuser",
         *   "password": "securePassword123",
         *   "name": "John Doe",
         *   "email": "john@example.com"
         * }
         * 
         * Response 201 Created:
         * {
         *   "userId": "USR-1712605445123",
         *   "username": "newuser",
         *   "role": "USER",
         *   "status": "ACTIVE",
         *   "message": "User registered successfully. You can now login.",
         *   "expiresIn": 3600000
         * }
         * </pre>
         * 
         * <p>
         * <b>Ejemplo: Registrar usuario LIBRARIAN (solo si autenticado como
         * LIBRARIAN)</b>
         * </p>
         * 
         * <pre>
         * POST /auth/register
         * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         * Content-Type: application/json
         * 
         * {
         *   "username": "newlibrarian",
         *   "password": "securePassword123",
         *   "role": "LIBRARIAN",
         *   "name": "Jane Smith",
         *   "email": "jane@example.com"
         * }
         * 
         * Response 201 Created:
         * {
         *   "userId": "USR-1712605445124",
         *   "username": "newlibrarian",
         *   "role": "LIBRARIAN",
         *   "status": "ACTIVE",
         *   "message": "User registered successfully. You can now login.",
         *   "expiresIn": 3600000
         * }
         * </pre>
         */
        @PostMapping("/register")
        @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos o username duplicado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
                logger.info("Registration attempt for user: {} with role: {}",
                                registerRequest.getUsername(),
                                registerRequest.getRole() != null ? registerRequest.getRole() : "USER");

                try {
                        AuthResponse response = authService.register(registerRequest);

                        logger.info("User registered successfully: {} | Role: {}",
                                        response.getUsername(), response.getRole());

                        return ResponseEntity
                                        .status(HttpStatus.CREATED)
                                        .body(response);

                } catch (IllegalArgumentException e) {
                        logger.warn("Registration failed for user: {} - {}",
                                        registerRequest.getUsername(), e.getMessage());
                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .build();

                } catch (Exception e) {
                        logger.error("Error during registration for user: {} | Error: {}",
                                        registerRequest.getUsername(), e.getMessage(), e);
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build();
                }
        }

        /**
         * Elimina un usuario del sistema (solo LIBRARIAN).
         * 
         * <p>
         * <b>Proceso:</b>
         * </p>
         * <ol>
         * <li>Verifica que el usuario autenticado sea LIBRARIAN</li>
         * <li>Busca el usuario a eliminar</li>
         * <li>Elimina todos los registros asociados (préstamos, auditoría, etc.)</li>
         * <li>Retorna 204 No Content</li>
         * </ol>
         * 
         * <p>
         * <b>Ejemplo:</b>
         * </p>
         * 
         * <pre>
         * DELETE /auth/users/USR-001
         * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         * 
         * Response 204 No Content
         * </pre>
         */
        @DeleteMapping("/users/{userId}")
        @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema (solo LIBRARIAN)")
        @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Se requiere token Bearer"),
                        @ApiResponse(responseCode = "403", description = "Prohibido - Solo LIBRARIAN puede eliminar usuarios"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @org.springframework.security.access.prepost.PreAuthorize("hasRole('LIBRARIAN')")
        public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
                logger.info("Delete user attempt for user ID: {}", userId);

                try {
                        authService.deleteUser(userId);

                        logger.info("User deleted successfully: {}", userId);

                        return ResponseEntity
                                        .status(HttpStatus.NO_CONTENT)
                                        .build();

                } catch (edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException e) {
                        logger.warn("Delete failed - User not found: {}", userId);
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .build();

                } catch (Exception e) {
                        logger.error("Error deleting user: {} | Error: {}",
                                        userId, e.getMessage(), e);
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .build();
                }
        }
}
