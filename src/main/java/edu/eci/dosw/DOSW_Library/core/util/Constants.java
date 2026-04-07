package edu.eci.dosw.DOSW_Library.core.util;

/**
 * Constantes globales de la aplicación DOSW Library.
 * 
 * <p>
 * <b>Propósito:</b>
 * </p>
 * <ul>
 * <li>✅ Centralizar valores constantes usados en toda la aplicación</li>
 * <li>✅ Evitar "magic numbers" y "magic strings"</li>
 * <li>✅ Facilitar mantenimiento (cambiar un valor en un solo lugar)</li>
 * <li>✅ Mejorar legibilidad del código</li>
 * <li>✅ Documentar reglas de negocio</li>
 * </ul>
 * 
 * <p>
 * <b>Organización:</b>
 * </p>
 * <ul>
 * <li>Reglas de negocio de la biblioteca</li>
 * <li>Límites de validación</li>
 * <li>Formatos y patrones</li>
 * <li>Mensajes de error estándar</li>
 * <li>Configuración de API</li>
 * </ul>
 * 
 * <p>
 * <b>Ejemplo de uso:</b>
 * </p>
 * 
 * <pre>{@code
 * // ❌ MALO - Magic number
 * if (activeLoans >= 3) {
 *     throw new LoanLimitExceededException(...);
 * }
 * 
 * // ✅ BUENO - Constante con nombre descriptivo
 * if (activeLoans >= Constants.MAX_ACTIVE_LOANS_PER_USER) {
 *     throw new LoanLimitExceededException(...);
 * }
 * }</pre>
 * 
 * @author DOSW Company
 * @version 1.0
 */
public final class Constants {

    /**
     * Constructor privado para prevenir instanciación.
     * Esta es una clase de utilidad que solo contiene constantes estáticas.
     * 
     * @throws UnsupportedOperationException Si se intenta instanciar
     */
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ============================================
    // REGLAS DE NEGOCIO DE LA BIBLIOTECA
    // ============================================

    /**
     * Número máximo de préstamos activos permitidos por usuario.
     * 
     * <p>
     * <b>Regla de negocio:</b> Un usuario solo puede tener hasta 3 libros
     * prestados simultáneamente en estado ACTIVE.
     * </p>
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>LoanService.validateLoanLimit()</li>
     * <li>LoanValidator.canUserBorrowMore()</li>
     * <li>LoanLimitExceededException</li>
     * </ul>
     */
    public static final int MAX_ACTIVE_LOANS_PER_USER = 3;

    /**
     * Días máximos de préstamo antes de considerarlo vencido.
     * 
     * <p>
     * <b>Regla de negocio:</b> Los préstamos tienen un plazo de 30 días
     * naturales desde la fecha de préstamo.
     * </p>
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>DateUtil.calculateDueDate()</li>
     * <li>DateUtil.isLoanOverdue()</li>
     * <li>LoanValidator.isLoanOverdue()</li>
     * </ul>
     */
    public static final int MAX_LOAN_DAYS = 30;

    /**
     * Cantidad mínima de copias permitidas para un libro.
     * 
     * <p>
     * <b>Regla de validación:</b> Un libro puede tener 0 o más copias,
     * pero nunca un número negativo.
     * </p>
     */
    public static final int MIN_BOOK_COPIES = 0;

    /**
     * Cantidad máxima de copias permitidas para un libro.
     * 
     * <p>
     * <b>Regla de validación:</b> Límite razonable para una biblioteca
     * pequeña/mediana.
     * </p>
     */
    public static final int MAX_BOOK_COPIES = 1000;

    // ============================================
    // LÍMITES DE VALIDACIÓN - LIBROS
    // ============================================

    /**
     * Longitud mínima permitida para el ID de un libro.
     * 
     * <p>
     * <b>Formato esperado:</b> BOOK-XXXXXXXX (mínimo 3 caracteres)
     * </p>
     */
    public static final int MIN_BOOK_ID_LENGTH = 3;

    /**
     * Longitud máxima permitida para el ID de un libro.
     * 
     * <p>
     * <b>Incluye:</b> ISBN, códigos internos, etc.
     * </p>
     */
    public static final int MAX_BOOK_ID_LENGTH = 50;

