package edu.eci.dosw.DOSW_Library.persistence.mongodb.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.BookDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte entre documento MongoDB BookDocument y modelo de dominio
 * Book.
 * 
 * Realiza conversión bidireccional entre la representación NoSQL y el modelo
 * de dominio agnóstico de persistencia.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Component
public class BookDocumentMapper {

    /**
     * Convierte modelo de dominio Book a documento MongoDB.
     *
     * @param book modelo de dominio
     * @return documento MongoDB
     */
    public BookDocument toDocument(Book book) {
        if (book == null) {
            return null;
        }

        // Mapeo simple - solo guardamos ID, título, autor, ISBN
        // La estructura de disponibilidad se gestiona en otro lugar
        return BookDocument.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .active(true)
                .build();
    }

    /**
     * Convierte documento MongoDB a modelo de dominio Book.
     *
     * @param document documento MongoDB
     * @return modelo de dominio
     */
    public Book toDomain(BookDocument document) {
        if (document == null) {
            return null;
        }

        // Mapeo simple - extraemos datos básicos
        // Nota: copies y available no se pueden acceder directamente
        // ya que están en la estructura anidada BookAvailability
        return Book.builder()
                .id(document.getId())
                .title(document.getTitle())
                .author(document.getAuthor())
                .isbn(document.getIsbn())
                .build();
    }
}
