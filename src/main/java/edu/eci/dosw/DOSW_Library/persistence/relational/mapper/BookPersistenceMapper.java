package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.CreateBookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UpdateBookInventoryDTO;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapeador bidireccional entre BookEntity (persistencia) y Book (core.model)
 * 
 * Responsabilidades:
 * - Convertir BookEntity → Book (resultado de consultas JPA)
 * - Convertir Book → BookEntity (preparar para guardar en BD)
 * 
 * @author DOSW-Library Team
 */
@Component
public class BookPersistenceMapper {

    /**
     * Convertir entidad JPA a modelo de dominio
     * 
     * @param entity BookEntity desde BD
     * @return Book modelo de negocio, null si entity es null
     */
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

    /**
     * Convertir modelo de dominio a entidad JPA
     * 
     * Nota: No convierte timestamps (PrePersist/PreUpdate los manejan)
     * 
     * @param domain Book modelo de negocio
     * @return BookEntity lista para persistir, null si domain es null
     */
    public BookEntity toEntity(Book domain) {
        if (domain == null) {
            return null;
        }

        return BookEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .author(domain.getAuthor())
                .copies(domain.getCopies())
                .available(domain.getAvailable())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    // ============== CONVERSIONES DTO → DOMAIN ==============

    /**
     * Convertir CreateBookDTO a model.Book
     * 
     * @param createDTO DTO de creación desde controlador
     * @return Book modelo con datos iniciales, null si dto es null
     */
    public Book toEntity(CreateBookDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        return Book.builder()
                .id(createDTO.getId())
                .title(createDTO.getTitle())
                .author(createDTO.getAuthor())
                .copies(createDTO.getCopies())
                .available(createDTO.getCopies())
                .build();
    }

    /**
     * Convertir UpdateBookInventoryDTO a model.Book (para actualizar inventario)
     * 
     * @param updateDTO    DTO con cambios de inventario
     * @param existingBook Libro existente (para preservar datos)
     * @return Book modelo actualizado
     */
    public Book toEntity(UpdateBookInventoryDTO updateDTO, Book existingBook) {
        if (updateDTO == null || existingBook == null) {
            return existingBook;
        }

        int newCopies;
        int newAvailable;

        // Aplicar la operación según el tipo
        switch (updateDTO.getOperation()) {
            case SET:
                // Establecer cantidad absoluta
                newCopies = updateDTO.getQuantity();
                newAvailable = updateDTO.getQuantity();
                break;
            case ADD:
                // Agregar copias
                newCopies = existingBook.getCopies() + updateDTO.getQuantity();
                newAvailable = existingBook.getAvailable() + updateDTO.getQuantity();
                break;
            case REMOVE:
                // Quitar copias (no puede ser negativo)
                newCopies = Math.max(0, existingBook.getCopies() - updateDTO.getQuantity());
                newAvailable = Math.max(0, existingBook.getAvailable() - updateDTO.getQuantity());
                break;
            default:
                return existingBook;
        }

        return Book.builder()
                .id(existingBook.getId())
                .title(existingBook.getTitle())
                .author(existingBook.getAuthor())
                .copies(newCopies)
                .available(newAvailable)
                .createdAt(existingBook.getCreatedAt())
                .updatedAt(existingBook.getUpdatedAt())
                .build();
    }

    // ============== CONVERSIONES DOMAIN → DTO ==============

    /**
     * Convertir model.Book a BookDTO (para respuestas)
     * 
     * @param domain Book modelo de negocio
     * @return BookDTO para enviar al cliente, null si domain es null
     */
    public BookDTO toDTO(Book domain) {
        if (domain == null) {
            return null;
        }

        return BookDTO.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .author(domain.getAuthor())
                .copies(domain.getCopies())
                .available(domain.getAvailable() > 0)
                .build();
    }

    /**
     * Convertir lista de model.Book a lista de BookDTO
     * 
     * @param books Lista de libros
     * @return Lista de DTOs, lista vacía si books es null
     */
    public List<BookDTO> toDTOList(List<Book> books) {
        if (books == null) {
            return List.of();
        }

        return books.stream()
                .map(this::toDTO)
                .toList();
    }
}