    /**
     * Longitud mínima permitida para el título de un libro.
     */
    public static final int MIN_BOOK_TITLE_LENGTH = 1;

    /**
     * Longitud máxima permitida para el título de un libro.
     * 
     * <p>
     * <b>Ejemplo:</b> "Clean Code: A Handbook of Agile Software Craftsmanship"
     * tiene ~60 caracteres.
     * </p>
     */
    public static final int MAX_BOOK_TITLE_LENGTH = 200;

    /**
     * Longitud mínima permitida para el autor de un libro.
     */
    public static final int MIN_BOOK_AUTHOR_LENGTH = 1;

    /**
     * Longitud máxima permitida para el autor de un libro.
     * 
     * <p>
     * <b>Ejemplo:</b> "Robert Cecil Martin" tiene ~19 caracteres.
     * </p>
     */
    public static final int MAX_BOOK_AUTHOR_LENGTH = 100;

    // ============================================
    // LÍMITES DE VALIDACIÓN - USUARIOS
    // ============================================

    /**
     * Longitud mínima permitida para el ID de un usuario.
     * 
     * <p>
     * <b>Formato esperado:</b> USR-XXXXXXXX
     * </p>
     */
    public static final int MIN_USER_ID_LENGTH = 3;

    /**
     * Longitud máxima permitida para el ID de un usuario.
     */
    public static final int MAX_USER_ID_LENGTH = 20;

    /**
     * Longitud mínima permitida para el nombre de un usuario.
     */
    public static final int MIN_USER_NAME_LENGTH = 1;

    /**
     * Longitud máxima permitida para el nombre de un usuario.
     */
    public static final int MAX_USER_NAME_LENGTH = 100;

    // ============================================
    // FORMATOS Y PATRONES
    // ============================================

    /**
     * Formato de fecha ISO-8601 usado en la aplicación.
     * 
     * <p>
     * <b>Formato:</b> yyyy-MM-dd
     * </p>
     * <p>
     * <b>Ejemplo:</b> 2024-01-15
     * </p>
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>Respuestas JSON de la API</li>
     * <li>Serialización de LocalDate</li>
     * <li>Logs y timestamps</li>
     * </ul>
     */
    public static final String DATE_FORMAT_ISO = "yyyy-MM-dd";

    /**
     * Formato de fecha largo usado en reportes.
     * 
     * <p>
     * <b>Formato:</b> dd/MM/yyyy
     * </p>
     * <p>
     * <b>Ejemplo:</b> 15/01/2024
     * </p>
     */
    public static final String DATE_FORMAT_LONG = "dd/MM/yyyy";

    /**
     * Formato de fecha y hora usado en logs.
     * 
     * <p>
     * <b>Formato:</b> yyyy-MM-dd HH:mm:ss
     * </p>
     * <p>
     * <b>Ejemplo:</b> 2024-01-15 14:30:45
     * </p>
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Patrón regex para validar email.
     * 
     * <p>
     * <b>Formato:</b> usuario@dominio.extension
     * </p>
     * <p>
     * <b>Ejemplos válidos:</b>
     * </p>
     * <ul>
     * <li>john.doe@example.com</li>
     * <li>user123@domain.co.uk</li>
     * <li>test_user@test-domain.org</li>
     * </ul>
     */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * Patrón regex para validar IDs (alfanumérico con guiones y underscores).
     * 
     * <p>
     * <b>Formato permitido:</b> Letras, números, guiones (-) y underscores (_)
     * </p>
     * <p>
     * <b>Ejemplos válidos:</b>
     * </p>
     * <ul>
     * <li>BOOK-001</li>
     * <li>USR-A1B2C3D4</li>
     * <li>LOAN_20240115</li>
     * </ul>
     */
    public static final String ID_REGEX = "^[A-Za-z0-9-_]+$";

    // ============================================
    // MENSAJES DE ERROR ESTÁNDAR
    // ============================================

