package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.core.model.User;
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
}