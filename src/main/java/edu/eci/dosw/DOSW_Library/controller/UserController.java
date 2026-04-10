package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.CreateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UpdateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.persistence.relational.mapper.UserPersistenceMapper;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de usuarios de la biblioteca.
 * 
 * <p>
 * <b>Endpoints disponibles:</b>
 * </p>
 * <ul>
 * <li>POST /api/users - Registrar usuario</li>
 * <li>GET /api/users - Listar todos los usuarios</li>
 * <li>GET /api/users/{id} - Obtener usuario por ID</li>
 * <li>PATCH /api/users/{id} - Actualizar usuario</li>
 * <li>DELETE /api/users/{id} - Eliminar usuario</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API para gestión de usuarios de la biblioteca")
public class UserController {

        private static final Logger logger = LoggerFactory.getLogger(UserController.class);

        private final UserService userService;
        private final UserPersistenceMapper userMapper;

        public UserController(UserService userService, UserPersistenceMapper userMapper) {
                this.userService = userService;
                this.userMapper = userMapper;
                logger.info("UserController initialized");
        }

        // ============================================
        // POST - REGISTRAR USUARIO
        // ============================================

        /**
         * Registra un nuevo usuario en el sistema.
         * 
         * <p>
         * <b>AUTORIZACIÓN:</b> Solo BIBLIOTECARIOS pueden registrar usuarios
         * </p>
         * 
         * <p>
         * <b>Endpoint:</b> POST /api/users
         * </p>
         * 
         * <p>
         * <b>Ejemplo de request:</b>
         * </p>
         * 
         * <pre>
         * POST /api/users
         * Authorization: Bearer {token}
         * {
         *   "id": "USR-001",
         *   "name": "John Doe",
         *   "email": "john.doe@example.com"
         * }
         * </pre>
         * 
         * @param createDTO DTO con datos del usuario
         * @return ResponseEntity con UserDTO y código 201 CREATED
         */
        @PostMapping
        @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema de biblioteca (solo BIBLIOTECARIO)")
        @SecurityRequirement(name = "Bearer Authentication")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere rol LIBRARIAN)"),
                        @ApiResponse(responseCode = "409", description = "Usuario o email ya existe")
        })
        public ResponseEntity<UserDTO> createUser(
                        @Valid @RequestBody CreateUserDTO createDTO) {

                logger.info("POST /api/users - Creating user: {}", createDTO.getId());
                logger.debug("Request body: {}", createDTO);

                // DTO → Entity
                User user = userMapper.toEntity(createDTO);

                // Service (lógica de negocio)
                User createdUser = userService.registerUser(user);

                // Entity → DTO
                UserDTO responseDTO = userMapper.toDTO(createdUser);

                logger.info("User created successfully: {} | Response: {}",
                                createdUser.getId(),
                                HttpStatus.CREATED);

                return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }

        // ============================================
        // GET - OBTENER TODOS LOS USUARIOS
        // ============================================

        @GetMapping
        @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista completa de usuarios registrados (solo BIBLIOTECARIO)")
        @SecurityRequirement(name = "Bearer Authentication")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere rol LIBRARIAN)")
        })
        public ResponseEntity<List<UserDTO>> getAllUsers() {
                logger.info("GET /api/users - Retrieving all users");

                List<User> users = userService.getAllUsers();
                List<UserDTO> responseDTOs = userMapper.toDTOList(users);

                logger.info("Retrieved {} users | Response: {}",
                                responseDTOs.size(),
                                HttpStatus.OK);

                return ResponseEntity.ok(responseDTOs);
        }

        // ============================================
        // GET - OBTENER USUARIO POR ID
        // ============================================

        @GetMapping("/{id}")
        @PreAuthorize("hasRole('LIBRARIAN') or @userSecurityService.isOwner(#id, authentication)")
        @Operation(summary = "Obtener usuario por ID", description = "Busca y retorna un usuario específico (solo sí mismo o BIBLIOTECARIO)")
        @SecurityRequirement(name = "Bearer Authentication")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para ver este usuario"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        public ResponseEntity<UserDTO> getUserById(
                        @Parameter(description = "ID del usuario", example = "USR-001") @PathVariable String id,
                        Authentication authentication)
                        throws UserNotFoundException {

                logger.info("GET /api/users/{} - Retrieving user", id);

                User user = userService.getUserById(id);
                UserDTO responseDTO = userMapper.toDTO(user);

                logger.info("User {} found | Response: {}", id, HttpStatus.OK);

                return ResponseEntity.ok(responseDTO);
        }

        // ============================================
        // PATCH - ACTUALIZAR USUARIO
        // ============================================

        /**
         * Actualiza información de un usuario existente.
         * 
         * <p>
         * <b>AUTORIZACIÓN:</b> Solo BIBLIOTECARIOS o el usuario sí mismo puede
         * actualizar
         * </p>
         * 
         * <p>
         * <b>Endpoint:</b> PATCH /api/users/{id}
         * </p>
         * 
         * <p>
         * <b>Ejemplo de request:</b>
         * </p>
         * 
         * <pre>
         * PATCH /api/users/USR-001
         * Authorization: Bearer {token}
         * {
         *   "name": "Jane Doe",
         *   "email": "jane.doe@example.com"
         * }
         * </pre>
         * 
         * @param id        ID del usuario
         * @param updateDTO DTO con campos a actualizar
         * @return ResponseEntity con UserDTO actualizado
         */
        @PatchMapping("/{id}")
        @PreAuthorize("hasRole('LIBRARIAN') or @userSecurityService.isOwner(#id, authentication)")
        @Operation(summary = "Actualizar usuario", description = "Modifica nombre y/o email de un usuario (solo sí mismo o BIBLIOTECARIO)")
        @SecurityRequirement(name = "Bearer Authentication")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para actualizar este usuario"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @ApiResponse(responseCode = "409", description = "Email ya está en uso")
        })
        public ResponseEntity<UserDTO> updateUser(
                        @Parameter(description = "ID del usuario", example = "USR-001") @PathVariable String id,
                        @Valid @RequestBody UpdateUserDTO updateDTO,
                        Authentication authentication) throws UserNotFoundException {

                logger.info("PATCH /api/users/{} - Updating user", id);
                logger.debug("Request body: {}", updateDTO);

                // Obtener usuario
                User user = userService.getUserById(id);

                // Aplicar actualizaciones
                userMapper.updateEntity(user, updateDTO);

                // Guardar cambios a través del service
                User updatedUser = userService.updateUser(id, user);

                // Convertir a DTO
                UserDTO responseDTO = userMapper.toDTO(updatedUser);

                logger.info("User {} updated successfully | Response: {}", id, HttpStatus.OK);

                return ResponseEntity.ok(responseDTO);
        }

        // ============================================
        // DELETE - ELIMINAR USUARIO
        // ============================================

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema (solo BIBLIOTECARIO)")
        @SecurityRequirement(name = "Bearer Authentication")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere rol LIBRARIAN)"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @ApiResponse(responseCode = "409", description = "No se puede eliminar - tiene préstamos activos")
        })
        public ResponseEntity<Void> deleteUser(
                        @Parameter(description = "ID del usuario", example = "USR-001") @PathVariable String id)
                        throws UserNotFoundException {

                logger.info("DELETE /api/users/{} - Deleting user", id);

                userService.deleteUser(id);

                logger.info("User {} deleted successfully | Response: {}", id, HttpStatus.NO_CONTENT);

                return ResponseEntity.noContent().build();
        }
}