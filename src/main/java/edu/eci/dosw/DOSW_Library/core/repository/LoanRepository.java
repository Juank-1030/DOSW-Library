package edu.eci.dosw.DOSW_Library.core.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gestionar persistencia de préstamos.
 * 
 * <p>
 * <b>Características especiales:</b>
 * </p>
 * <ul>
 * <li>Maneja relaciones con Book y User (@ManyToOne)</li>
 * <li>Consultas por estado (ACTIVE, RETURNED)</li>
 * <li>Consultas por fechas</li>
 * <li>Agregaciones (COUNT, AVG)</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {

    // ============================================
    // CONSULTAS POR USUARIO
    // ============================================

    /**
     * Obtiene todos los préstamos de un usuario.
     * 
     * <p>
     * <b>SQL generado (con JOIN automático):</b>
     * </p>
     * 
     * <pre>
     * SELECT l.* FROM loans l 
     * INNER JOIN users u ON l.user_id = u.id 
     * WHERE u.id = ?
     * </pre>
     * 
     * @param userId ID del usuario
     * @return Lista de préstamos del usuario
     */
    List<Loan> findByUserId(String userId);

    /**
     * Obtiene préstamos activos de un usuario.
     * 
     * <p>
     * <b>Uso crítico:</b> Validar límite de 3 préstamos activos
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * List<Loan> activeLoans = loanRepository.findByUserIdAndStatus(userId, LoanStatus.ACTIVE);
     * if (activeLoans.size() >= 3) {
     *     throw new LoanLimitExceededException(...);
     * }
     * }</pre>
     * 
     * @param userId ID del usuario
     * @param status Estado del préstamo (ACTIVE o RETURNED)
     * @return Lista de préstamos con ese estado
     */
    List<Loan> findByUserIdAndStatus(String userId, LoanStatus status);

    /**
     * Cuenta préstamos activos de un usuario.
     * 
     * <p>
     * <b>SQL generado:</b>
     * </p>
     * 
     * <pre>
     * SELECT COUNT(*) FROM loans l 
     * WHERE l.user_id = ? AND l.status = 'ACTIVE'
     * </pre>
     * 
     * <p>
     * <b>Ventaja:</b> Más eficiente que cargar lista completa
     * </p>
     * 
     * @param userId ID del usuario
     * @param status Estado del préstamo
     * @return Cantidad de préstamos con ese estado
     */
    long countByUserIdAndStatus(String userId, LoanStatus status);

    // ============================================
    // CONSULTAS POR LIBRO
    // ============================================

    /**
     * Obtiene todos los préstamos de un libro.
     * 
     * @param bookId ID del libro
     * @return Lista de préstamos del libro
     */
    List<Loan> findByBookId(String bookId);

    /**
     * Obtiene préstamos activos de un libro.
     * 
     * @param bookId ID del libro
     * @param status Estado del préstamo
     * @return Lista de préstamos activos del libro
     */
    List<Loan> findByBookIdAndStatus(String bookId, LoanStatus status);

    /**
     * Cuenta préstamos activos de un libro.
     * 
     * @param bookId ID del libro
     * @param status Estado del préstamo
     * @return Cantidad de préstamos activos
     */
    long countByBookIdAndStatus(String bookId, LoanStatus status);

    // ============================================
    // CONSULTAS POR ESTADO
    // ============================================

    /**
     * Obtiene todos los préstamos con un estado específico.
     * 
     * @param status Estado del préstamo
     * @return Lista de préstamos con ese estado
     */
    List<Loan> findByStatus(LoanStatus status);

    /**
     * Cuenta préstamos por estado.
     * 
     * @param status Estado del préstamo
     * @return Cantidad de préstamos con ese estado
     */
    long countByStatus(LoanStatus status);

    // ============================================
    // CONSULTAS POR FECHA
    // ============================================

    /**
     * Obtiene préstamos realizados en una fecha específica.
     * 
     * @param loanDate Fecha del préstamo
     * @return Lista de préstamos de esa fecha
     */
    List<Loan> findByLoanDate(LocalDate loanDate);

    /**
     * Obtiene préstamos realizados después de una fecha.
     * 
     * @param date Fecha límite
     * @return Lista de préstamos posteriores a la fecha
     */
    List<Loan> findByLoanDateAfter(LocalDate date);

    /**
     * Obtiene préstamos realizados antes de una fecha.
     * 
     * @param date Fecha límite
     * @return Lista de préstamos anteriores a la fecha
     */
    List<Loan> findByLoanDateBefore(LocalDate date);

    /**
     * Obtiene préstamos realizados en un rango de fechas.
     * 
     * @param startDate Fecha inicial
     * @param endDate   Fecha final
     * @return Lista de préstamos en el rango
     */
    List<Loan> findByLoanDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene préstamos devueltos en una fecha específica.
     * 
     * @param returnDate Fecha de devolución
     * @return Lista de préstamos devueltos en esa fecha
     */
    List<Loan> findByReturnDate(LocalDate returnDate);

    // ============================================
    // CONSULTAS COMBINADAS (usuario + libro)
    // ============================================

    /**
     * Busca préstamo activo de un usuario para un libro específico.
     * 
     * <p>
     * <b>Uso crítico:</b> Validar que usuario no tenga préstamo duplicado
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * Optional<Loan> existingLoan = loanRepository.findByUserIdAndBookIdAndStatus(
     *         userId, bookId, LoanStatus.ACTIVE);
     * if (existingLoan.isPresent()) {
     *     throw new IllegalStateException("User already has active loan for this book");
     * }
     * }</pre>
     * 
     * @param userId ID del usuario
     * @param bookId ID del libro
     * @param status Estado del préstamo
     * @return Optional con el préstamo si existe
     */
    Optional<Loan> findByUserIdAndBookIdAndStatus(String userId, String bookId, LoanStatus status);

    /**
     * Verifica si existe préstamo activo de un usuario para un libro.
     * 
     * @param userId ID del usuario
     * @param bookId ID del libro
     * @param status Estado del préstamo
     * @return true si existe, false en caso contrario
     */
    boolean existsByUserIdAndBookIdAndStatus(String userId, String bookId, LoanStatus status);

    // ============================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ============================================

    /**
     * Obtiene préstamos activos con información completa (JOIN FETCH).
     * 
     * <p>
     * <b>JPQL con JOIN FETCH:</b> Carga relaciones en una sola consulta
     * </p>
     * 
     * <pre>
     * SELECT l FROM Loan l 
     * JOIN FETCH l.book 
     * JOIN FETCH l.user 
     * WHERE l.status = 'ACTIVE'
     * </pre>
     * 
     * <p>
     * <b>Ventaja:</b> Evita problema N+1 queries
     * </p>
     * <p>
     * <b>Uso:</b> Cuando necesitas convertir a DTO con información de libro y
     * usuario
     * </p>
     * 
     * @return Lista de préstamos activos con relaciones cargadas
     */
    @Query("SELECT l FROM Loan l " +
            "JOIN FETCH l.book " +
            "JOIN FETCH l.user " +
            "WHERE l.status = :status")
    List<Loan> findAllByStatusWithDetails(@Param("status") LoanStatus status);

    /**
     * Obtiene préstamos de un usuario con información completa.
     * 
     * @param userId ID del usuario
     * @return Lista de préstamos del usuario con relaciones cargadas
     */
    @Query("SELECT l FROM Loan l " +
            "JOIN FETCH l.book " +
            "JOIN FETCH l.user " +
            "WHERE l.user.id = :userId")
    List<Loan> findByUserIdWithDetails(@Param("userId") String userId);

    /**
     * Obtiene préstamos activos de un usuario con información completa.
     * 
     * <p>
     * <b>Uso típico:</b> Mostrar préstamos activos en perfil de usuario
     * </p>
     * 
     * @param userId ID del usuario
     * @param status Estado del préstamo
     * @return Lista de préstamos con relaciones cargadas
     */
    @Query("SELECT l FROM Loan l " +
            "JOIN FETCH l.book " +
            "JOIN FETCH l.user " +
            "WHERE l.user.id = :userId AND l.status = :status")
    List<Loan> findByUserIdAndStatusWithDetails(
            @Param("userId") String userId,
            @Param("status") LoanStatus status);

    /**
     * Obtiene préstamos vencidos (más de 30 días sin devolver).
     * 
     * <p>
     * <b>JPQL:</b>
     * </p>
     * 
     * <pre>
     * SELECT l FROM Loan l 
     * WHERE l.status = 'ACTIVE' 
     * AND l.loanDate < :cutoffDate
     * </pre>
     * 
     * <p>
     * <b>Ejemplo de uso:</b>
     * </p>
     * 
     * <pre>{@code
     * LocalDate cutoffDate = LocalDate.now().minusDays(30);
     * List<Loan> overdue = loanRepository.findOverdueLoans(cutoffDate);
     * }</pre>
     * 
     * @param cutoffDate Fecha límite (ej: hace 30 días)
     * @return Lista de préstamos vencidos
     */
    @Query("SELECT l FROM Loan l " +
            "WHERE l.status = 'ACTIVE' AND l.loanDate < :cutoffDate")
    List<Loan> findOverdueLoans(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * Obtiene libros más prestados (estadística).
     * 
     * <p>
     * <b>JPQL con GROUP BY y ORDER BY:</b>
     * </p>
     * 
     * <pre>
     * SELECT l.book.id, l.book.title, COUNT(l) 
     * FROM Loan l 
     * GROUP BY l.book.id, l.book.title 
     * ORDER BY COUNT(l) DESC
     * </pre>
     * 
     * <p>
     * <b>Uso:</b> Reportes, estadísticas, recomendaciones
     * </p>
     * 
     * @param limit Cantidad máxima de resultados
     * @return Lista de IDs de libros ordenados por popularidad
     */
    @Query("SELECT l.book.id FROM Loan l " +
            "GROUP BY l.book.id " +
            "ORDER BY COUNT(l) DESC")
    List<String> findMostBorrowedBooks();

    /**
     * Cuenta total de préstamos por usuario.
     * 
     * <p>
     * <b>JPQL:</b>
     * </p>
     * 
     * <pre>
     * SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId
     * </pre>
     * 
     * @param userId ID del usuario
     * @return Cantidad total de préstamos del usuario (activos + devueltos)
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId")
    long countTotalLoansByUser(@Param("userId") String userId);

    /**
     * Obtiene préstamos devueltos en un rango de fechas.
     * 
     * @param startDate Fecha inicial
     * @param endDate   Fecha final
     * @return Lista de préstamos devueltos en el rango
     */
    @Query("SELECT l FROM Loan l " +
            "WHERE l.status = 'RETURNED' " +
            "AND l.returnDate BETWEEN :startDate AND :endDate")
    List<Loan> findReturnedLoansBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // ============================================
    // CONSULTAS DE ESTADÍSTICAS
    // ============================================

    /**
     * Cuenta total de préstamos activos en el sistema.
     * 
     * @return Cantidad de préstamos activos
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE'")
    long countActiveLoans();

    /**
     * Cuenta total de préstamos devueltos.
     * 
     * @return Cantidad de préstamos devueltos
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'RETURNED'")
    long countReturnedLoans();

    /**
     * Calcula promedio de días de préstamo.
     * 
     * <p>
     * <b>JPQL con función de fecha:</b>
     * </p>
     * 
     * <pre>
     * SELECT AVG(DATEDIFF(l.returnDate, l.loanDate)) 
     * FROM Loan l 
     * WHERE l.status = 'RETURNED'
     * </pre>
     * 
     * <p>
     * <b>Nota:</b> DATEDIFF puede variar según BD (H2, MySQL, PostgreSQL)
     * </p>
     * 
     * @return Promedio de días de préstamo
     */
    @Query("SELECT AVG(FUNCTION('DATEDIFF', l.returnDate, l.loanDate)) " +
            "FROM Loan l WHERE l.status = 'RETURNED'")
    Double calculateAverageLoanDuration();
}