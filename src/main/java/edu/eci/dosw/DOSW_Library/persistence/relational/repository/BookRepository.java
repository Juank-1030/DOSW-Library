package edu.eci.dosw.DOSW_Library.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para acceso a datos de libros
 * 
 * Proporciona operaciones CRUD + consultas personalizadas para BookEntity
 * 
 * Verbo del cambio: Importar de persistence.relational.entity, no core.model
 * 
 * @author DOSW-Library Team
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {

    /**
     * Buscar libro por titulo
     * 
     * @param title Titulo del libro
     * @return Libro si existe, vacio si no
     */
    Optional<BookEntity> findByTitle(String title);

    /**
     * Buscar libros por autor
     * 
     * @param author Autor del libro
     * @return Lista de libros del autor
     */
    List<BookEntity> findByAuthor(String author);

    /**
     * Buscar libros disponibles (available > 0)
     * 
     * @return Lista de libros con copias disponibles
     */
    @Query("SELECT b FROM BookEntity b WHERE b.available > 0")
    List<BookEntity> findAvailableBooks();

    /**
     * Buscar libro por titulo y autor (unico)
     * 
     * @param title  Titulo
     * @param author Autor
     * @return Libro si existe, vacio si no
     */
    @Query("SELECT b FROM BookEntity b WHERE b.title = ?1 AND b.author = ?2")
    Optional<BookEntity> findByTitleAndAuthor(String title, String author);

    /**
     * Libros sin stock disponible
     * 
     * @return Libros agotados
     */
    @Query("SELECT b FROM BookEntity b WHERE b.available = 0")
    List<BookEntity> findUnavailableBooks();

}
