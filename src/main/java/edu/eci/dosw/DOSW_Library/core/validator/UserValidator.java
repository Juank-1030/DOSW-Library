package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador de lógica de negocio para usuarios.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>✅ Validar formato de datos de usuario</li>
 * <li>✅ Validar formato de email</li>
 * <li>✅ Validar longitud de campos</li>
 * <li>✅ Logging de validaciones</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Component
public class UserValidator {

    private static final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    /**
     * Longitud mínima permitida para el ID de usuario.
     */
    private static final int MIN_ID_LENGTH = 3;

    /**
     * Longitud máxima permitida para el ID de usuario.
     */
    private static final int MAX_ID_LENGTH = 20;

    /**
     * Longitud mínima permitida para el nombre.
     */
    private static final int MIN_NAME_LENGTH = 1;

    /**
     * Longitud máxima permitida para el nombre.
     */
    private static final int MAX_NAME_LENGTH = 100;

    /**
     * Patrón regex para validar email.
     * Formato: usuario@dominio.extension
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Longitud mínima permitida para username.
     */
    private static final int MIN_USERNAME_LENGTH = 3;

    /**
     * Longitud máxima permitida para username.
     */
    private static final int MAX_USERNAME_LENGTH = 50;

    /**
     * Longitud mínima permitida para password.
     */
    private static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Longitud máxima permitida para password.
     */
    private static final int MAX_PASSWORD_LENGTH = 255;

    // ============================================
    // VALIDACIÓN COMPLETA DE USUARIO
    // ============================================

    /**
     * Valida todos los aspectos de un usuario.
     * 
     * <p>
     * <b>Validaciones aplicadas:</b>
     * </p>
     * <ul>
     * <li>ID no puede ser nulo o vacío</li>
     * <li>ID debe tener longitud entre 3 y 20 caracteres</li>
     * <li>ID no debe contener espacios</li>
     * <li>Nombre no puede ser nulo o vacío</li>
     * <li>Nombre debe tener longitud entre 1 y 100 caracteres</li>
     * <li>Email debe tener formato válido (si está presente)</li>
     * <li>Username debe tener longitud entre 3 y 50 caracteres (si está
     * presente)</li>
     * <li>Password debe tener longitud entre 6 y 255 caracteres (si está
     * presente)</li>
     * <li>Role debe ser válido: USER, LIBRARIAN (si está presente)</li>
     * </ul>
     * 
     * @param user Usuario a validar
     * @return Lista de mensajes de error (vacía si no hay errores)
     */
    public List<String> validate(User user) {
        logger.debug("Validating user: {}", user != null ? user.getId() : "null");

        List<String> errors = new ArrayList<>();

        if (user == null) {
            logger.warn("User is null - validation failed");
            errors.add("User cannot be null");
            return errors;
        }

        // Validar ID
        errors.addAll(validateId(user.getId()));

        // Validar nombre
        errors.addAll(validateName(user.getName()));

        // Validar email (si está presente)
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            errors.addAll(validateEmail(user.getEmail()));
        }

