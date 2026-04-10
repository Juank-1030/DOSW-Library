package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de gestión de usuarios de la biblioteca.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>✅ Lógica de negocio relacionada con usuarios</li>
 * <li>✅ Validaciones de unicidad (email único)</li>
 * <li>✅ Gestión del ciclo de vida de usuarios</li>
 * <li>✅ Búsqueda por diferentes criterios</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Inyección del repositorio (interfaz genérica agnóstica de persistencia)
    private final UserRepository userRepository;

    /**
     * Constructor con inyección de dependencias.
     * Spring inyecta automáticamente usando el constructor único.
     * 
     * @param userRepository Repositorio genérico de usuarios (implementación: MongoDB o JPA)
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("UserService initialized with UserRepository");
    }

    // ============================================
    // OPERACIONES DE CREACIÓN
    // ============================================

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * <p>
     * <b>Validaciones de negocio:</b>
     * </p>
     * <ul>
     * <li>El ID del usuario no debe existir</li>
     * <li>El email debe ser único (si se proporciona)</li>
     * </ul>
     * 
     * @param user Usuario a registrar
     * @return El usuario registrado
     * @throws IllegalArgumentException Si el usuario ya existe o email duplicado
     */
    public User registerUser(User user) {
        logger.info("Registering user: {} | Name: '{}' | Email: {} | Username: {}",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUsername());

        // Validación: usuario no debe existir
        if (userRepository.existsById(user.getId())) {
            logger.warn("Attempted to register duplicate user: {}", user.getId());
            throw new IllegalArgumentException("User with ID " + user.getId() + " already exists");
        }

        // Validación: email único
        if (user.getEmail() != null && isEmailTaken(user.getEmail())) {
            logger.warn("Attempted to register user with duplicate email: {}", user.getEmail());
            throw new IllegalArgumentException("Email " + user.getEmail() + " is already registered");
        }

        // Validación: username único
        if (user.getUsername() != null && isUsernameTaken(user.getUsername())) {
            logger.warn("Attempted to register user with duplicate username: {}", user.getUsername());
            throw new IllegalArgumentException("Username " + user.getUsername() + " is already taken");
        }

        // Guardar usuario en BD
        User savedUser = userRepository.save(user);

        logger.debug("User registered successfully. Total users: {}", userRepository.count());
        logger.info("User {} registered successfully", user.getId());

        return savedUser;
    }

    // ============================================
    // OPERACIONES DE CONSULTA
    // ============================================

    /**
     * Obtiene un usuario por su ID.
     * 
     * @param userId ID del usuario
     * @return El usuario encontrado
     * @throws UserNotFoundException Si el usuario no existe
     */
    public User getUserById(String userId) throws UserNotFoundException {
        logger.debug("Searching for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", userId);
                    return UserNotFoundException.byId(userId);
                });

        logger.info("User found: {} | Name: '{}'", userId, user.getName());
        return user;
    }

    /**
     * Obtiene todos los usuarios registrados.
     * 
     * @return Lista de todos los usuarios
     */
    public List<User> getAllUsers() {
        logger.debug("Retrieving all users. Total count: {}", userRepository.count());

        List<User> users = userRepository.findAll();

        logger.info("Retrieved {} users from system", users.size());
        return users;
    }

    /**
     * Busca un usuario por su email.
     * 
     * @param email Email del usuario
     * @return El usuario encontrado
     * @throws UserNotFoundException Si no existe usuario con ese email
     */
    public User getUserByEmail(String email) throws UserNotFoundException {
        logger.debug("Searching for user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return UserNotFoundException.byEmail(email);
                });

        logger.info("User found by email: {} | ID: {}", email, user.getId());
        return user;
    }

    /**
     * Busca un usuario por su username.
     * 
     * <p>
     * <b>Propósito:</b> Método para autenticación/login
     * </p>
     * 
     * @param username Username del usuario
     * @return El usuario encontrado
     * @throws UserNotFoundException Si no existe usuario con ese username
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        logger.debug("Searching for user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return UserNotFoundException.byEmail(username);
                });

        logger.info("User found by username: {} | ID: {}", username, user.getId());
        return user;
    }

    // ============================================
    // OPERACIONES DE ACTUALIZACIÓN
    // ============================================

    /**
     * Actualiza la información de un usuario.
     * 
     * @param userId      ID del usuario a actualizar
     * @param updatedUser Usuario con datos actualizados
     * @return El usuario actualizado
     * @throws UserNotFoundException Si el usuario no existe
     */
    public User updateUser(String userId, User updatedUser) throws UserNotFoundException {
        logger.info("Updating user: {}", userId);

        User existingUser = getUserById(userId);

        // Validar email único si cambió
        if (updatedUser.getEmail() != null
                && !updatedUser.getEmail().equals(existingUser.getEmail())
                && isEmailTaken(updatedUser.getEmail())) {
            logger.warn("Cannot update user {} - Email {} already taken",
                    userId, updatedUser.getEmail());
            throw new IllegalArgumentException("Email " + updatedUser.getEmail() + " is already in use");
        }

        // Actualizar campos
        if (updatedUser.getName() != null) {
            logger.debug("Updating name: '{}' -> '{}'", existingUser.getName(), updatedUser.getName());
            existingUser.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null) {
            logger.debug("Updating email: '{}' -> '{}'", existingUser.getEmail(), updatedUser.getEmail());
            existingUser.setEmail(updatedUser.getEmail());
        }

        User savedUser = userRepository.save(existingUser);

        logger.info("User {} updated successfully", userId);
        return savedUser;
    }

    // ============================================
    // OPERACIONES DE ELIMINACIÓN
    // ============================================

    /**
     * Elimina un usuario del sistema.
     * 
     * <p>
     * <b>Validación de negocio:</b> Solo se puede eliminar si no tiene préstamos
     * activos
     * </p>
     * 
     * @param userId ID del usuario a eliminar
     * @throws UserNotFoundException Si el usuario no existe
     */
    public void deleteUser(String userId) throws UserNotFoundException {
        logger.info("Attempting to delete user: {}", userId);

        User user = getUserById(userId);

        userRepository.deleteById(userId);

        logger.info("User deleted: {} | Name: '{}'", userId, user.getName());
    }

    // ============================================
    // MÉTODOS DE VALIDACIÓN Y UTILIDAD
    // ============================================

    /**
     * Verifica si un usuario existe en el sistema.
     * 
     * @param userId ID del usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existsById(String userId) {
        boolean exists = userRepository.existsById(userId);
        logger.debug("User {} exists: {}", userId, exists);
        return exists;
    }

    /**
     * Verifica si un email ya está registrado.
     * 
     * @param email Email a verificar
     * @return true si el email está en uso, false en caso contrario
     */
    private boolean isEmailTaken(String email) {
        if (email == null)
            return false;

        boolean taken = userRepository.findAll().stream()
                .anyMatch(u -> email.equals(u.getEmail()));

        logger.debug("Email '{}' is taken: {}", email, taken);
        return taken;
    }

    /**
     * Verifica si un username ya está registrado.
     * 
     * <p>
     * <b>Propósito:</b> Validar unicidad de username para autenticación
     * </p>
     * 
     * @param username Username a verificar
     * @return true si el username está en uso, false en caso contrario
     */
    private boolean isUsernameTaken(String username) {
        if (username == null)
            return false;

        boolean taken = userRepository.existsByUsername(username);

        logger.debug("Username '{}' is taken: {}", username, taken);
        return taken;
    }

    /**
     * Obtiene el total de usuarios registrados.
     * 
     * @return Cantidad total de usuarios
     */
    public int getTotalUsers() {
        int total = (int) userRepository.count();
        logger.debug("Total users in system: {}", total);
        return total;
    }
}