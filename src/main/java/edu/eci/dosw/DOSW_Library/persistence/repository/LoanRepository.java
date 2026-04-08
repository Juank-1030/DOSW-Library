package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para Loan
 * 
 * IMPORTANTE: Loan es una entidad JPA (@Entity en core.model)
 * No usamos LoanEntity - el modelo de dominio ES la entidad JPA
 * 
 * Proporciona:
 * - CRUD automatico heredado de JpaRepository
 * - Query methods para buscar prestamos por usuario, libro, estado
 * - Consultas JPQL personalizadas para reportes y validaciones
 * 
 * @author DOSW-Library Team
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {

    /**
     * Obtener prestamos activos de un usuario
     * 
     * @param userId identificador del usuario
     * @return lista de prestamos vigentes del usuario
     */
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.status = 'ACTIVE'")
    List<Loan> findActiveLoansForUser(@Param("userId") String userId);

    /**
     * Contar prestamos activos de un usuario
     * Util para validar limite de prestamos simultaneos
     * 
     * @param userId identificador del usuario
     * @return cantidad de prestamos activos
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status = 'ACTIVE'")
    Integer countActiveLoansForUser(@Param("userId") String userId);

    /**
     * Verificar si un usuario tiene prestado un libro especifico (aun activo)
     * 
     * @param userId identificador del usuario
     * @param bookId identificador del libro
     * @return true si tiene un prestamo activo de ese libro
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId AND l.status = 'ACTIVE'")
    boolean hasActiveLoan(@Param("userId") String userId, @Param("bookId") String bookId);

    /**
     * Obtener prestamos vencidos (atrasados)
     * 
     * @param now fecha/hora actual
     * @return lista de prestamos con dueDate < ahora y aun no devueltos
     */
    @Query("SELECT l FROM Loan l WHERE l.dueDate < :now AND l.status = 'ACTIVE'")
    List<Loan> findOverdueLoans(@Param("now") LocalDateTime now);

    /**
     * Contar prestamos atrasados totales en el sistema
     * 
     * @param now fecha/hora actual
     * @return cantidad de prestamos en mora
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.dueDate < :now AND l.status = 'ACTIVE'")
    Integer countOverdueLoans(@Param("now") LocalDateTime now);

    /**
     * Obtener todos los prestamos (activos y devueltos) de un usuario
     * 
     * @param userId identificador del usuario
     * @return lista completa de prestamos del usuario, ordenada por fecha
     *         descendente
     */
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId ORDER BY l.loanDate DESC")
    List<Loan> findAllLoansForUser(@Param("userId") String userId);

    /**
     * Obtener el ultimo prestamo de un usuario para un libro especifico
     * Util para historial y validaciones
     * 
     * @param userId identificador del usuario
     * @param bookId identificador del libro
     * @return Optional del prestamo mas reciente
     */
    @Query(value = "SELECT l FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId ORDER BY l.loanDate DESC LIMIT 1")
    Optional<Loan> findLastLoanForUserAndBook(@Param("userId") String userId, @Param("bookId") String bookId);
}
