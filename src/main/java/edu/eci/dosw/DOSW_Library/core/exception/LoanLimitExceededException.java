package edu.eci.dosw.DOSW_Library.core.exception;

/**
 * Excepción lanzada cuando un usuario intenta realizar un préstamo
 * pero ya alcanzó el límite máximo de préstamos activos permitido.
 * 
 * <h2>Reglas de negocio:</h2>
 * <ul>
 * <li>Límite estándar: <b>3 préstamos activos simultáneos</b></li>
 * <li>Solo cuentan préstamos en estado ACTIVE</li>
 * <li>Préstamos devueltos (RETURNED) no cuentan para el límite</li>
 * <li>Usuarios premium pueden tener límites diferentes (configurable)</li>
 * </ul>
 * 
 * <h2>Manejo por GlobalExceptionHandler:</h2>
 * <p>
 * Convertida automáticamente a HTTP <b>403 FORBIDDEN</b>
 * </p>
 * <p>
 * El usuario está autenticado pero no autorizado a realizar más préstamos.
 * </p>
 * 
 * <h2>Ejemplo de uso:</h2>
 * 
 * <pre>{@code
 * int activeLoans = loanRepository.countActiveByUser(user);
 * if (activeLoans >= MAX_LOANS) {
 *     throw LoanLimitExceededException.withLimit(
 *             user.getId(),
 *             activeLoans,
 *             MAX_LOANS);
 * }
 * 
 * // Respuesta HTTP:
 * // 403 FORBIDDEN
 * // {
 * // "status": 403,
 * // "error": "Forbidden",
 * // "message": "User 'USR-001' has reached the loan limit (3/3 active loans)"
 * // }
 * }</pre>
 * 
 * @see GlobalExceptionHandler#handleLoanLimitExceeded
 * @see edu.eci.dosw.DOSW_Library.core.model.Loan
 * @author DOSW Company
 * @version 1.0
 */
public class LoanLimitExceededException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Límite por defecto de préstamos activos simultáneos.
     */
    public static final int DEFAULT_LIMIT = 3;

    /**
     * Constructor privado con mensaje personalizado.
     * Usar métodos factory.
     * 
     * @param message Descripción del error
     */
    private LoanLimitExceededException(String message) {
        super(message);
    }

    /**
     * Constructor privado con mensaje y causa.
     * 
     * @param message Descripción del error
     * @param cause   Excepción original
     */
    private LoanLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Crea excepción con límite personalizado.
     * 
     * <p>
     * Formato: "User '{userId}' has reached the loan limit ({current}/{limit}
     * active loans)"
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * int active = loanRepository.countActiveByUser(user);
     * int limit = user.isPremium() ? 5 : 3;
     * 
     * if (active >= limit) {
     *     throw LoanLimitExceededException.withLimit(
     *             user.getId(),
     *             active,
     *             limit);
     * }
     * }</pre>
     * 
     * @param userId       ID del usuario
     * @param currentLoans Cantidad actual de préstamos activos
     * @param limit        Límite máximo permitido
     * @return Nueva instancia con mensaje formateado
     */
    public static LoanLimitExceededException withLimit(
            String userId,
            int currentLoans,
            int limit) {
        return new LoanLimitExceededException(
                String.format(
                        "User '%s' has reached the loan limit (%d/%d active loans)",
                        userId,
                        currentLoans,
                        limit));
    }

    /**
     * Crea excepción usando el límite por defecto (3).
     * 
     * <p>
     * Atajo para el caso más común de límite estándar.
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * int active = loanRepository.countActiveByUser(user);
     * if (active >= LoanLimitExceededException.DEFAULT_LIMIT) {
     *     throw LoanLimitExceededException.withDefaultLimit(
     *             user.getId(),
     *             active);
     * }
     * }</pre>
     * 
     * @param userId       ID del usuario
     * @param currentLoans Cantidad actual de préstamos
     * @return Nueva instancia usando límite DEFAULT_LIMIT (3)
     */
    public static LoanLimitExceededException withDefaultLimit(
            String userId,
            int currentLoans) {
        return withLimit(userId, currentLoans, DEFAULT_LIMIT);
    }

    /**
     * Crea excepción con mensaje personalizado.
     * 
     * @param customMessage Mensaje personalizado
     * @return Nueva instancia
     */
    public static LoanLimitExceededException withMessage(String customMessage) {
        return new LoanLimitExceededException(customMessage);
    }

    /**
     * Crea excepción con mensaje y causa raíz.
     * 
     * @param message Descripción del error
     * @param cause   Excepción original
     * @return Nueva instancia con causa encadenada
     */
    public static LoanLimitExceededException withCause(String message, Throwable cause) {
        return new LoanLimitExceededException(message, cause);
    }
}