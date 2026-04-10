package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio MongoDB para la colección de libros.
 * Proporciona operaciones CRUD y consultas personalizadas para BookDocument.
 *
 * Métodos heredados de MongoRepository:
 * - save(BookDocument) → BookDocument
 * - saveAll(Iterable) → Iterable
 * - findById(String) → Optional
 * - findAll() → List
 * - deleteById(String) → void
 * - delete(BookDocument) → void
 * - count() → long
 */
@Repository
public interface BookRepository extends MongoRepository<BookDocument, String> {

    /**
     * Busca un libro por su título exacto.
     *
     * @param title el título del libro
     * @return Optional con el libro si existe
     */
    @Query("{ 'title' : ?0 }")
    Optional<BookDocument> findByTitle(String title);

    /**
     * Busca un libro por su ISBN (identificador único).
     *
     * @param isbn el código ISBN
     * @return Optional con el libro si existe
     */
    @Query("{ 'isbn' : ?0 }")
    Optional<BookDocument> findByIsbn(String isbn);

    /**
     * Busca todos los libros de un autor específico.
     *
     * @param author el nombre del autor
     * @return lista de libros del autor
     */
    @Query("{ 'author' : ?0 }")
    List<BookDocument> findByAuthor(String author);

    /**
     * Busca todos los libros de una categoría específica.
     *
     * @param category la categoría del libro
     * @return lista de libros en esa categoría
     */
    @Query("{ 'category' : ?0 }")
    List<BookDocument> findByCategory(String category);

    /**
     * Busca libros por título parcial (búsqueda flexible).
     *
     * @param titlePattern patrón de búsqueda
     * @return lista de libros coincidentes
     */
    @Query("{ 'title' : { $regex : ?0, $options : 'i' } }")
    List<BookDocument> findByTitleContaining(String titlePattern);

    /**
     * Busca libros por autor parcial (búsqueda flexible).
     *
     * @param authorPattern patrón de búsqueda
     * @return lista de libros coincidentes
     */
    @Query("{ 'author' : { $regex : ?0, $options : 'i' } }")
    List<BookDocument> findByAuthorContaining(String authorPattern);

    /**
     * Busca libros disponibles (con inventario > 0).
     *
     * @return lista de libros disponibles
     */
    @Query("{ 'inventory' : { $gt : 0 } }")
    List<BookDocument> findAvailableBooks();

    /**
     * Busca libros agotados (inventario = 0).
     *
     * @return lista de libros sin inventario
     */
    @Query("{ 'inventory' : 0 }")
    List<BookDocument> findOutOfStockBooks();

    /**
     * Busca libros con inventario bajo (menos de N copias).
     *
     * @param threshold cantidad mínima
     * @return lista de libros con poco inventario
     */
    @Query("{ 'inventory' : { $gt : 0, $lt : ?0 } }")
    List<BookDocument> findLowInventoryBooks(int threshold);

    /**
     * Busca libros disponibles de una categoría específica.
     *
     * @param category la categoría
     * @return lista de libros disponibles en esa categoría
     */
    @Query("{ 'category' : ?0, 'inventory' : { $gt : 0 } }")
    List<BookDocument> findAvailableBooksByCategory(String category);

    /**
     * Busca libros disponibles de un autor específico.
     *
     * @param author el autor
     * @return lista de libros disponibles del autor
     */
    @Query("{ 'author' : ?0, 'inventory' : { $gt : 0 } }")
    List<BookDocument> findAvailableBooksByAuthor(String author);

    /**
     * Verifica si un ISBN ya existe (para validación de registro).
     *
     * @param isbn el ISBN a validar
     * @return true si el ISBN ya existe
     */
    @Query("{ 'isbn' : ?0 }")
    boolean existsByIsbn(String isbn);

    /**
     * Busca libros más solicitados (por número de préstamos históricos).
     *
     * @return lista de libros con mayor demanda
     */
    @Query(value = "{}", sort = "{ 'totalLoans' : -1 }")
    List<BookDocument> findMostRequestedBooks();

    /**
     * Busca libros por rango de precio.
     *
     * @param minPrice precio mínimo
     * @param maxPrice precio máximo
     * @return lista de libros en el rango
     */
    @Query("{ 'price' : { $gte : ?0, $lte : ?1 } }")
    List<BookDocument> findByPriceRange(double minPrice, double maxPrice);

}
