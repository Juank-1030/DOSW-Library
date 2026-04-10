package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para operaciones de persistencia sobre Loan.
 * Define un contrato que puede ser implementado por diferentes fuentes de datos
 * (MongoDB, JPA/PostgreSQL, etc.)
 *
 * Esta es la interfaz de abstracción de persistencia.
 * Las implementaciones concretas (MongoDB, JPA) deben implementar estos
 * métodos.
 */
public interface LoanRepository {

    /**
     * Guarda un nuevo préstamo o actualiza uno existente.
     *
     * @param loan el préstamo a guardar
     * @return el préstamo guardado
     */
    Loan save(Loan loan);

    /**
     * Guarda múltiples préstamos en lote.
     *
     * @param loans lista de préstamos a guardar
     * @return lista de préstamos guardados
     */
    List<Loan> saveAll(List<Loan> loans);

    /**
     * Busca un préstamo por su ID.
     *
     * @param id el identificador del préstamo
     * @return Optional con el préstamo si existe
     */
    Optional<Loan> findById(String id);

    /**
     * Obtiene todos los préstamos.
     *
     * @return lista de todos los préstamos
     */
    List<Loan> findAll();

    /**
     * Busca todos los préstamos de un usuario específico.
     *
     * @param userId el ID del usuario
     * @return lista de préstamos del usuario
     */
    List<Loan> findByUserId(String userId);

    /**
     * Busca préstamos activos de un usuario.
     *
     * @param userId el ID del usuario
     * @param status el estado del préstamo
     * @return lista de préstamos con ese estado
     */
    List<Loan> findByUserIdAndStatus(String userId, String status);

    /**
     * Busca todos los préstamos de un libro específico.
     *
     * @param bookId el ID del libro
     * @return lista de préstamos del libro
     */
    List<Loan> findByBookId(String bookId);

    /**
     * Busca préstamos activos de un libro.
     *
     * @param bookId el ID del libro
     * @param status el estado del préstamo
     * @return lista de préstamos con ese estado
     */
    List<Loan> findByBookIdAndStatus(String bookId, String status);

    /**
     * Busca todos los préstamos con un estado específico.
     *
     * @param status el estado a buscar
     * @return lista de préstamos con ese estado
     */
    List<Loan> findByStatus(String status);

    /**
     * Busca los préstamos vencidos (dueDate en el pasado, estado ACTIVE).
     *
     * @param now la fecha/hora actual
     * @return lista de préstamos vencidos
     */
    List<Loan> findOverdueLoans(LocalDateTime now);

    /**
     * Busca los préstamos próximos a vencer (dentro de N días).
     *
     * @param startDate la fecha actual
     * @param endDate   la fecha límite
     * @return lista de préstamos próximos a vencer
     */
    List<Loan> findUpcomingDueLoans(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca los préstamos en rango de fechas.
     *
     * @param startDate fecha inicio del rango
     * @param endDate   fecha fin del rango
     * @return lista de préstamos en el rango
     */
    List<Loan> findLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca el préstamo más reciente de un usuario y libro específicos.
     *
     * @param userId el ID del usuario
     * @param bookId el ID del libro
     * @return Optional con el préstamo más reciente
     */
    Optional<Loan> findMostRecentLoanByUserAndBook(String userId, String bookId);

    /**
     * Cuenta los préstamos activos de un usuario.
     *
     * @param userId el ID del usuario
     * @param status el estado a contar
     * @return cantidad de préstamos
     */
    long countByUserIdAndStatus(String userId, String status);

    /**
     * Busca todos los préstamos devueltos en un rango de fechas.
     *
     * @param startDate fecha inicio
     * @param endDate   fecha fin
     * @return lista de préstamos devueltos
     */
    List<Loan> findReturnedLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Elimina un préstamo por su ID.
     *
     * @param id el identificador del préstamo a eliminar
     */
    void deleteById(String id);

    /**
     * Elimina un préstamo.
     *
     * @param loan el préstamo a eliminar
     */
    void delete(Loan loan);

    /**
     * Elimina todos los préstamos.
     */
    void deleteAll();

    /**
     * Cuenta la cantidad total de préstamos.
     *
     * @return cantidad de préstamos
     */
    long count();

    /**
     * Verifica si existe un préstamo con el ID especificado.
     *
     * @param id el identificador a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsById(String id);

}
