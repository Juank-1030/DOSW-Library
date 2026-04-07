package edu.eci.dosw.DOSW_Library.core.exception;

/**
 * Excepción genérica para cuando un recurso no es encontrado en la base de
 * datos.
 * 
 * <p>
 * Esta es una <b>RuntimeException</b> (unchecked), lo que significa:
 * </p>
 * <ul>
 * <li>No necesita declararse con {@code throws} en las firmas de métodos</li>
 * <li>No obliga a usar try-catch en código cliente</li>
 * <li>Más conveniente para errores comunes de recursos no encontrados</li>
 * </ul>
 * 
 * <h2>Diferencia con UserNotFoundException:</h2>
 * <table border="1">
 * <tr>
 * <th>Característica</th>
 * <th>ResourceNotFoundException</th>
 * <th>UserNotFoundException</th>
 * </tr>
 * <tr>
 * <td>Tipo</td>
 * <td>RuntimeException (unchecked)</td>
 * <td>Exception (checked)</td>
 * </tr>
 * <tr>
 * <td>Uso</td>
 * <td>Genérico - cualquier recurso</td>
 * <td>Específico - solo usuarios</td>
 * </tr>
 * <tr>
 * <td>Manejo</td>
 * <td>Opcional</td>
 * <td>Obligatorio</td>
 * </tr>
 * </table>
 * 
 * <h2>Manejo por GlobalExceptionHandler:</h2>
 * <p>
 * Convertida automáticamente a HTTP <b>404 NOT FOUND</b>
 * </p>
 * 
 * <h2>Cuándo usar:</h2>
 * <ul>
 * <li>Búsquedas simples donde no importa manejar la excepción</li>
 * <li>Endpoints REST donde el handler global maneja el error</li>
 * <li>Recursos genéricos (no específicos como User)</li>
 * </ul>
 * 
 * <h2>Ejemplo de uso:</h2>
 * 
 * <pre>{@code
 * public Book findById(String id) {
 *     return bookRepository.findById(id)
 *             .orElseThrow(() -> new ResourceNotFoundException("Book", id));
 * }
 * 
 * // Respuesta HTTP:
 * // 404 NOT FOUND
 * // {
 * // "status": 404,
 * // "error": "Not Found",
 * // "message": "Book not found with ID: BOOK-001"
 * // }
 * }</pre>
 * 
 * @see GlobalExceptionHandler#handleResourceNotFound
 * @author DOSW Company
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje personalizado.
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * throw new ResourceNotFoundException("The requested item was not found");
     * }</pre>
     * 
     * @param message Descripción del error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor genérico por tipo de recurso e ID.
     * 
     * <p>
     * Formato: "{resourceType} not found with ID: {id}"
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * public Book findById(String id) {
     *     return bookRepository.findById(id)
     *         .orElseThrow(() -> new ResourceNotFoundException("Book", id));
     * }
     * 
     * findById("BOOK-999");
     * // "Book not found with ID: BOOK-999"
     * }</pre>
     * 
     * @param resourceType Tipo de recurso (Book, User, Loan, etc.)
     * @param id           ID del recurso no encontrado
     */
    public ResourceNotFoundException(String resourceType, String id) {
        super(String.format("%s not found with ID: %s", resourceType, id));
    }

    /**
     * Constructor con causa raíz.
     * 
     * <p>
     * Encadena excepciones para debugging completo.
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * try {
     *     return repository.findById(id).get();
     * } catch (NoSuchElementException e) {
     *     throw new ResourceNotFoundException(
     *             "Resource not found",
     *             e);
     * }
     * }</pre>
     * 
     * @param message Descripción del error
     * @param cause   Excepción original
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}