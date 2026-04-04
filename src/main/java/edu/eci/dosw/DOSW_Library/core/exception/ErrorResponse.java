package edu.eci.dosw.DOSW_Library.core.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Respuesta estandarizada de error para toda la API REST.
 * 
 * <p>
 * Esta clase representa la estructura de respuesta cuando ocurre un error
 * en cualquier endpoint de la aplicación. Proporciona información detallada
 * para debugging y para que los clientes puedan manejar errores apropiadamente.
 * </p>
 * 
 * <h2>Características:</h2>
 * <ul>
 * <li>Inmutable (@Value) - una vez creada no puede modificarse</li>
 * <li>Builder pattern (@Builder) - construcción fluida y legible</li>
 * <li>Formato JSON consistente con Spring Boot</li>
 * <li>Documentación Swagger automática</li>
 * <li>Compatible con estándares REST</li>
 * <li>Solo incluye campos no nulos en JSON</li>
 * </ul>
 * 
 * <h2>Cumplimiento con requisitos:</h2>
 * <ul>
 * <li>✅ Mensajes claros de error para el cliente</li>
 * <li>✅ Respuestas uniformes en toda la API</li>
 * <li>✅ Códigos HTTP apropiados</li>
 * <li>✅ Información útil sin exponer detalles sensibles</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.1
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estructura estandarizada de error para respuestas de la API REST")
public class ErrorResponse {

    @Schema(description = "Marca de tiempo del error en formato ISO-8601", example = "2024-01-15T14:30:15.123", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    String timestamp;

    @Schema(description = "Código de estado HTTP", example = "404", minimum = "100", maximum = "599", required = true)
    int status;

    @Schema(description = "Nombre descriptivo del error HTTP", example = "Not Found", required = true)
    String error;

    @Schema(description = "Mensaje descriptivo del error que ayuda a entender qué salió mal", example = "Book not found with ID: BOOK-001", required = true)
    String message;

    @Schema(description = "Ruta HTTP donde ocurrió el error (sin dominio)", example = "/api/books/BOOK-001", required = true)
    String path;

    @Schema(description = "Información adicional sobre el error (opcional)", example = "{ \"availableCopies\": 0, \"totalCopies\": 5 }", nullable = true)
    Map<String, Object> details;

    @Schema(description = "Lista de errores de validación específicos (opcional)", example = "[\"Title cannot be empty\", \"Author is required\"]", nullable = true)
    List<String> validationErrors;

    @Schema(description = "Código de error interno de la aplicación (opcional)", example = "BOOK_NOT_AVAILABLE", nullable = true)
    String errorCode;

    @Schema(description = "Método HTTP de la petición que falló", example = "POST", nullable = true)
    String method;

    @Schema(description = "Sugerencia de cómo resolver el error (opcional)", example = "Please ensure the book ID exists before creating a loan", nullable = true)
    String suggestion;

    // ============================================
    // MÉTODOS FACTORY ESTÁTICOS
    // ============================================

    /**
     * Crea una respuesta de error básica con los campos mínimos requeridos.
     * 
     * @param timestamp Marca de tiempo del error
     * @param status    Código HTTP
     * @param error     Nombre del error HTTP
     * @param message   Mensaje descriptivo
     * @param path      Ruta donde ocurrió el error
     * @return ErrorResponse básico
     */
    public static ErrorResponse of(
            String timestamp,
            int status,
            String error,
            String message,
            String path) {

        return ErrorResponse.builder()
                .timestamp(timestamp)
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Crea una respuesta de error con timestamp automático.
     * 
     * @param status  Código HTTP
     * @param error   Nombre del error
     * @param message Mensaje del error
     * @param path    Ruta del error
     * @return ErrorResponse con timestamp actual
     */
    public static ErrorResponse now(
            int status,
            String error,
            String message,
            String path) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Crea una respuesta de error NOT FOUND (404).
     * 
     * @param resourceType Tipo de recurso no encontrado
     * @param resourceId   ID del recurso
     * @param path         Ruta de la petición
     * @return ErrorResponse 404
     */
    public static ErrorResponse notFound(
            String resourceType,
            String resourceId,
            String path) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(404)
                .error("Not Found")
                .message(String.format("%s not found with ID: %s", resourceType, resourceId))
                .path(path)
                .errorCode("RESOURCE_NOT_FOUND")
                .build();
    }

    /**
     * Crea una respuesta de error BAD REQUEST (400) con errores de validación.
     * 
     * @param validationErrors Lista de errores de validación
     * @param path             Ruta de la petición
     * @return ErrorResponse 400 con validaciones
     */
    public static ErrorResponse validationError(
            List<String> validationErrors,
            String path) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(400)
                .error("Bad Request")
                .message("Validation failed for one or more fields")
                .path(path)
                .validationErrors(validationErrors)
                .errorCode("VALIDATION_ERROR")
                .suggestion("Please check the provided data and try again")
                .build();
    }

    /**
     * Crea una respuesta de error de validación con Map.
     * Compatible con el formato tradicional de Spring.
     * 
     * @param errors Mapa de campo → mensaje de error
     * @param path   Ruta de la petición
     * @return ErrorResponse con validaciones
     */
    public static ErrorResponse validationErrorMap(
            Map<String, String> errors,
            String path) {

        // Convertir Map a List de strings
        List<String> errorList = errors.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(400)
                .error("Bad Request")
                .message("Validation failed for one or more fields")
                .path(path)
                .validationErrors(errorList)
                .details(new HashMap<>(errors)) // Agregar también como details
                .errorCode("VALIDATION_ERROR")
                .suggestion("Please check the provided data and try again")
                .build();
    }

    /**
     * Crea una respuesta de error CONFLICT (409).
     * 
     * @param message Mensaje del conflicto
     * @param path    Ruta de la petición
     * @return ErrorResponse 409
     */
    public static ErrorResponse conflict(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(409)
                .error("Conflict")
                .message(message)
                .path(path)
                .errorCode("RESOURCE_CONFLICT")
                .build();
    }

    /**
     * Crea una respuesta de error FORBIDDEN (403).
     * 
     * @param message Mensaje de por qué está prohibido
     * @param path    Ruta de la petición
     * @return ErrorResponse 403
     */
    public static ErrorResponse forbidden(String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(403)
                .error("Forbidden")
                .message(message)
                .path(path)
                .errorCode("OPERATION_FORBIDDEN")
                .build();
    }

    /**
     * Crea una respuesta de error INTERNAL SERVER ERROR (500).
     * 
     * @param path Ruta de la petición
     * @return ErrorResponse 500
     */
    public static ErrorResponse internalServerError(String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(500)
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .path(path)
                .errorCode("INTERNAL_ERROR")
                .suggestion("If the problem persists, please contact support")
                .build();
    }
}