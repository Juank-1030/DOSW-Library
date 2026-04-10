package edu.eci.dosw.DOSW_Library.persistence.jpa.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte entre entidad JPA Book y modelo de dominio Book.
 *
 * En este proyecto, se utilizan los mismos modelos como entidades JPA,
 * por lo que el mapping es directo (identity mapping).
 *
 * En proyectos más grandes, aquí se haría la conversión:
 * BookEntity (JPA) ↔ Book (Domain)
 */
@Component
public class BookEntityMapper {

    /**
     * Convierte modelo de dominio a entidad JPA.
     * (Actualmente: identity mapping)
     *
     * @param book modelo de dominio
     * @return entidad JPA
     */
    public Book toEntity(Book book) {
        // En esta arquitectura, el modelo es a la vez entidad JPA
        return book;
    }

    /**
     * Convierte entidad JPA a modelo de dominio.
     * (Actualmente: identity mapping)
     *
     * @param entity entidad JPA
     * @return modelo de dominio
     */
    public Book toDomain(Book entity) {
        // En esta arquitectura, el modelo es a la vez entidad JPA
        return entity;
    }
}
