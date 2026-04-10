package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de gestión de libros de la biblioteca.
 * 
 * <p>
 * <b>Responsabilidades (según diapositivas):</b>
 * </p>
 * <ul>
 * <li>✅ Lógica de negocio compleja relacionada con libros</li>
 * <li>✅ Validaciones de negocio (NO de input - esas van en DTOs)</li>
 * <li>✅ Gestión del inventario de copias</li>
 * <li>✅ NO acceso directo a BD (usaría Repository en versión con BD)</li>
 * </ul>
 * 
 * <p>
 * <b>Anotación @Service:</b>
 * </p>
 * <ul>
 * <li>Marca la clase como componente de capa de servicio</li>
 * <li>Spring la gestiona como singleton</li>
 * <li>Se inyecta en controllers</li>
 * </ul>
 * 
 * <p>
 * <b>Estructura de datos actual:</b>
 * </p>
 * <ul>
 * <li>Map&lt;String, Book&gt; bookInventory - Libros registrados</li>
 * <li>Map&lt;String, Integer&gt; bookCopies - Cantidad de copias por libro</li>
 * </ul>
 * 
 * <p>
 * <b>Logging implementado:</b>
 * </p>
 * <ul>
 * <li>INFO - Operaciones exitosas importantes</li>
 * <li>DEBUG - Flujo de métodos, búsquedas</li>
 * <li>WARN - Situaciones anómalas recuperables</li>
 * <li>ERROR - Errores graves</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    // Inyección del repositorio (interfaz genérica agnóstica de persistencia)
    private final BookRepository bookRepository;

    /**
     * Constructor con inyección de dependencias.
     * Spring inyecta automáticamente usando el constructor único.
     * 
     * @param bookRepository Repositorio genérico de libros (implementación: MongoDB o JPA)
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        logger.info("BookService initialized with BookRepository");
    }

    // ============================================
    // OPERACIONES DE CREACIÓN
    // ============================================

    /**
     * Agrega un nuevo libro al inventario de la biblioteca.
     * 
     * <p>
     * <b>Validaciones de negocio:</b>
     * </p>
     * <ul>
     * <li>El ID del libro no debe existir previamente</li>
     * <li>El número de copias debe ser consistente</li>
     * <li>La disponibilidad se calcula automáticamente</li>
     * </ul>
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * INFO  - "Adding book: BOOK-001 | Title: 'Clean Code' | Copies: 5"
     * DEBUG - "Book added successfully. Current inventory size: 1"
     * </pre>
     * 
     * @param book   Entidad Book con datos básicos (id, title, author)
     * @param copies Cantidad inicial de copias
     * @return El libro agregado con disponibilidad calculada
     * @throws IllegalArgumentException Si el libro ya existe
     */
    public Book addBook(Book book, int copies) {
        logger.info("Adding book: {} | Title: '{}' | Copies: {}",
                book.getId(),
                book.getTitle(),
                copies);

        // Validación de negocio: libro no debe existir
        if (bookRepository.existsById(book.getId())) {
            logger.warn("Attempted to add duplicate book: {}", book.getId());
            throw new IllegalArgumentException("Book with ID " + book.getId() + " already exists");
        }

        // Establecer copias y disponibilidad
        book.setCopies(copies);
        book.setAvailable(copies); // available es Integer (cantidad), no boolean

        // Guardar en BD
        Book savedBook = bookRepository.save(book);

        logger.debug("Book added successfully. Current inventory size: {}", bookRepository.count());
        logger.info("Book {} registered | Available: {} | Copies: {}",
                book.getId(),
                book.getAvailable(),
                copies);

        return savedBook;
    }

    // ============================================
    // OPERACIONES DE CONSULTA
    // ============================================

    /**
     * Obtiene un libro por su ID.
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Searching for book with ID: BOOK-001"
     * INFO  - "Book found: BOOK-001 | Title: 'Clean Code'"
     * WARN  - "Book not found: BOOK-999" (si no existe)
     * </pre>
     * 
     * @param bookId ID del libro a buscar
     * @return El libro encontrado
     * @throws ResourceNotFoundException Si el libro no existe
     */
    public Book getBookById(String bookId) {
        logger.debug("Searching for book with ID: {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.warn("Book not found: {}", bookId);
                    return new ResourceNotFoundException("Book", bookId);
                });

        logger.info("Book found: {} | Title: '{}'", bookId, book.getTitle());
        return book;
    }

    /**
     * Obtiene todos los libros registrados en la biblioteca.
     * 
     * @return Lista de todos los libros
     */
    public List<Book> getAllBooks() {
        logger.debug("Retrieving all books. Total count: {}", bookRepository.count());

        List<Book> books = bookRepository.findAll();

        logger.info("Retrieved {} books from inventory", books.size());
        return books;
    }

    /**
     * Obtiene la cantidad de copias disponibles de un libro.
     * 
     * @param bookId ID del libro
     * @return Cantidad de copias disponibles
     * @throws ResourceNotFoundException Si el libro no existe
     */
    public int getAvailableCopies(String bookId) {
        logger.debug("Getting available copies for book: {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Cannot get copies - Book not found: {}", bookId);
                    return new ResourceNotFoundException("Book", bookId);
                });

        int copies = book.getAvailable() != null ? book.getAvailable() : 0;

        logger.debug("Book {} has {} available copies", bookId, copies);
        return copies;
    }

    // ============================================
    // VALIDACIONES DE DISPONIBILIDAD
    // ============================================

    /**
     * Verifica si un libro está disponible para préstamo.
     * 
     * <p>
     * <b>Regla de negocio:</b> Un libro está disponible si tiene copias > 0
     * </p>
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Checking availability for book: BOOK-001"
     * INFO  - "Book BOOK-001 is available. Copies: 5"
     * WARN  - "Book BOOK-001 has no available copies" (si copies = 0)
     * </pre>
     * 
     * @param bookId ID del libro a verificar
     * @return true si está disponible (copies > 0)
     * @throws ResourceNotFoundException Si el libro no existe
     */
    public boolean isBookAvailable(String bookId) {
        logger.debug("Checking availability for book: {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Book ID not found in inventory: {}", bookId);
                    return new ResourceNotFoundException("Book", bookId);
                });

        Integer availableCopies = book.getAvailable();
        boolean available = availableCopies != null && availableCopies > 0;

        if (!available) {
            logger.warn("Book {} has no available copies", bookId);
        } else {
            logger.info("Book {} is available. Copies: {}", bookId, availableCopies);
        }

        return available;
    }

    // ============================================
    // OPERACIONES DE INVENTARIO
    // ============================================

    /**
     * Actualiza la disponibilidad de un libro (para préstamos/devoluciones).
     * 
     * <p>
     * <b>Uso interno:</b> LoanService llama a este método cuando:
     * </p>
     * <ul>
     * <li>Se crea un préstamo: updateAvailability(bookId, -1)</li>
     * <li>Se devuelve un libro: updateAvailability(bookId, +1)</li>
     * </ul>
     * 
     * <p>
     * <b>Validaciones de negocio:</b>
     * </p>
     * <ul>
     * <li>El libro debe existir</li>
     * <li>El resultado no puede ser copias negativas</li>
     * <li>La disponibilidad se actualiza automáticamente</li>
     * </ul>
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Updating availability for book: BOOK-001 | Change: -1"
     * INFO  - "Book BOOK-001 availability updated: 5 -> 4 copies"
     * ERROR - "Invalid operation - Result would be negative copies: 0 + (-1) = -1"
     * </pre>
     * 
     * @param bookId ID del libro
     * @param change Cambio en cantidad (positivo para agregar, negativo para
     *               quitar)
     * @throws ResourceNotFoundException Si el libro no existe
     * @throws IllegalStateException     Si resultaría en copias negativas
     */
    public void updateAvailability(String bookId, int change) {
        logger.debug("Updating availability for book: {} | Change: {}", bookId, change);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Cannot update availability - Book not found: {}", bookId);
                    return new ResourceNotFoundException("Book", bookId);
                });

        Integer currentCopies = book.getAvailable() != null ? book.getAvailable() : 0;
        int newCopies = currentCopies + change;

        // Validación de negocio: no puede haber copias negativas
        if (newCopies < 0) {
            logger.error("Invalid operation - Result would be negative copies: {} + {} = {}",
                    currentCopies, change, newCopies);
            throw new IllegalStateException(
                    String.format("Cannot remove %d copies from book %s (only %d available)",
                            Math.abs(change), bookId, currentCopies));
        }

        // Actualizar copias en la entidad
        book.setCopies(newCopies);
        book.setAvailable(newCopies);

        // Guardar cambios en BD
        bookRepository.save(book);

        logger.info("Book {} availability updated: {} -> {} copies | Available: {}",
                bookId, currentCopies, newCopies, book.getAvailable());
    }

    /**
     * Actualiza el inventario de copias de un libro (SET absoluto).
     * 
     * <p>
     * <b>Usado en:</b> Operaciones de inventario SET desde UpdateBookInventoryDTO
     * </p>
     * 
     * @param bookId      ID del libro
     * @param newQuantity Nueva cantidad absoluta de copias
     * @throws ResourceNotFoundException Si el libro no existe
     * @throws IllegalArgumentException  Si newQuantity es negativo
     */
    public void setBookCopies(String bookId, int newQuantity) {
        logger.debug("Setting book {} copies to: {}", bookId, newQuantity);

        if (newQuantity < 0) {
            logger.error("Invalid quantity: {} (must be >= 0)", newQuantity);
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Cannot set copies - Book not found: {}", bookId);
                    return new ResourceNotFoundException("Book", bookId);
                });

        int oldCopies = book.getAvailable() != null ? book.getAvailable() : 0;

        book.setCopies(newQuantity);
        book.setAvailable(newQuantity);

        bookRepository.save(book);

        logger.info("Book {} copies set: {} -> {} | Available: {}",
                bookId, oldCopies, newQuantity, book.getAvailable());
    }

    // ============================================
    // OPERACIONES DE ELIMINACIÓN (OPCIONAL)
    // ============================================

    /**
     * Elimina un libro del inventario.
     * 
     * <p>
     * <b>Validación de negocio:</b> Solo se puede eliminar si no hay préstamos
     * activos
     * </p>
     * <p>
     * (En versión completa, verificaría con LoanService)
     * </p>
     * 
     * @param bookId ID del libro a eliminar
     * @throws ResourceNotFoundException Si el libro no existe
     */
    public void deleteBook(String bookId) {
        logger.info("Attempting to delete book: {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Cannot delete - Book not found: {}", bookId);
                    return new ResourceNotFoundException("Book", bookId);
                });

        bookRepository.deleteById(bookId);

        logger.info("Book deleted: {} | Title: '{}'", bookId, book.getTitle());
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    /**
     * Verifica si un libro existe en el sistema.
     * 
     * @param bookId ID del libro
     * @return true si existe, false en caso contrario
     */
    public boolean existsById(String bookId) {
        boolean exists = bookRepository.existsById(bookId);
        logger.debug("Book {} exists: {}", bookId, exists);
        return exists;
    }

    /**
     * Obtiene el total de libros registrados.
     * 
     * @return Cantidad total de libros
     */
    public int getTotalBooks() {
        int total = (int) bookRepository.count();
        logger.debug("Total books in inventory: {}", total);
        return total;
    }
}