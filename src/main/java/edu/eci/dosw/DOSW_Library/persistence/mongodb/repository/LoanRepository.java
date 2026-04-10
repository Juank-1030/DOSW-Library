package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.LoanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio MongoDB para la colección de préstamos.
 * Proporciona operaciones CRUD y consultas personalizadas para LoanDocument.
 *
 * Implementa estrategia HÍBRIDA:
 * - REFERENCIADO: userRef, bookRef (denormalizados para consultas sin join)
 * - EMBEBIDO: history[] (auditoría sin tabla separada)
 *
 * Métodos heredados de MongoRepository:
 * - save(LoanDocument) → LoanDocument
 * - saveAll(Iterable) → Iterable
 * - findById(String) → Optional
 * - findAll() → List
 * - deleteById(String) → void
 * - delete(LoanDocument) → void
 * - count() → long
 */
@Repository
public interface LoanRepository extends MongoRepository<LoanDocument, String> {

    /**
     * Busca todos los préstamos de un usuario específico.
     * Utiliza índice compuesto: (userRef.userId, status)
     *
     * @param userId el ID del usuario
     * @return lista de préstamos del usuario
     */
    @Query("{ 'userRef.userId' : ?0 }")
    List<LoanDocument> findByUserId(String userId);

    /**
     * Busca los préstamos activos de un usuario.
     * Utiliza índice compuesto: (userRef.userId, status)
     *
     * @param userId el ID del usuario
     * @param status el estado del préstamo (ej: "ACTIVE", "ONGOING")
     * @return lista de préstamos activos
     */
    @Query("{ 'userRef.userId' : ?0, 'status' : ?1 }")
    List<LoanDocument> findByUserIdAndStatus(String userId, String status);

    /**
     * Busca todos los préstamos de un libro específico.
     * Utiliza índice compuesto: (bookRef.bookId, status)
     *
     * @param bookId el ID del libro
     * @return lista de préstamos del libro
     */
    @Query("{ 'bookRef.bookId' : ?0 }")
    List<LoanDocument> findByBookId(String bookId);

    /**
     * Busca los préstamos activos de un libro.
     * Utiliza índice compuesto: (bookRef.bookId, status)
     *
     * @param bookId el ID del libro
     * @param status el estado del préstamo
     * @return lista de préstamos activos del libro
     */
    @Query("{ 'bookRef.bookId' : ?0, 'status' : ?1 }")
    List<LoanDocument> findByBookIdAndStatus(String bookId, String status);

    /**
     * Busca todos los préstamos con un estado específico.
     * Utiliza índice simple: status
     *
     * @param status el estado a buscar
     * @return lista de préstamos con ese estado
     */
    @Query("{ 'status' : ?0 }")
    List<LoanDocument> findByStatus(String status);

    /**
     * Busca los préstamos vencidos (dueDate en el pasado, estado ACTIVE).
     * Utiliza índice compuesto: (dueDate, status)
     *
     * @param now la fecha/hora actual
     * @return lista de préstamos vencidos
     */
    @Query("{ 'dueDate' : { $lt : ?0 }, 'status' : 'ACTIVE' }")
    List<LoanDocument> findOverdueLoans(LocalDateTime now);

    /**
     * Busca los préstamos próximos a vencer (dentro de N días).
     *
     * @param startDate la fecha actual
     * @param endDate   la fecha límite
     * @return lista de préstamos próximos a vencer
     */
    @Query("{ 'dueDate' : { $gte : ?0, $lt : ?1 }, 'status' : 'ACTIVE' }")
    List<LoanDocument> findUpcomingDueLoans(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca los préstamos en rango de fechas.
     *
     * @param startDate fecha inicio del rango
     * @param endDate   fecha fin del rango
     * @return lista de préstamos en el rango
     */
    @Query("{ 'loanDate' : { $gte : ?0, $lt : ?1 } }")
    List<LoanDocument> findLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca el préstamo más reciente de un usuario y libro específicos.
     *
     * @param userId el ID del usuario
     * @param bookId el ID del libro
     * @return Optional con el préstamo más reciente
     */
    @Query(value = "{ 'userRef.userId' : ?0, 'bookRef.bookId' : ?1 }", sort = "{ 'createdAt' : -1 }")
    Optional<LoanDocument> findMostRecentLoanByUserAndBook(String userId, String bookId);

    /**
     * Cuenta los préstamos activos de un usuario.
     *
     * @param userId el ID del usuario
     * @param status el estado a contar
     * @return cantidad de préstamos
     */
    @Query("{ 'userRef.userId' : ?0, 'status' : ?1 }")
    long countByUserIdAndStatus(String userId, String status);

    /**
     * Busca todos los préstamos devueltos en un rango de fechas.
     *
     * @param startDate fecha inicio
     * @param endDate   fecha fin
     * @return lista de préstamos devueltos
     */
    @Query("{ 'returnDate' : { $gte : ?0, $lt : ?1 }, 'status' : 'RETURNED' }")
    List<LoanDocument> findReturnedLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate);

}
