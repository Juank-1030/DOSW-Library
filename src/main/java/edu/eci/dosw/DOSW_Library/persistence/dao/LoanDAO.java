package edu.eci.dosw.DOSW_Library.persistence.dao;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO LoanDAO - Data Access Object para operaciones complejas sobre prestamos
 * 
 * IMPORTANTE: Loan es una entidad JPA (@Entity en core.model)
 * El DAO accede directamente a EntityManager para:
 * - Queries avanzadas de reportes
 * - Updates/Deletes batch
 * - Estadisticas y auditoria
 * 
 * @author DOSW-Library Team
 */
@Component
public class LoanDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Obtener todos los prestamos atrasados (debido date <= ahora, estado ACTIVE)
     * 
     * @return lista de prestamos en mora
     */
    public List<Loan> getOverdueLoans() {
        String jpql = "SELECT l FROM Loan l WHERE l.dueDate <= CURRENT_TIMESTAMP AND l.status = 'ACTIVE' ORDER BY l.dueDate ASC";
        return entityManager.createQuery(jpql, Loan.class)
                .getResultList();
    }

    /**
     * Contar prestamos atrasados actualmente
     * 
     * @return cantidad de prestamos en mora
     */
    public long countOverdueLoans() {
        String jpql = "SELECT COUNT(l) FROM Loan l WHERE l.dueDate <= CURRENT_TIMESTAMP AND l.status = 'ACTIVE'";
        Long result = (Long) entityManager.createQuery(jpql).getSingleResult();
        return result != null ? result : 0L;
    }

    /**
     * Obtener prestamos atrasados de un usuario especifico
     * 
     * @param userId identificador del usuario
     * @return lista de sus prestamos vencidos
     */
    public List<Loan> getOverdueLoansForUser(String userId) {
        String jpql = "SELECT l FROM Loan l WHERE l.user.id = :userId AND l.dueDate <= CURRENT_TIMESTAMP AND l.status = 'ACTIVE'";
        return entityManager.createQuery(jpql, Loan.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Obtener historial completo de prestamos de un usuario (todos: activos +
     * devueltos)
     * Con paginacion manual (simula LIMIT/OFFSET)
     * 
     * @param userId identificador del usuario
     * @param limit  cantidad de registros
     * @param offset salto desde inicio
     * @return lista de prestamos
     */
    public List<Loan> getUserLoanHistory(String userId, int limit, int offset) {
        String jpql = "SELECT l FROM Loan l WHERE l.user.id = :userId ORDER BY l.loanDate DESC";
        return entityManager.createQuery(jpql, Loan.class)
                .setParameter("userId", userId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * Obtener cantidad total de prestamos (activos + devueltos todos los tiempos)
     * 
     * @return total de prestamos en historia
     */
    public long getTotalLoansCount() {
        String jpql = "SELECT COUNT(l) FROM Loan l";
        Long result = (Long) entityManager.createQuery(jpql).getSingleResult();
        return result != null ? result : 0L;
    }

    /**
     * Obtener cantidad de prestamos activos actualmente
     * 
     * @return cantidad de prestamos vigentes
     */
    public long countActiveLoans() {
        String jpql = "SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE'";
        Long result = (Long) entityManager.createQuery(jpql).getSingleResult();
        return result != null ? result : 0L;
    }

    /**
     * Cerrar un prestamo (marcar como devuelto y establecer fecha de devolucion)
     * Operacion atomica: UPDATE + set return_date, cambiar status
     * 
     * @param loanId identificador del prestamo
     */
    @Transactional
    public void closeLoan(String loanId) {
        String sql = "UPDATE loans SET status = 'RETURNED', return_date = CURRENT_TIMESTAMP WHERE id = :id";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id", loanId);
        int updated = query.executeUpdate();

        if (updated == 0) {
            throw new IllegalStateException("Prestamo no encontrado: " + loanId);
        }
    }

    /**
     * Renovar un prestamo: extender due_date otros 14 dias
     * 
     * @param loanId identificador del prestamo
     */
    @Transactional
    public void renewLoan(String loanId) {
        // SQL que suma 14 dias a due_date
        String sql = "UPDATE loans SET due_date = DATE_ADD(due_date, INTERVAL 14 DAY) WHERE id = :id AND status = 'ACTIVE'";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id", loanId);
        int updated = query.executeUpdate();

        if (updated == 0) {
            throw new IllegalStateException("No se pudo renovar el prestamo: " + loanId + " (no activo o inexistente)");
        }
    }

    /**
     * Reporte: libros mas prestados (ranking)
     * 
     * @param limit cantidad de top libros
     * @return lista de (book_id, cantidad_prestamos) ordenada descendente
     */
    public List<?> getTopBorrowedBooks(int limit) {
        String jpql = "SELECT l.book.id, COUNT(l) FROM Loan l GROUP BY l.book.id ORDER BY COUNT(l) DESC";
        return entityManager.createQuery(jpql)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * Reporte: usuarios mas activos (que mas libros han pedido)
     * 
     * @param limit cantidad de top usuarios
     * @return lista de (user_id, cantidad_prestamos)
     */
    public List<?> getMostActiveUsers(int limit) {
        String jpql = "SELECT l.user.id, COUNT(l) FROM Loan l GROUP BY l.user.id ORDER BY COUNT(l) DESC";
        return entityManager.createQuery(jpql)
                .setMaxResults(limit)
                .getResultList();
    }
}
