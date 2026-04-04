package edu.eci.dosw.DOSW_Library.core.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utilidades comunes de validación.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Métodos helper para validaciones frecuentes</li>
 * <li>Formateo de mensajes de error</li>
 * <li>Validaciones genéricas reutilizables</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Component
public class ValidationUtil {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtil.class);

    /**
     * Valida que una cadena no sea nula ni vacía.
     * 
     * @param value     Valor a validar
     * @param fieldName Nombre del campo (para mensaje de error)
     * @return Mensaje de error o null si es válido
     */
    public String validateNotEmpty(String value, String fieldName) {
        if (value == null) {
            return fieldName + " cannot be null";
        }

        if (value.trim().isEmpty()) {
            return fieldName + " cannot be empty";
        }

        return null;
    }

    /**
     * Valida que una cadena tenga longitud dentro de un rango.
     * 
     * @param value     Valor a validar
     * @param fieldName Nombre del campo
     * @param minLength Longitud mínima
     * @param maxLength Longitud máxima
     * @return Mensaje de error o null si es válido
     */
    public String validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            return null; // Ya se validaría con validateNotEmpty
        }

        int length = value.trim().length();

        if (length < minLength) {
            return String.format("%s must be at least %d characters long", fieldName, minLength);
        }

        if (length > maxLength) {
            return String.format("%s must not exceed %d characters", fieldName, maxLength);
        }

        return null;
    }

    /**
     * Valida que un número esté dentro de un rango.
     * 
     * @param value     Valor a validar
     * @param fieldName Nombre del campo
     * @param min       Valor mínimo
     * @param max       Valor máximo
     * @return Mensaje de error o null si es válido
     */
    public String validateRange(int value, String fieldName, int min, int max) {
        if (value < min) {
            return String.format("%s must be at least %d", fieldName, min);
        }

        if (value > max) {
            return String.format("%s must not exceed %d", fieldName, max);
        }

        return null;
    }

    /**
     * Lanza excepción si hay errores de validación.
     * 
     * <p>
     * <b>Uso típico:</b>
     * </p>
     * 
     * <pre>{@code
     * List<String> errors = bookValidator.validate(book);
     * validationUtil.throwIfInvalid(errors, "Book");
     * }</pre>
     * 
     * @param errors     Lista de errores
     * @param entityName Nombre de la entidad validada
     * @throws IllegalArgumentException Si hay errores
     */
    public void throwIfInvalid(List<String> errors, String entityName) {
        if (errors != null && !errors.isEmpty()) {
            String message = String.format("%s validation failed: %s",
                    entityName,
                    String.join(", ", errors));

            logger.error("Validation failed for {}: {}", entityName, errors);

            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Formatea una lista de errores en un mensaje único.
     * 
     * @param errors Lista de errores
     * @return Mensaje formateado
     */
    public String formatErrors(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            return "";
        }

        return String.join("; ", errors);
    }

    /**
     * Valida que un objeto no sea nulo.
     * 
     * @param object    Objeto a validar
     * @param fieldName Nombre del campo
     * @return Mensaje de error o null si es válido
     */
    public String validateNotNull(Object object, String fieldName) {
        if (object == null) {
            return fieldName + " cannot be null";
        }
        return null;
    }
}