package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador de lógica de negocio para libros.
 * 
 * <p>
 * <b>Diferencia con validaciones de DTOs:</b>
 * </p>
 * <ul>
 * <li>DTOs (@NotBlank, @Min): Validaciones de INPUT (formato, tipo de
 * dato)</li>
 * <li>Validators: Validaciones de NEGOCIO (reglas complejas, consistencia)</li>
 * </ul>
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>✅ Validar reglas de negocio complejas</li>
 * <li>✅ Validar consistencia de datos</li>
 * <li>✅ Validar relaciones entre campos</li>
 * <li>✅ Logging de validaciones</li>
 * </ul>
 * 
 * <p>
 * <b>Anotación @Component:</b>
 * </p>
 * <ul>
 * <li>Marca como componente de utilidad</li>
 * <li>Spring lo gestiona como singleton</li>
 * <li>Inyectable en Services</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Component
public class BookValidator {

    private static final Logger logger = LoggerFactory.getLogger(BookValidator.class);

    /**
     * Longitud mínima permitida para el ID de un libro.
     */
    private static final int MIN_ID_LENGTH = 3;

    /**
     * Longitud máxima permitida para el ID de un libro.
     */
    private static final int MAX_ID_LENGTH = 50;

    /**
     * Longitud mínima permitida para el título.
     */
    private static final int MIN_TITLE_LENGTH = 1;

    /**
     * Longitud máxima permitida para el título.
     */
    private static final int MAX_TITLE_LENGTH = 200;

    /**
     * Longitud mínima permitida para el autor.
     */
    private static final int MIN_AUTHOR_LENGTH = 1;

    /**
     * Longitud máxima permitida para el autor.
     */
    private static final int MAX_AUTHOR_LENGTH = 100;

    /**
     * Cantidad mínima de copias permitidas.
     */
    private static final int MIN_COPIES = 0;

    /**
     * Cantidad máxima de copias permitidas (razonable para biblioteca pequeña).
     */
    private static final int MAX_COPIES = 1000;

    // ============================================
    // VALIDACIÓN COMPLETA DE LIBRO
    // ============================================

    /**
     * Valida todos los aspectos de un libro.
     * 
     * <p>
     * <b>Validaciones aplicadas:</b>
     * </p>
     * <ul>
     * <li>ID no puede ser nulo o vacío</li>
     * <li>ID debe tener longitud entre 3 y 50 caracteres</li>
     * <li>ID no debe contener espacios</li>
     * <li>Título no puede ser nulo o vacío</li>
     * <li>Título debe tener longitud entre 1 y 200 caracteres</li>
     * <li>Autor no puede ser nulo o vacío</li>
     * <li>Autor debe tener longitud entre 1 y 100 caracteres</li>
     * <li>Copias debe ser >= 0 y <= 1000</li>
     * <li>Available debe ser consistente con copies (available = copies > 0)</li>
     * </ul>
     * 
     * <p>
     * <b>Ejemplo de uso:</b>
     * </p>
     * 
     * <pre>{@code
     * Book book = new Book("BOOK-001", "Clean Code", "Robert C. Martin", 5);
     * 
     * List<String> errors = bookValidator.validate(book);
     * if (!errors.isEmpty()) {
     *     throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
     * }
     * }</pre>
     * 
     * @param book Libro a validar
     * @return Lista de mensajes de error (vacía si no hay errores)
     */
    public List<String> validate(Book book) {
        logger.debug("Validating book: {}", book != null ? book.getId() : "null");

        List<String> errors = new ArrayList<>();

        if (book == null) {
            logger.warn("Book is null - validation failed");
            errors.add("Book cannot be null");
            return errors;
        }

        // Validar ID
        errors.addAll(validateId(book.getId()));

        // Validar título
        errors.addAll(validateTitle(book.getTitle()));

        // Validar autor
        errors.addAll(validateAuthor(book.getAuthor()));

        // Validar copias
        errors.addAll(validateCopies(book.getCopies()));

        // Validar consistencia available/copies
        errors.addAll(validateAvailabilityConsistency(book));

        if (errors.isEmpty()) {
            logger.debug("Book {} validated successfully", book.getId());
        } else {
            logger.warn("Book {} validation failed with {} errors: {}",
                    book.getId(),
                    errors.size(),
                    errors);
        }

        return errors;
    }

    // ============================================
    // VALIDACIONES INDIVIDUALES
    // ============================================

    /**
     * Valida el ID del libro.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser nulo</li>
     * <li>No puede estar vacío</li>
     * <li>No puede contener solo espacios</li>
     * <li>Longitud entre 3 y 50 caracteres</li>
     * <li>No debe contener espacios internos</li>
     * </ul>
     * 
     * @param id ID del libro
     * @return Lista de errores de validación
     */
    public List<String> validateId(String id) {
        List<String> errors = new ArrayList<>();

        if (id == null) {
            errors.add("Book ID cannot be null");
            return errors;
        }

        if (id.trim().isEmpty()) {
            errors.add("Book ID cannot be empty");
            return errors;
        }

        if (id.contains(" ")) {
            errors.add("Book ID cannot contain spaces");
        }

        if (id.length() < MIN_ID_LENGTH) {
            errors.add(String.format("Book ID must be at least %d characters long", MIN_ID_LENGTH));
        }

        if (id.length() > MAX_ID_LENGTH) {
            errors.add(String.format("Book ID must not exceed %d characters", MAX_ID_LENGTH));
        }

        // Validar formato (opcional: solo caracteres alfanuméricos y guiones)
        if (!id.matches("^[A-Za-z0-9-_]+$")) {
            errors.add("Book ID must contain only letters, numbers, hyphens and underscores");
        }

        return errors;
    }

