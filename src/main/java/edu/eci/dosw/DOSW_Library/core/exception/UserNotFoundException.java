package edu.eci.dosw.DOSW_Library.core.exception;

/**
 * Excepción lanzada cuando se busca un usuario que no existe en el sistema.
 * 
 * <p>
 * Esta es una <b>checked exception</b> porque la búsqueda de usuarios
 * es una operación crítica que debe manejarse explícitamente.
 * </p>
 * 
 * <h2>Casos de uso:</h2>
 * <ul>
 * <li>Búsqueda de usuario por ID que no existe</li>
 * <li>Búsqueda de usuario por email no registrado</li>
 * <li>Validación de usuario antes de crear préstamo</li>
 * <li>Actualización o eliminación de usuario inexistente</li>
 * </ul>
 * 
 * <h2>Manejo por GlobalExceptionHandler:</h2>
 * <p>
 * Convertida automáticamente a HTTP <b>404 NOT FOUND</b>
 * </p>
 * 
 * <h2>Patrón Factory:</h2>
 * <p>
 * Esta clase usa constructores privados y métodos factory (static)
 * para garantizar mensajes consistentes y evitar ambigüedad.
 * </p>
 * 
 * <h2>Ejemplo de uso:</h2>
 * 
 * <pre>{@code
 * public User findById(String id) throws UserNotFoundException {
 *     return userRepository.findById(id)
 *             .orElseThrow(() -> UserNotFoundException.byId(id));
 * }
 * 
 * // Respuesta HTTP:
 * // 404 NOT FOUND
 * // {
 * // "status": 404,
 * // "error": "Not Found",
 * // "message": "User not found with ID: USR-001"
 * // }
 * }</pre>
 * 
 * @see GlobalExceptionHandler#handleUserNotFound
 * @see edu.eci.dosw.DOSW_Library.core.model.User
 * @author DOSW Company
 * @version 1.0
 */
public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor privado con mensaje personalizado.
     * Usar métodos factory públicos.
     * 
     * @param message Descripción del error
     */
    private UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor privado con mensaje y causa raíz.
     * 
     * @param message Descripción del error
     * @param cause   Excepción original
     */
    private UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Crea excepción para usuario no encontrado por ID.
     * 
     * <p>
     * Formato: "User not found with ID: {userId}"
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * public User findById(String id) throws UserNotFoundException {
     *     return userRepository.findById(id)
     *             .orElseThrow(() -> UserNotFoundException.byId(id));
     * }
     * }</pre>
     * 
     * @param userId ID del usuario no encontrado
     * @return Nueva instancia con mensaje formateado
     */
    public static UserNotFoundException byId(String userId) {
        return new UserNotFoundException(
                String.format("User not found with ID: %s", userId));
    }

    /**
     * Crea excepción para usuario no encontrado por email.
     * 
     * <p>
     * Formato: "User not found with email: {email}"
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * public User findByEmail(String email) throws UserNotFoundException {
     *     return userRepository.findByEmail(email)
     *             .orElseThrow(() -> UserNotFoundException.byEmail(email));
     * }
     * }</pre>
     * 
     * @param email Email del usuario no encontrado
     * @return Nueva instancia con mensaje formateado
     */
    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException(
                String.format("User not found with email: %s", email));
    }

    /**
     * Crea excepción con mensaje personalizado.
     * 
     * <p>
     * Para casos donde necesitas un mensaje específico
     * que no sigue los patrones estándar.
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * throw UserNotFoundException.withMessage(
     *         "User account has been deactivated");
     * }</pre>
     * 
     * @param customMessage Mensaje personalizado
     * @return Nueva instancia
     */
    public static UserNotFoundException withMessage(String customMessage) {
        return new UserNotFoundException(customMessage);
    }

    /**
     * Crea excepción con mensaje y causa raíz.
     * 
     * <p>
     * Encadena excepciones para mantener contexto completo del error.
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * try {
     *     return userRepository.findById(id).orElseThrow();
     * } catch (DataAccessException e) {
     *     throw UserNotFoundException.withCause(
     *             "Database error while searching user",
     *             e);
     * }
     * }</pre>
     * 
     * @param message Descripción del error
     * @param cause   Excepción original
     * @return Nueva instancia con causa encadenada
     */
    public static UserNotFoundException withCause(String message, Throwable cause) {
        return new UserNotFoundException(message, cause);
    }
}