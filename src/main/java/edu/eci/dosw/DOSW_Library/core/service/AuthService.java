package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.controller.dto.AuthResponse;
import edu.eci.dosw.DOSW_Library.controller.dto.LoginRequest;
import edu.eci.dosw.DOSW_Library.controller.dto.RegisterRequest;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar la autenticación de usuarios.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Validar credenciales (username + password)</li>
 * <li>Generar tokens JWT después de autenticación exitosa</li>
 * <li>Proporcionar información del usuario autenticado</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthService {

        private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        /**
         * Autentica un usuario con sus credenciales y devuelve un token JWT.
         * 
         * @param loginRequest Credenciales (username + password)
         * @return AuthResponse con el token JWT y datos del usuario
         * @throws BadCredentialsException Si las credenciales son inválidas
         */
        public AuthResponse authenticate(LoginRequest loginRequest) {
                logger.info("Attempting authentication for user: {}", loginRequest.getUsername());

                try {
                        // ✅ PASO 1: Validar credenciales contra AuthenticationManager
                        // AuthenticationManager compara el password con BCrypt
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        loginRequest.getUsername(),
                                                        loginRequest.getPassword()));

                        logger.info("Authentication successful for user: {}", loginRequest.getUsername());

                        // ✅ PASO 2: Obtener UserDetails del usuario autenticado
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                        // ✅ PASO 3: Generar token JWT (Header.Payload.Signature)
                        String token = jwtService.generateToken(userDetails);

                        logger.info("JWT token generated for user: {} with {} authorities",
                                        userDetails.getUsername(), userDetails.getAuthorities());

                        // ✅ PASO 4: Obtener tiempo de expiración
                        Long expirationMs = jwtService.getExpirationTime();

                        // ✅ PASO 5: Retornar AuthResponse con token + información del usuario
                        return AuthResponse.builder()
                                        .token(token)
                                        .tokenType("Bearer")
                                        .userId(userDetails.getUsername())
                                        .username(userDetails.getUsername())
                                        .role(extractRoleFromAuthorities(userDetails))
                                        .status("ACTIVE") // Estado por defecto para usuarios autenticados
                                        .expiresIn(expirationMs)
                                        .build();

                } catch (BadCredentialsException e) {
                        logger.warn("Authentication failed for user: {} - Invalid credentials",
                                        loginRequest.getUsername());
                        throw new BadCredentialsException("Credenciales inválidas");
                } catch (Exception e) {
                        logger.error("Unexpected error during authentication for user: {}",
                                        loginRequest.getUsername(), e);
                        throw new RuntimeException("Error durante la autenticación: " + e.getMessage());
                }
        }

        /**
         * Registra un nuevo usuario en el sistema.
         * 
         * @param registerRequest Datos de registro (username, password, rol, nombre,
         *                        email)
         * @return AuthResponse con información del usuario registrado
         * @throws IllegalArgumentException Si el usuario o email ya existe
         */
        public AuthResponse register(RegisterRequest registerRequest) {
                logger.info("Registering new user: {} with role: {}",
                                registerRequest.getUsername(),
                                registerRequest.getRole() != null ? registerRequest.getRole() : "USER");

                // Validar que el username no exista
                if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                        logger.warn("Attempted to register duplicate username: {}", registerRequest.getUsername());
                        throw new IllegalArgumentException("Username already exists");
                }

                // Determinar el rol (USER por defecto)
                String role = registerRequest.getRole() != null ? registerRequest.getRole() : "USER";

                // Crear nuevo usuario
                User newUser = User.builder()
                                .id("USR-" + System.currentTimeMillis()) // ID único basado en timestamp
                                .username(registerRequest.getUsername())
                                .password(passwordEncoder.encode(registerRequest.getPassword())) // Encriptar contraseña
                                .role(role)
                                .name(registerRequest.getName() != null ? registerRequest.getName()
                                                : registerRequest.getUsername())
                                .email(registerRequest.getEmail())
                                .build();

                // Guardar usuario
                User savedUser = userRepository.save(newUser);

                logger.info("User registered successfully: {} with role: {}",
                                savedUser.getUsername(), savedUser.getRole());

                // Retornar AuthResponse con datos del nuevo usuario
                return AuthResponse.builder()
                                .userId(savedUser.getId())
                                .username(savedUser.getUsername())
                                .role(savedUser.getRole())
                                .status("ACTIVE")
                                .expiresIn(jwtService.getExpirationTime())
                                .message("User registered successfully. You can now login.")
                                .build();
        }

        /**
         * Elimina un usuario del sistema.
         * 
         * @param userId ID del usuario a eliminar
         * @throws UserNotFoundException Si el usuario no existe
         */
        public void deleteUser(String userId) throws UserNotFoundException {
                logger.info("Deleting user: {}", userId);

                // Verificar que el usuario existe
                if (!userRepository.existsById(userId)) {
                        logger.warn("Attempted to delete non-existent user: {}", userId);
                        throw new UserNotFoundException("User with ID " + userId + " not found");
                }

                // Eliminar usuario
                userRepository.deleteById(userId);

                logger.info("User deleted successfully: {}", userId);
        }

        /**
         * Extrae el rol (role) del usuario a partir de sus autoridades de Spring
         * Security.
         * Las autoridades vienen con prefijo "ROLE_" desde SecurityUserDetailsService.
         * 
         * @param userDetails UserDetails con autoridades
         * @return Nombre del rol (USER, ADMIN, LIBRARIAN, etc.)
         */
        private String extractRoleFromAuthorities(UserDetails userDetails) {
                return userDetails.getAuthorities()
                                .stream()
                                .map(ga -> ga.getAuthority().replace("ROLE_", ""))
                                .findFirst()
                                .orElse("USER"); // Rol por defecto
        }
}
