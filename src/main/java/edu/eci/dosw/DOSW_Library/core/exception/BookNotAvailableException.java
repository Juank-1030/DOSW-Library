package edu.eci.dosw.DOSW_Library.core.exception;

/**
 * Excepción lanzada cuando se intenta prestar un libro que no está disponible.
 * 
 * <p>
 * Un libro puede no estar disponible por varias razones:
 * </p>
 * <ul>
 * <li>No hay copias en inventario (copies = 0)</li>
 * <li>Todas las copias están prestadas actualmente</li>
 * <li>El libro está marcado como no disponible manualmente</li>
 * <li>El libro está reservado para otro usuario</li>
 * </ul>
 * 
 * <p>
 * Esta es una <b>checked exception</b>, lo que significa que los métodos
 * que la lancen deben declararla con {@code throws} y los que las llamen
 * deben manejarla explícitamente.
 * </p>
 * 
 * <h2>Manejo por GlobalExceptionHandler:</h2>
 * <p>
 * Convertida automáticamente a HTTP <b>409 CONFLICT</b>
 * </p>
 * 
 * <h2>Ejemplo de uso:</h2>
 * 
 * <pre>{@code
 * if (book.getCopies() == 0) {
 *     throw BookNotAvailableException.withDetails(book.getId(), 0);
 * }
 * 
 * // Respuesta HTTP:
 * // 409 CONFLICT
 * // {
 * // "status": 409,
 * // "error": "Conflict",
 * // "message": "Book with ID 'BOOK-001' is not available (0 copies)"
 * // }
 * }</pre>
 * 
 * @see GlobalExceptionHandler#handleBookNotAvailable
 * @see edu.eci.dosw.DOSW_Library.core.model.Book
 * @author DOSW Company
 * @version 1.0
 */
public class BookNotAvailableException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor privado con mensaje personalizado.
     * Usar métodos factory para construcción.
     * 
     * @param message Descripción del error
     */
    private BookNotAvailableException(String message) {
        super(message);
    }

    /**
     * Constructor privado con mensaje y causa raíz.
     * 
     * @param message Descripción del error
     * @param cause   Excepción original que causó este error
     */
    private BookNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Crea excepción con detalles de disponibilidad del libro.
     * 
     * <p>
     * Genera mensaje automático:
     * "Book with ID '{bookId}' is not available for loan (available copies:
     * {copies})"
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * Book book = bookRepository.findById("BOOK-001").get();
     * if (!book.isAvailable()) {
     *     throw BookNotAvailableException.withDetails(
     *             book.getId(),
     *             book.getCopies());
     * }
     * }</pre>
     * 
     * @param bookId          ID del libro no disponible
     * @param availableCopies Número de copias disponibles actualmente
     * @return Nueva instancia con mensaje formateado
     */
    public static BookNotAvailableException withDetails(String bookId, int availableCopies) {
        return new BookNotAvailableException(
                String.format(
                        "Book with ID '%s' is not available for loan (available copies: %d)",
                        bookId,
                        availableCopies));
    }

    /**
     * Crea excepción para libro sin copias disponibles.
     * 
     * <p>
     * Mensaje simplificado cuando todas las copias están prestadas.
     * </p>
     * 
     * @param bookId ID del libro
     * @return Nueva instancia
     */
    public static BookNotAvailableException noCopiesAvailable(String bookId) {
        return new BookNotAvailableException(
                String.format("Book with ID '%s' has no copies available", bookId));
    }

    /**
     * Crea excepción para libro marcado como no disponible.
     * 
     * @param bookId ID del libro
     * @param reason Razón por la cual está marcado como no disponible
     * @return Nueva instancia
     */
    public static BookNotAvailableException markedUnavailable(String bookId, String reason) {
        return new BookNotAvailableException(
                String.format(
                        "Book with ID '%s' is marked as unavailable: %s",
                        bookId,
                        reason));
    }

    /**
     * Crea excepción con mensaje personalizado.
     * 
     * @param customMessage Mensaje personalizado del error
     * @return Nueva instancia
     */
    public static BookNotAvailableException withMessage(String customMessage) {
        return new BookNotAvailableException(customMessage);
    }

    /**
     * Crea excepción con mensaje y causa raíz.
     * 
     * <p>
     * Útil cuando la verificación de disponibilidad falla por un error
     * de base de datos o sistema.
     * </p>
     * 
     * <h3>Ejemplo:</h3>
     * 
     * <pre>{@code
     * try {
     *     verifyBookAvailability(bookId);
     * } catch (DataAccessException e) {
     *     throw BookNotAvailableException.withCause(
     *             "Unable to verify book availability",
     *             e);
     * }
     * }</pre>
     * 
     * @param message Descripción del error
     * @param cause   Excepción original
     * @return Nueva instancia con causa encadenada
     */
    public static BookNotAvailableException withCause(String message, Throwable cause) {
        return new BookNotAvailableException(message, cause);
    }
}