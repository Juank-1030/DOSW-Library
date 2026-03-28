package edu.eci.dosw.DOSW_Library.core.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, String> {

    List<Loan> findByUser_IdAndStatus(String userId, LoanStatus status);
}
