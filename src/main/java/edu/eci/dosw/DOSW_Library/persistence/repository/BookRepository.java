package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para Book
 * 
 * IMPORTANTE: Book es una entidad JPA (@Entity en core.model)
 * No usamos BookEntity - el modelo de dominio ES la entidad JPA
 * 
 * Spring Data genera automaticamente:
 * - save(entity): INSERT or UPDATE
 * - findById(id): SELECT by PK
 * - findAll(): SELECT all
 * - delete(entity): DELETE
 * - count(): SELECT COUNT(*)
 * 
 * Este repositorio agrega consultas personalizadas JPQL/SQL.
 * 
 * @author DOSW-Library Team
 */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    /**
     * Query method automatico: SELECT * FROM books WHERE title = ?
     */
    Optional<Book> findByTitle(String title);

    /**
     * Query method automatico: SELECT * FROM books WHERE author LIKE %?%
     */
    List<Book> findByAuthorContaining(String author);

    /**
     * Consulta JPQL personalizada: libros con al menos 1 copia disponible
     * 
     * @return lista de libros que se pueden prestar
     */
    @Query("SELECT b FROM Book b WHERE b.available > 0 ORDER BY b.available DESC")
    List<Book> findAvailableBooks();

    /**
     * Consulta JPQL personalizada: libros sin copias
     * 
     * @return lista de libros agotados
     */
    @Query("SELECT b FROM Book b WHERE b.available = 0")
    List<Book> findOutOfStockBooks();

    /**
     * Verificar si un libro existe y tiene copias disponibles
     * 
     * @param bookId identificador del libro
     * @return true si existe y hay copias disponibles
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 AND b.available > 0 THEN true ELSE false END FROM Book b WHERE b.id = :bookId")
    boolean isBookAvailable(@Param("bookId") String bookId);

    /**
     * Obtener cantidad de copias disponibles de un libro
     * 
     * @param bookId identificador del libro
     * @return cantidad disponible (0 si no existe)
     */
    @Query("SELECT COALESCE(b.available, 0) FROM Book b WHERE b.id = :bookId")
    Integer getAvailableCopies(@Param("bookId") String bookId);
}
