package edu.eci.dosw.DOSW_Library.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones con sistema de logging integrado.
 * 
 * <h2>Sistema de Logging:</h2>
 * <ul>
 * <li>WARN - Para errores de negocio esperados (libro no disponible, usuario no
 * encontrado)</li>
 * <li>ERROR - Para errores inesperados del sistema</li>
 * <li>INFO - Para operaciones importantes</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger para esta clase.
     * ✅ ESTO ES LO NUEVO - Crea un logger específico para esta clase
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones cuando un libro no está disponible.
     * 
     * <p>
     * Registra un WARNING porque es un error esperado de negocio.
     * </p>
     */
    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleBookNotAvailable(
            BookNotAvailableException ex,
            HttpServletRequest request) {

        // ✅ LOGGING NIVEL WARN - Es un error de negocio esperado
        logger.warn("Book not available: {} | Path: {} | Method: {}",
                ex.getMessage(),
                request.getRequestURI(),
                request.getMethod());

        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /**
     * Maneja excepciones cuando un usuario no es encontrado.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        // ✅ LOGGING NIVEL WARN
        logger.warn("User not found: {} | Path: {}",
                ex.getMessage(),
                request.getRequestURI());

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Maneja excepciones de recursos no encontrados (genérico).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        // ✅ LOGGING NIVEL WARN
        logger.warn("Resource not found: {} | Path: {}",
                ex.getMessage(),
                request.getRequestURI());

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Maneja excepciones de límite de préstamos excedido.
     */
    @ExceptionHandler(LoanLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleLoanLimitExceeded(
            LoanLimitExceededException ex,
            HttpServletRequest request) {

        // ✅ LOGGING NIVEL WARN con detalles adicionales
        logger.warn("Loan limit exceeded: {} | Path: {} | User IP: {}",
                ex.getMessage(),
                request.getRequestURI(),
                request.getRemoteAddr() // IP del cliente
        );

        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    /**
     * Maneja errores de validación de DTOs (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        // ✅ LOGGING NIVEL WARN - Input inválido del cliente
        logger.warn("Validation failed: {} errors | Path: {} | Errors: {}",
                errors.size(),
                request.getRequestURI(),
                errors);

        ErrorResponse errorResponse = ErrorResponse.validationError(errors, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja IllegalArgumentException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        HttpStatus status = ex.getMessage() != null
                && ex.getMessage().toLowerCase().contains("not found")
                        ? HttpStatus.NOT_FOUND
                        : HttpStatus.BAD_REQUEST;

        // ✅ LOGGING según severidad
        if (status == HttpStatus.NOT_FOUND) {
            logger.warn("Resource not found (IllegalArgument): {} | Path: {}",
                    ex.getMessage(), request.getRequestURI());
        } else {
            logger.warn("Invalid argument: {} | Path: {}",
                    ex.getMessage(), request.getRequestURI());
        }

        return buildResponse(status, ex.getMessage(), request);
    }

    /**
     * Maneja IllegalStateException.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request) {

        // ✅ LOGGING NIVEL WARN
        logger.warn("Illegal state: {} | Path: {}",
                ex.getMessage(),
                request.getRequestURI());

        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /**
     * Maneja excepciones de autenticación.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        // ✅ LOGGING NIVEL WARN - Intento de acceso no autorizado
        logger.warn("Authentication failed: {} | Path: {} | IP: {}",
                ex.getMessage(),
                request.getRequestURI(),
                request.getRemoteAddr());

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                request);
    }

    /**
     * Maneja cualquier excepción no capturada específicamente.
     * 
     * <p>
     * Registra ERROR porque son errores INESPERADOS del sistema.
     * </p>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        // ✅ LOGGING NIVEL ERROR - Con STACK TRACE COMPLETO
        logger.error("Unexpected error | Path: {} | Method: {} | Error: {}",
                request.getRequestURI(),
                request.getMethod(),
                ex.getMessage(),
                ex // ✅ Este parámetro loggea el stack trace completo
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request);
    }

    /**
     * Construye respuesta de error estandarizada.
     */
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(body, status);
    }
}