    /**
     * Valida el título del libro.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser nulo</li>
     * <li>No puede estar vacío después de trim</li>
     * <li>Longitud entre 1 y 200 caracteres</li>
     * </ul>
     * 
     * @param title Título del libro
     * @return Lista de errores de validación
     */
    public List<String> validateTitle(String title) {
        List<String> errors = new ArrayList<>();

        if (title == null) {
            errors.add("Book title cannot be null");
            return errors;
        }

        String trimmedTitle = title.trim();

        if (trimmedTitle.isEmpty()) {
            errors.add("Book title cannot be empty");
            return errors;
        }

        if (trimmedTitle.length() < MIN_TITLE_LENGTH) {
            errors.add(String.format("Book title must be at least %d character long", MIN_TITLE_LENGTH));
        }

        if (trimmedTitle.length() > MAX_TITLE_LENGTH) {
            errors.add(String.format("Book title must not exceed %d characters", MAX_TITLE_LENGTH));
        }

        return errors;
    }

    /**
     * Valida el autor del libro.
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
     * @param author Autor del libro
     * @return Lista de errores de validación
     */
    public List<String> validateAuthor(String author) {
        List<String> errors = new ArrayList<>();

        if (author == null) {
            errors.add("Book author cannot be null");
            return errors;
        }

        String trimmedAuthor = author.trim();

        if (trimmedAuthor.isEmpty()) {
            errors.add("Book author cannot be empty");
            return errors;
        }

        if (trimmedAuthor.length() < MIN_AUTHOR_LENGTH) {
            errors.add(String.format("Book author must be at least %d character long", MIN_AUTHOR_LENGTH));
        }

        if (trimmedAuthor.length() > MAX_AUTHOR_LENGTH) {
            errors.add(String.format("Book author must not exceed %d characters", MAX_AUTHOR_LENGTH));
        }

        return errors;
    }

    /**
     * Valida la cantidad de copias.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>Debe ser >= 0 (no puede ser negativo)</li>
     * <li>Debe ser <= 1000 (límite razonable)</li>
     * </ul>
     * 
     * @param copies Cantidad de copias
     * @return Lista de errores de validación
     */
    public List<String> validateCopies(int copies) {
        List<String> errors = new ArrayList<>();

        if (copies < MIN_COPIES) {
            errors.add(String.format("Book copies cannot be negative (minimum: %d)", MIN_COPIES));
        }

        if (copies > MAX_COPIES) {
            errors.add(String.format("Book copies exceed maximum limit (%d)", MAX_COPIES));
        }

        return errors;
    }

    /**
     * Valida la consistencia entre el campo 'available' y 'copies'.
     * 
     * <p>
     * <b>Regla de negocio:</b>
     * </p>
     * <ul>
     * <li>available debe ser > 0 si copies > 0</li>
     * <li>available debe ser 0 si copies = 0</li>
     * </ul>
     * 
     * @param book Libro a validar
     * @return Lista de errores de validación
     */
    public List<String> validateAvailabilityConsistency(Book book) {
        List<String> errors = new ArrayList<>();

        if (book == null) {
            return errors;
        }

        int shouldBeAvailable = book.getCopies();
        int isAvailable = book.getAvailable();

        if (shouldBeAvailable != isAvailable) {
            errors.add(String.format(
                    "Book availability is inconsistent: copies=%d but available=%d (should be %d)",
                    book.getCopies(),
                    isAvailable,
                    shouldBeAvailable));
        }

        return errors;
    }

    // ============================================
    // VALIDACIONES DE OPERACIONES
    // ============================================

    /**
     * Valida que se pueda decrementar copias sin quedar en negativo.
     * 
     * <p>
     * <b>Uso:</b> Antes de prestar un libro
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * if (!bookValidator.canDecrementCopies(book, 1)) {
     *     throw new BookNotAvailableException("No copies available");
     * }
     * }</pre>
     * 
     * @param book      Libro a validar
     * @param decrement Cantidad a decrementar
     * @return true si se puede decrementar, false si resultaría negativo
     */
    public boolean canDecrementCopies(Book book, int decrement) {
        if (book == null) {
            logger.warn("Cannot validate decrement - book is null");
            return false;
        }

        int newCopies = book.getCopies() - decrement;
        boolean canDecrement = newCopies >= 0;

        logger.debug("Can decrement {} copies from book {}? {} (current: {}, result: {})",
                decrement,
                book.getId(),
                canDecrement,
                book.getCopies(),
                newCopies);

        return canDecrement;
    }

    /**
     * Valida que un libro tenga copias disponibles.
     * 
     * @param book Libro a validar
     * @return true si tiene copias disponibles, false en caso contrario
     */
    public boolean hasAvailableCopies(Book book) {
        if (book == null) {
            logger.warn("Cannot check availability - book is null");
            return false;
        }

        boolean hasAvailable = book.getCopies() > 0;

        logger.debug("Book {} has available copies? {} (copies: {})",
                book.getId(),
                hasAvailable,
                book.getCopies());

        return hasAvailable;
    }

    /**
     * Valida que un libro esté marcado como disponible.
     * 
     * @param book Libro a validar
     * @return true si tiene copias disponibles (available > 0), false en caso
     *         contrario
     */
    public boolean isAvailableForLoan(Book book) {
        if (book == null) {
            logger.warn("Cannot check loan availability - book is null");
            return false;
        }

        boolean available = book.getAvailable() > 0;

        logger.debug("Book {} is available for loan? {} (available: {})",
                book.getId(),
                available,
                book.getAvailable());

        return available;
    }
}