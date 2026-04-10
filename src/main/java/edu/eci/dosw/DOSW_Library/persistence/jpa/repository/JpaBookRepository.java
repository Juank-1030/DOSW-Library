package edu.eci.dosw.DOSW_Library.persistence.jpa.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Book.
 * Spring Data JPA genera la implementación automáticamente.
 *
 * NOTA: Esta es una interfaz interna de la capa JPA.
 * Los servicios usan la interfaz genérica:
 * persistence.repository.BookRepository
 * que es implementada por BookRepositoryJpaImpl.
 */
@Repository
public interface JpaBookRepository extends JpaRepository<Book, String> {
}
