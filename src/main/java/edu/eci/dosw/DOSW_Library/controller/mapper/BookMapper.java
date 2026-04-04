package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.CreateBookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UpdateBookInventoryDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Book y DTOs relacionados.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Convertir Book (Entity) → BookDTO (Response)</li>
 * <li>Convertir CreateBookDTO (Request) → Book (Entity)</li>
 * <li>Aplicar operaciones de inventario desde UpdateBookInventoryDTO</li>
 * <li>Logging de todas las conversiones para auditoría</li>
 * </ul>
 * 
 * <p>
 * <b>Patrón Component:</b>
 * </p>
 * <ul>
 * <li>@Component - Spring lo gestiona como bean singleton</li>
 * <li>Inyectable en Controllers y Services</li>
 * <li>Thread-safe (sin estado mutable)</li>
 * </ul>
 * 
 * <p>
 * <b>Logging Strategy:</b>
 * </p>
 * <ul>
 * <li>DEBUG - Conversiones normales</li>
 * <li>INFO - Operaciones importantes (creación, actualización inventario)</li>
 * <li>WARN - Datos nulos o situaciones anómalas</li>
 * <li>TRACE - Detalles completos del objeto convertido</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@Component
public class BookMapper {

    private static final Logger logger = LoggerFactory.getLogger(BookMapper.class);

    // ============================================
    // ENTITY → DTO (Para respuestas)
    // ============================================

    /**
     * Convierte una entidad Book a BookDTO para respuestas HTTP.
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>GET /api/books/{id} - Respuesta individual</li>
     * <li>GET /api/books - Respuesta de listado</li>
     * <li>POST /api/books - Respuesta después de crear</li>
     * </ul>
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Converting Book to DTO | ID: BOOK-001 | Title: 'Clean Code'"
     * TRACE - "Book converted successfully: BookDTO{id=BOOK-001, ...}"
     * </pre>
     * 
     * @param book Entidad Book a convertir
     * @return BookDTO con los datos del libro, o null si book es null
     */
    public BookDTO toDTO(Book book) {
        if (book == null) {
            logger.warn("Attempted to convert null Book to BookDTO");
            return null;
        }

        logger.debug("Converting Book to DTO | ID: {} | Title: '{}'",
                book.getId(),
                book.getTitle());

        BookDTO dto = BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .copies(book.getCopies())
                .available(book.isAvailable())
                .build();

        logger.trace("Book converted successfully: {}", dto);

        return dto;
    }

    /**
     * Convierte una lista de entidades Book a lista de BookDTOs.
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>GET /api/books - Listar todos los libros</li>
     * <li>GET /api/books/search?query=... - Resultados de búsqueda</li>
     * </ul>
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Converting 5 books to DTO list"
     * DEBUG - "Converted 5 books to DTOs successfully"
     * </pre>
     * 
     * @param books Lista de entidades Book
     * @return Lista de BookDTOs, o lista vacía si books es null
     */
    public List<BookDTO> toDTOList(List<Book> books) {
        if (books == null) {
            logger.warn("Attempted to convert null Book list to DTO list");
            return List.of();
        }

        logger.debug("Converting {} books to DTO list", books.size());

        List<BookDTO> dtoList = books.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        logger.debug("Converted {} books to DTOs successfully", dtoList.size());

        return dtoList;
    }

    // ============================================
    // DTO → ENTITY (Para crear nuevos libros)
    // ============================================

    /**
     * Convierte CreateBookDTO a entidad Book para persistencia.
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>POST /api/books - Crear nuevo libro</li>
     * </ul>
     * 
     * <p>
     * <b>Lógica aplicada:</b>
     * </p>
     * <ul>
     * <li>available se calcula automáticamente: copies > 0</li>
     * <li>Todos los campos son obligatorios (validados en DTO)</li>
     * </ul>
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Converting CreateBookDTO to Book | ID: BOOK-001 | Title: 'Clean Code'"
     * INFO  - "Book entity created from DTO | ID: BOOK-001 | Copies: 5"
     * </pre>
     * 
     * @param createDTO DTO con datos para crear el libro
     * @return Nueva entidad Book lista para persistir, o null si createDTO es null
     */
    public Book toEntity(CreateBookDTO createDTO) {
        if (createDTO == null) {
            logger.warn("Attempted to convert null CreateBookDTO to Book");
            return null;
        }

        logger.debug("Converting CreateBookDTO to Book | ID: {} | Title: '{}'",
                createDTO.getId(),
                createDTO.getTitle());

        // Constructor que ya establece available = copies > 0
        Book book = new Book(
                createDTO.getId(),
                createDTO.getTitle(),
                createDTO.getAuthor(),
                createDTO.getCopies());

        logger.info("Book entity created from DTO | ID: {} | Copies: {} | Available: {}",
                book.getId(),
                book.getCopies(),
                book.isAvailable());

        return book;
    }