        // Validar username (si está presente)
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            errors.addAll(validateUsername(user.getUsername()));
        }

        // Validar password (si está presente)
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            errors.addAll(validatePassword(user.getPassword()));
        }

        // Validar role (si está presente)
        if (user.getRole() != null) {
            errors.addAll(validateRole(user.getRole()));
        }

        if (errors.isEmpty()) {
            logger.debug("User {} validated successfully", user.getId());
        } else {
            logger.warn("User {} validation failed with {} errors: {}",
                    user.getId(),
                    errors.size(),
                    errors);
        }

        return errors;
    }

    // ============================================
    // VALIDACIONES INDIVIDUALES
    // ============================================

    /**
     * Valida el ID del usuario.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser nulo</li>
     * <li>No puede estar vacío</li>
     * <li>No puede contener solo espacios</li>
     * <li>Longitud entre 3 y 20 caracteres</li>
     * <li>No debe contener espacios internos</li>
     * </ul>
     * 
     * @param id ID del usuario
     * @return Lista de errores de validación
     */
    public List<String> validateId(String id) {
        List<String> errors = new ArrayList<>();

        if (id == null) {
            errors.add("User ID cannot be null");
            return errors;
        }

        if (id.trim().isEmpty()) {
            errors.add("User ID cannot be empty");
            return errors;
        }

        if (id.contains(" ")) {
            errors.add("User ID cannot contain spaces");
        }

        if (id.length() < MIN_ID_LENGTH) {
            errors.add(String.format("User ID must be at least %d characters long", MIN_ID_LENGTH));
        }

        if (id.length() > MAX_ID_LENGTH) {
            errors.add(String.format("User ID must not exceed %d characters", MAX_ID_LENGTH));
        }

        // Validar formato (opcional: solo alfanuméricos y guiones)
        if (!id.matches("^[A-Za-z0-9-_]+$")) {
            errors.add("User ID must contain only letters, numbers, hyphens and underscores");
        }

        return errors;
    }

    /**
     * Valida el nombre del usuario.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser nulo</li>
     * <li>No puede estar vacío después de trim</li>
     * <li>Longitud entre 1 y 100 caracteres</li>
     * </ul>
     * 
     * @param name Nombre del usuario
     * @return Lista de errores de validación
     */
    public List<String> validateName(String name) {
        List<String> errors = new ArrayList<>();

        if (name == null) {
            errors.add("User name cannot be null");
            return errors;
        }

        String trimmedName = name.trim();

        if (trimmedName.isEmpty()) {
            errors.add("User name cannot be empty");
            return errors;
        }

        if (trimmedName.length() < MIN_NAME_LENGTH) {
            errors.add(String.format("User name must be at least %d character long", MIN_NAME_LENGTH));
        }

        if (trimmedName.length() > MAX_NAME_LENGTH) {
            errors.add(String.format("User name must not exceed %d characters", MAX_NAME_LENGTH));
        }

        return errors;
    }

    /**
     * Valida el formato del email.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>Debe tener formato válido: usuario@dominio.extension</li>
     * <li>No puede contener espacios</li>
     * <li>Debe tener @ y al menos un punto después del @</li>
     * </ul>
     * 
     * <p>
     * <b>Ejemplos válidos:</b>
     * </p>
     * <ul>
     * <li>john.doe@example.com</li>
     * <li>user123@domain.co.uk</li>
     * <li>test_user@test-domain.org</li>
     * </ul>
     * 
     * @param email Email del usuario
     * @return Lista de errores de validación
     */
    public List<String> validateEmail(String email) {
        List<String> errors = new ArrayList<>();

        if (email == null) {
            errors.add("Email cannot be null");
            return errors;
        }

        String trimmedEmail = email.trim();

        if (trimmedEmail.isEmpty()) {
            errors.add("Email cannot be empty");
            return errors;
        }

        if (trimmedEmail.contains(" ")) {
            errors.add("Email cannot contain spaces");
        }

        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            errors.add("Email format is invalid (expected: user@domain.com)");
        }

        return errors;
    }

    /**
     * Valida que un email sea único (verificación externa).
     * 
     * <p>
     * <b>Nota:</b> Este método solo valida el formato.
     * La unicidad debe verificarse en el Service con el Repository.
     * </p>
     * 
     * @param email Email a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        boolean valid = EMAIL_PATTERN.matcher(email.trim()).matches();

        logger.debug("Email '{}' is valid? {}", email, valid);

        return valid;
    }

    /**
     * Valida el username del usuario.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser nulo</li>
     * <li>No puede estar vacío</li>
     * <li>Longitud entre 3 y 50 caracteres</li>
     * <li>Solo permite letras, números, guiones y guiones bajos</li>
     * </ul>
     * 
     * @param username Username del usuario
     * @return Lista de errores de validación
     */
    public List<String> validateUsername(String username) {
        List<String> errors = new ArrayList<>();

        if (username == null) {
            errors.add("Username cannot be null");
            return errors;
        }

        String trimmedUsername = username.trim();

        if (trimmedUsername.isEmpty()) {
            errors.add("Username cannot be empty");
            return errors;
        }

        if (trimmedUsername.length() < MIN_USERNAME_LENGTH) {
            errors.add(String.format("Username must be at least %d characters long", MIN_USERNAME_LENGTH));
        }

        if (trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            errors.add(String.format("Username must not exceed %d characters", MAX_USERNAME_LENGTH));
        }

        if (!trimmedUsername.matches("^[A-Za-z0-9-_]+$")) {
            errors.add("Username must contain only letters, numbers, hyphens and underscores");
        }

        return errors;
    }

    /**
     * Valida la contraseña del usuario.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser nula</li>
     * <li>No puede estar vacía</li>
     * <li>Longitud entre 6 y 255 caracteres</li>
     * <li>Se recomienda contener mayúsculas, minúsculas, números y caracteres
     * especiales</li>
     * </ul>
     * 
     * @param password Password del usuario
     * @return Lista de errores de validación
     */
    public List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null) {
            errors.add("Password cannot be null");
            return errors;
        }

        String trimmedPassword = password.trim();

        if (trimmedPassword.isEmpty()) {
            errors.add("Password cannot be empty");
            return errors;
        }

        if (trimmedPassword.length() < MIN_PASSWORD_LENGTH) {
            errors.add(String.format("Password must be at least %d characters long", MIN_PASSWORD_LENGTH));
        }

        if (trimmedPassword.length() > MAX_PASSWORD_LENGTH) {
            errors.add(String.format("Password must not exceed %d characters", MAX_PASSWORD_LENGTH));
        }

        return errors;
    }

    /**
     * Valida el rol del usuario.
     * 
     * <p>
     * <b>Roles válidos:</b>
     * </p>
     * <ul>
     * <li>USER - Usuario regular (puede solicitar préstamos)</li>
     * <li>LIBRARIAN - Bibliotecario (puede gestionar préstamos y usuarios)</li>
     * </ul>
     * 
     * @param role Rol del usuario (enum UserRole)
     * @return Lista de errores de validación
     */
    public List<String> validateRole(UserRole role) {
        List<String> errors = new ArrayList<>();

        if (role == null) {
            errors.add("Role cannot be null");
            return errors;
        }

        // UserRole es un enum, solo puede ser USER o LIBRARIAN
        // No hay validación adicional necesaria, pero se puede extender aquí
        logger.debug("Role validated: {}", role.name());

        return errors;
    }
}