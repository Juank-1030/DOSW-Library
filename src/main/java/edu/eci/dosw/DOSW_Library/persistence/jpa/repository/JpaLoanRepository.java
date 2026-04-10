package edu.eci.dosw.DOSW_Library.persistence.jpa.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Loan.
 * Spring Data JPA genera la implementación automáticamente.
 *
 * NOTA: Esta es una interfaz interna de la capa JPA.
 * Los servicios usan la interfaz genérica:
 * persistence.repository.LoanRepository
 * que es implementada por LoanRepositoryJpaImpl.
 */
@Repository
public interface JpaLoanRepository extends JpaRepository<Loan, String> {
}
