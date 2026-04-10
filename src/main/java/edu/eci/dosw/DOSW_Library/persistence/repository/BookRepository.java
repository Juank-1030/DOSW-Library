package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para operaciones de persistencia sobre Book.
 * Define un contrato que puede ser implementado por diferentes fuentes de datos
 * (MongoDB, JPA/PostgreSQL, etc.)
 *
 * Esta es la interfaz de abstracción de persistencia.
 * Las implementaciones concretas (MongoDB, JPA) deben implementar estos
 * métodos.
 */
public interface BookRepository {

    /**
     * Guarda un nuevo libro o actualiza uno existente.
     *
     * @param book el libro a guardar
     * @return el libro guardado
     */
    Book save(Book book);

    /**
     * Guarda múltiples libros en lote.
     *
     * @param books lista de libros a guardar
     * @return lista de libros guardados
     */
    List<Book> saveAll(List<Book> books);

    /**
     * Busca un libro por su ID.
     *
     * @param id el identificador del libro
     * @return Optional con el libro si existe
     */
    Optional<Book> findById(String id);

    /**
     * Obtiene todos los libros.
     *
     * @return lista de todos los libros
     */
    List<Book> findAll();

    /**
     * Busca un libro por su título exacto.
     *
     * @param title el título del libro
     * @return Optional con el libro si existe
     */
    Optional<Book> findByTitle(String title);

    /**
     * Busca un libro por su ISBN.
     *
     * @param isbn el código ISBN
     * @return Optional con el libro si existe
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Busca todos los libros de un autor específico.
     *
     * @param author el nombre del autor
     * @return lista de libros del autor
     */
    List<Book> findByAuthor(String author);

    /**
     * Busca todos los libros de una categoría específica.
     *
     * @param category la categoría del libro
     * @return lista de libros en esa categoría
     */
    List<Book> findByCategory(String category);

    /**
     * Busca libros por título parcial (búsqueda flexible).
     *
     * @param titlePattern patrón de búsqueda
     * @return lista de libros coincidentes
     */
    List<Book> findByTitleContaining(String titlePattern);

    /**
     * Busca libros por autor parcial (búsqueda flexible).
     *
     * @param authorPattern patrón de búsqueda
     * @return lista de libros coincidentes
     */
    List<Book> findByAuthorContaining(String authorPattern);

    /**
     * Busca libros disponibles (con inventario > 0).
     *
     * @return lista de libros disponibles
     */
    List<Book> findAvailableBooks();

    /**
     * Busca libros agotados (inventario = 0).
     *
     * @return lista de libros sin inventario
     */
    List<Book> findOutOfStockBooks();

    /**
     * Busca libros con inventario bajo (menos de N copias).
     *
     * @param threshold cantidad mínima
     * @return lista de libros con poco inventario
     */
    List<Book> findLowInventoryBooks(int threshold);

    /**
     * Busca libros disponibles de una categoría específica.
     *
     * @param category la categoría
     * @return lista de libros disponibles en esa categoría
     */
    List<Book> findAvailableBooksByCategory(String category);

    /**
     * Busca libros disponibles de un autor específico.
     *
     * @param author el autor
     * @return lista de libros disponibles del autor
     */
    List<Book> findAvailableBooksByAuthor(String author);

    /**
     * Verifica si un ISBN ya existe.
     *
     * @param isbn el ISBN a validar
     * @return true si el ISBN ya existe
     */
    boolean existsByIsbn(String isbn);

    /**
     * Busca libros más solicitados (por número de préstamos históricos).
     *
     * @return lista de libros con mayor demanda
     */
    List<Book> findMostRequestedBooks();

    /**
     * Busca libros por rango de precio.
     *
     * @param minPrice precio mínimo
     * @param maxPrice precio máximo
     * @return lista de libros en el rango
     */
    List<Book> findByPriceRange(double minPrice, double maxPrice);

    /**
     * Elimina un libro por su ID.
     *
     * @param id el identificador del libro a eliminar
     */
    void deleteById(String id);

    /**
     * Elimina un libro.
     *
     * @param book el libro a eliminar
     */
    void delete(Book book);

    /**
     * Elimina todos los libros.
     */
    void deleteAll();

    /**
     * Cuenta la cantidad total de libros.
     *
     * @return cantidad de libros
     */
    long count();

    /**
     * Verifica si existe un libro con el ID especificado.
     *
     * @param id el identificador a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsById(String id);

}