    // ============================================
    // ACTUALIZACIÓN DE INVENTARIO
    // ============================================

    /**
     * Aplica operaciones de inventario a un libro existente.
     * 
     * <p>
     * <b>Operaciones soportadas:</b>
     * </p>
     * <ul>
     * <li><b>SET</b>: Establece cantidad absoluta</li>
     * <li><b>ADD</b>: Incrementa cantidad actual</li>
     * <li><b>REMOVE</b>: Decrementa cantidad actual</li>
     * </ul>
     * 
     * <p>
     * <b>Validaciones:</b>
     * </p>
     * <ul>
     * <li>Cantidad no puede ser negativa después de REMOVE</li>
     * <li>Actualiza automáticamente el campo 'available'</li>
     * </ul>
     * 
     * <p>
     * <b>Logging detallado:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Updating Book BOOK-001 inventory with operation: ADD"
     * DEBUG - "ADD operation: 5 + 3 = 8 copies"
     * INFO  - "Book BOOK-001 inventory updated: 5 -> 8 copies | Available: true | Reason: Compra nuevos"
     * </pre>
     * 
     * @param book      Entidad existente a actualizar
     * @param updateDTO DTO con operación y cantidad
     * @throws IllegalArgumentException Si book es null
     * @throws IllegalStateException    Si la operación resultaría en copias
     *                                  negativas
     */
    public void updateInventory(Book book, UpdateBookInventoryDTO updateDTO) {
        if (book == null) {
            logger.error("Cannot update inventory - Book entity is null");
            throw new IllegalArgumentException("Book entity cannot be null");
        }

        if (updateDTO == null) {
            logger.warn("UpdateBookInventoryDTO is null, no changes applied to Book {}",
                    book.getId());
            return;
        }

        logger.debug("Updating Book {} inventory with operation: {}",
                book.getId(),
                updateDTO.getOperation());

        int originalCopies = book.getCopies();
        int newCopies;

        switch (updateDTO.getOperation()) {
            case SET:
                newCopies = updateDTO.getQuantity();
                logger.debug("SET operation: {} copies (absolute)", newCopies);
                break;

            case ADD:
                newCopies = originalCopies + updateDTO.getQuantity();
                logger.debug("ADD operation: {} + {} = {} copies",
                        originalCopies,
                        updateDTO.getQuantity(),
                        newCopies);
                break;

            case REMOVE:
                newCopies = originalCopies - updateDTO.getQuantity();
                logger.debug("REMOVE operation: {} - {} = {} copies",
                        originalCopies,
                        updateDTO.getQuantity(),
                        newCopies);

                if (newCopies < 0) {
                    logger.error("Invalid REMOVE operation for Book {}: Would result in {} copies (negative)",
                            book.getId(),
                            newCopies);
                    throw new IllegalStateException(
                            String.format(
                                    "Cannot remove %d copies from book %s (only %d available)",
                                    updateDTO.getQuantity(),
                                    book.getId(),
                                    originalCopies));
                }
                break;

            default:
                logger.error("Unknown inventory operation: {}", updateDTO.getOperation());
                throw new IllegalArgumentException("Unknown operation: " + updateDTO.getOperation());
        }

        // Aplicar cambios
        book.setCopies(newCopies);
        book.setAvailable(newCopies > 0);

        String reason = updateDTO.getReason() != null ? updateDTO.getReason() : "No reason provided";

        logger.info("Book {} inventory updated: {} -> {} copies | Available: {} | Reason: {}",
                book.getId(),
                originalCopies,
                newCopies,
                book.isAvailable(),
                reason);
    }
}