    /**
     * Mensaje para entidad no encontrada.
     * 
     * <p>
     * <b>Formato:</b> "{Tipo de entidad} not found with ID: {id}"
     * </p>
     * <p>
     * <b>Uso:</b> String.format(MSG_NOT_FOUND, "Book", "BOOK-001")
     * </p>
     * <p>
     * <b>Resultado:</b> "Book not found with ID: BOOK-001"
     * </p>
     */
    public static final String MSG_NOT_FOUND = "%s not found with ID: %s";

    /**
     * Mensaje para límite de préstamos excedido.
     * 
     * <p>
     * <b>Formato:</b> "User '{userId}' has reached the loan limit ({current}/{max}
     * active loans)"
     * </p>
     */
    public static final String MSG_LOAN_LIMIT_EXCEEDED = "User '%s' has reached the loan limit (%d/%d active loans)";

    /**
     * Mensaje para libro no disponible.
     * 
     * <p>
     * <b>Formato:</b> "Book with ID '{id}' is not available (0 copies)"
     * </p>
     */
    public static final String MSG_BOOK_NOT_AVAILABLE = "Book with ID '%s' is not available (0 copies)";

    /**
     * Mensaje para validación fallida.
     */
    public static final String MSG_VALIDATION_FAILED = "Validation failed for one or more fields";

    /**
     * Mensaje para operación no permitida.
     */
    public static final String MSG_OPERATION_NOT_ALLOWED = "Operation not allowed: %s";

    /**
     * Mensaje para dato duplicado.
     */
    public static final String MSG_ALREADY_EXISTS = "%s with %s '%s' already exists";

    // ============================================
    // CONFIGURACIÓN DE API
    // ============================================

    /**
     * Versión de la API.
     */
    public static final String API_VERSION = "v1";

    /**
     * Base path de la API REST.
     * 
     * <p>
     * <b>Todos los endpoints empiezan con:</b> /api
     * </p>
     */
    public static final String API_BASE_PATH = "/api";

    /**
     * Endpoint base de libros.
     * 
     * <p>
     * <b>Path completo:</b> /api/books
     * </p>
     */
    public static final String BOOKS_ENDPOINT = "/books";

    /**
     * Endpoint base de usuarios.
     * 
     * <p>
     * <b>Path completo:</b> /api/users
     * </p>
     */
    public static final String USERS_ENDPOINT = "/users";

    /**
     * Endpoint base de préstamos.
     * 
     * <p>
     * <b>Path completo:</b> /api/loans
     * </p>
     */
    public static final String LOANS_ENDPOINT = "/loans";

    // ============================================
    // ESTADOS DE ENTIDADES
    // ============================================

    /**
     * Estado de préstamo activo.
     * 
     * <p>
     * <b>Significado:</b> El libro está prestado y no ha sido devuelto.
     * </p>
     */
    public static final String LOAN_STATUS_ACTIVE = "ACTIVE";

    /**
     * Estado de préstamo devuelto.
     * 
     * <p>
     * <b>Significado:</b> El libro fue devuelto y el préstamo está cerrado.
     * </p>
     */
    public static final String LOAN_STATUS_RETURNED = "RETURNED";

    // ============================================
    // PREFIJOS DE IDs
    // ============================================

    /**
     * Prefijo para IDs de libros.
     * 
     * <p>
     * <b>Formato resultante:</b> BOOK-XXXXXXXX
     * </p>
     */
    public static final String BOOK_ID_PREFIX = "BOOK";

    /**
     * Prefijo para IDs de usuarios.
     * 
     * <p>
     * <b>Formato resultante:</b> USR-XXXXXXXX
     * </p>
     */
    public static final String USER_ID_PREFIX = "USR";

    /**
     * Prefijo para IDs de préstamos.
     * 
     * <p>
     * <b>Formato resultante:</b> LOAN-XXXXXXXX
     * </p>
     */
    public static final String LOAN_ID_PREFIX = "LOAN";

    // ============================================
    // CONFIGURACIÓN DE PAGINACIÓN (OPCIONAL)
    // ============================================

    /**
     * Tamaño de página por defecto para listados.
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Tamaño máximo de página permitido.
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Número de página inicial (primera página).
     */
    public static final int FIRST_PAGE = 0;
}