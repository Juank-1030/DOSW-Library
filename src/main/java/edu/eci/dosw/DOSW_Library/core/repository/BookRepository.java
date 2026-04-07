package edu.eci.dosw.DOSW_Library.core.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gestionar persistencia de libros.
 * 
 * <p>
 * <b>Responsabilidades (según diapositivas):</b>
 * </p>
 * <ul>
 * <li>✅ Gestionar operaciones de persistencia (BD)</li>
 * <li>✅ Extender JpaRepository para CRUD automático</li>
 * <li>✅ Definir consultas personalizadas cuando sea necesario</li>
 * <li>✅ NO contiene lógica de negocio</li>
 * <li>✅ Es una interfaz (Spring implementa automáticamente)</li>
 * </ul>
 * 
 * <p>
 * <b>Anotación @Repository:</b>
 * </p>
 * <ul>
 * <li>Marca la interfaz como componente de capa de persistencia</li>
 * <li>Spring Data JPA genera implementación automáticamente</li>
 * <li>Maneja excepciones de BD y las traduce a DataAccessException</li>
 * </ul>
 * 
 * <p>
 * <b>Métodos heredados de JpaRepository (automáticos):</b>
 * </p>
 * <ul>
 * <li>save(Book book) - Guardar o actualizar</li>
 * <li>findById(String id) - Buscar por ID</li>
 * <li>findAll() - Obtener todos</li>
 * <li>deleteById(String id) - Eliminar por ID</li>
 * <li>count() - Contar registros</li>
 * <li>existsById(String id) - Verificar existencia</li>
 * </ul>
 * 
 * <p>
 * <b>Consultas personalizadas:</b>
 * </p>
 * <ul>
 * <li>Método query (findBy...) - Spring genera SQL automáticamente</li>
 * <li>@Query - JPQL/SQL personalizado</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    // ============================================
    // MÉTODOS QUERY (Spring Data genera SQL automáticamente)
    // ============================================

    /**
     * Busca libros por título (búsqueda exacta).
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM books WHERE title = ?
     * </pre>
     * 
     * <p>
     * <b>Ejemplo de uso:</b>
     * </p>
     * 
     * <pre>{@code
     * List<Book> books = bookRepository.findByTitle("Clean Code");
     * }</pre>
     * 
     * @param title Título del libro
     * @return Lista de libros con ese título exacto
     */
    List<Book> findByTitle(String title);

    /**
     * Busca libros cuyo título contenga un texto (búsqueda parcial).
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM books WHERE title LIKE %?%
     * </pre>
     * 
     * <p>
     * <b>Ejemplo de uso:</b>
     * </p>
     * 
     * <pre>{@code
     * List<Book> books = bookRepository.findByTitleContaining("Code");
     * // Retorna: "Clean Code", "Code Complete", etc.
     * }</pre>
     * 
     * @param title Texto a buscar en el título
     * @return Lista de libros cuyo título contiene el texto
     */
    List<Book> findByTitleContaining(String title);

    /**
     * Busca libros por autor.
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM books WHERE author = ?
     * </pre>
     * 
     * @param author Nombre del autor
     * @return Lista de libros de ese autor
     */
    List<Book> findByAuthor(String author);

    /**
     * Busca libros cuyo autor contenga un texto.
     * 
     * @param author Texto a buscar en el autor
     * @return Lista de libros cuyo autor contiene el texto
     */
    List<Book> findByAuthorContaining(String author);

    /**
     * Busca libros por título Y autor (combinación).
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM books WHERE title = ? AND author = ?
     * </pre>
     * 
     * @param title  Título del libro
     * @param author Autor del libro
     * @return Lista de libros que coinciden con ambos criterios
     */
    List<Book> findByTitleAndAuthor(String title, String author);

    /**
     * Busca libros disponibles (available = true).
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM books WHERE available = true
     * </pre>
     * 
     * @param available Estado de disponibilidad
     * @return Lista de libros disponibles/no disponibles
     */
    List<Book> findByAvailable(boolean available);

    /**
     * Busca libros con cantidad de copias mayor a un valor.
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM books WHERE copies > ?
     * </pre>
     * 
     * @param copies Cantidad mínima de copias
     * @return Lista de libros con más copias que el valor dado
     */
    List<Book> findByCopiesGreaterThan(int copies);

    /**
     * Busca libros disponibles con copias mayores a cero.
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM books WHERE available = true AND copies > 0
     * </pre>
     * 
     * @return Lista de libros disponibles con stock
     */
    List<Book> findByAvailableTrueAndCopiesGreaterThan(int copies);

    // ============================================
    // CONSULTAS PERSONALIZADAS CON @Query (JPQL)
    // ============================================

    /**
     * Busca libros por título o autor (búsqueda flexible).
     * 
     * <p>
     * <b>JPQL personalizado:</b>
     * </p>
     * 
     * <pre>
     * SELECT b FROM Book b 
     * WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
     *    OR LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
     * </pre>
     * 
     * <p>
     * <b>Ejemplo de uso:</b>
     * </p>
     * 
     * <pre>{@code
     * List<Book> results = bookRepository.searchByTitleOrAuthor("martin");
     * // Retorna: "Clean Code" (por autor "Robert C. Martin")
     * // "Martin Eden" (por título)
     * }</pre>
     * 
     * @param searchTerm Término de búsqueda (case-insensitive)
     * @return Lista de libros que coinciden en título o autor
     */
    @Query("SELECT b FROM Book b " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> searchByTitleOrAuthor(@Param("searchTerm") String searchTerm);

    /**
     * Obtiene libros ordenados por título.
     * 
     * <p>
     * <b>JPQL personalizado:</b>
     * </p>
     * 
     * <pre>
     * SELECT b FROM Book b ORDER BY b.title ASC
     * </pre>
     * 
     * @return Lista de todos los libros ordenados alfabéticamente por título
     */
    @Query("SELECT b FROM Book b ORDER BY b.title ASC")
    List<Book> findAllOrderByTitle();

    /**
     * Obtiene libros ordenados por autor.
     * 
     * @return Lista de todos los libros ordenados por autor
     */
    @Query("SELECT b FROM Book b ORDER BY b.author ASC")
    List<Book> findAllOrderByAuthor();

    /**
     * Cuenta libros disponibles.
     * 
     * <p>
     * <b>JPQL personalizado:</b>
     * </p>
     * 
     * <pre>
     * SELECT COUNT(b) FROM Book b WHERE b.available = true
     * </pre>
     * 
     * @return Cantidad de libros disponibles
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.available = true")
    long countAvailableBooks();

    /**
     * Cuenta libros de un autor específico.
     * 
     * @param author Nombre del autor
     * @return Cantidad de libros de ese autor
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author = :author")
    long countBooksByAuthor(@Param("author") String author);

    /**
     * Obtiene total de copias en el sistema.
     * 
     * <p>
     * <b>JPQL con función agregada:</b>
     * </p>
     * 
     * <pre>
     * SELECT SUM(b.copies) FROM Book b
     * </pre>
     * 
     * @return Total de copias de todos los libros
     */
    @Query("SELECT SUM(b.copies) FROM Book b")
    Long getTotalCopiesInLibrary();

    // ============================================
    // CONSULTAS NATIVAS (SQL directo - opcional)
    // ============================================

    /**
     * Busca libros usando SQL nativo (útil para consultas complejas).
     * 
     * <p>
     * <b>SQL nativo:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM books 
     * WHERE available = true 
     * ORDER BY copies DESC 
     * LIMIT :limit
     * </pre>
     * 
     * <p>
     * <b>Cuándo usar SQL nativo:</b>
     * </p>
     * <ul>
     * <li>Funciones específicas de la BD (PostgreSQL, MySQL)</li>
     * <li>Optimizaciones de performance</li>
     * <li>Consultas muy complejas difíciles en JPQL</li>
     * </ul>
     * 
     * @param limit Cantidad máxima de resultados
     * @return Lista de libros disponibles ordenados por cantidad de copias
     */
    @Query(value = "SELECT * FROM books WHERE available = true ORDER BY copies DESC LIMIT :limit", nativeQuery = true)
    List<Book> findTopAvailableBooks(@Param("limit") int limit);

    // ============================================
    // MÉTODOS DE VERIFICACIÓN (convenientes)
    // ============================================

    /**
     * Verifica si existe un libro con un título específico.
     * 
     * <p>
     * <b>SQL generado:</b>
     * </p>
     * 
     * <pre>
     * SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END 
     * FROM books b WHERE b.title = ?
     * </pre>
     * 
     * @param title Título a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByTitle(String title);

    /**
     * Verifica si existe un libro de un autor específico.
     * 
     * @param author Autor a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByAuthor(String author);
}