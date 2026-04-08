package edu.eci.dosw.DOSW_Library.persistence.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.CreateBookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UpdateBookInventoryDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper consolidado para todas las transformaciones de Book.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>✅ BookEntity ↔ Book (persistencia ↔ dominio)</li>
 * <li>✅ Book ↔ BookDTO (dominio ↔ API Response)</li>
 * <li>✅ CreateBookDTO → Book (API Request → dominio)</li>
 * <li>✅ UpdateBookInventoryDTO → Book updates (operaciones de inventario)</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 3.0 - Mapper consolidado
 */
@Component
public class BookPersistenceMapper {

    private static final Logger logger = LoggerFactory.getLogger(BookPersistenceMapper.class);

    // ============================================
    // PERSISTENCE LAYER (BookEntity ↔ Book)
    // ============================================

    public Book toDomain(BookEntity entity) {
        if (entity == null) {
            return null;
        }

        return Book.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .copies(entity.getCopies())
                .available(entity.getAvailable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public BookEntity toEntity(Book book) {
        if (book == null) {
            return null;
        }

        return BookEntity.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .copies(book.getCopies())
                .available(book.getAvailable())
                .build();
    }

    // ============================================
    // API LAYER (Book ↔ BookDTO)
    // ============================================

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
                .available(book.getAvailable() > 0)
                .build();

        logger.trace("Book converted successfully: {}", dto);

        return dto;
    }

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
    // DTO → ENTITY (API Request → Dominio)
    // ============================================

    public Book toEntity(CreateBookDTO createDTO) {
        if (createDTO == null) {
            logger.warn("Attempted to convert null CreateBookDTO to Book");
            return null;
        }

        logger.debug("Converting CreateBookDTO to Book | ID: {} | Title: '{}'",
                createDTO.getId(),
                createDTO.getTitle());

        Book book = new Book(
                createDTO.getId(),
                createDTO.getTitle(),
                createDTO.getAuthor(),
                createDTO.getCopies());

        logger.info("Book entity created from DTO | ID: {} | Copies: {} | Available: {}",
                book.getId(),
                book.getCopies(),
                book.getAvailable());

        return book;
    }

    // ============================================
    // INVENTORY OPERATIONS
    // ============================================

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

        book.setCopies(newCopies);
        book.setAvailable(newCopies);

        logger.info("Book {} inventory updated: {} -> {} copies | Available: {} | Reason: {}",
                book.getId(), originalCopies, newCopies, book.getAvailable(),
                updateDTO.getOperation());
    }
}
