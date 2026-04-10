package edu.eci.dosw.DOSW_Library.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para acceso a datos de prestamos
 * 
 * Proporciona operaciones CRUD + consultas personalizadas para LoanEntity
 * 
 * Verbo del cambio: Importar de persistence.relational.entity, no core.model
 * 
 * @author DOSW-Library Team
 */
@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    /**
     * Obtener todos los prestamos activos de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de prestamos activos
     */
    @Query("SELECT l FROM LoanEntity l WHERE l.user.id = ?1 AND l.status = 'ACTIVE'")
    List<LoanEntity> findActiveLoansForUser(String userId);

    /**
     * Obtener todos los prestamos (activos o devueltos) de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de todos los prestamos del usuario
     */
    @Query("SELECT l FROM LoanEntity l WHERE l.user.id = ?1")
    List<LoanEntity> findAllLoansForUser(String userId);

    /**
     * Obtener todos los prestamos activos de un libro
     * 
     * @param bookId ID del libro
     * @return Lista de prestamos activos
     */
    @Query("SELECT l FROM LoanEntity l WHERE l.book.id = ?1 AND l.status = 'ACTIVE'")
    List<LoanEntity> findActiveLoansForBook(String bookId);

    /**
     * Obtener prestamosatrasados (vencimiento < ahora y status = ACTIVE)
     * 
     * @return Lista de prestamos en mora
     */
    @Query("SELECT l FROM LoanEntity l WHERE l.dueDate < CURRENT_TIMESTAMP AND l.status = 'ACTIVE'")
    List<LoanEntity> findOverdueLoans();

    /**
     * Contar prestamos activos de un usuario
     * 
     * @param userId ID del usuario
     * @return Cantidad de prestamos activos
     */
    @Query("SELECT COUNT(l) FROM LoanEntity l WHERE l.user.id = ?1 AND l.status = 'ACTIVE'")
    long countActiveLoansForUser(String userId);

    /**
     * Obtener prestamos activos entre dos fechas
     * 
     * @param startDate Fecha inicio
     * @param endDate   Fecha fin
     * @return Lista de prestamos
     */
    @Query("SELECT l FROM LoanEntity l WHERE l.status = 'ACTIVE' AND l.loanDate BETWEEN ?1 AND ?2")
    List<LoanEntity> findActiveLoansInPeriod(LocalDateTime startDate, LocalDateTime endDate);

}
