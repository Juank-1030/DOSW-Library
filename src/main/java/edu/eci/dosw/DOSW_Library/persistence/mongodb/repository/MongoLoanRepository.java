package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.LoanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz Spring Data MongoDB para la colección de préstamos.
 * Proporciona operaciones CRUD y consultas personalizadas para LoanDocument.
 *
 * Implementa estrategia HÍBRIDA:
 * - REFERENCIADO: userRef, bookRef (denormalizados para consultas sin join)
 * - EMBEBIDO: history[] (auditoría sin tabla separada)
 */
public interface MongoLoanRepository extends MongoRepository<LoanDocument, String> {

    @Query("{ 'userRef.userId' : ?0 }")
    List<LoanDocument> findByUserId(String userId);

    @Query("{ 'userRef.userId' : ?0, 'status' : ?1 }")
    List<LoanDocument> findByUserIdAndStatus(String userId, String status);

    @Query("{ 'bookRef.bookId' : ?0 }")
    List<LoanDocument> findByBookId(String bookId);

    @Query("{ 'bookRef.bookId' : ?0, 'status' : ?1 }")
    List<LoanDocument> findByBookIdAndStatus(String bookId, String status);

    @Query("{ 'status' : ?0 }")
    List<LoanDocument> findByStatus(String status);

    @Query("{ 'dueDate' : { $lt : ?0 }, 'status' : 'ACTIVE' }")
    List<LoanDocument> findOverdueLoans(LocalDateTime now);

    @Query("{ 'dueDate' : { $gte : ?0, $lt : ?1 }, 'status' : 'ACTIVE' }")
    List<LoanDocument> findUpcomingDueLoans(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'loanDate' : { $gte : ?0, $lt : ?1 } }")
    List<LoanDocument> findLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "{ 'userRef.userId' : ?0, 'bookRef.bookId' : ?1 }", sort = "{ 'createdAt' : -1 }")
    Optional<LoanDocument> findMostRecentLoanByUserAndBook(String userId, String bookId);

    @Query("{ 'userRef.userId' : ?0, 'status' : ?1 }")
    long countByUserIdAndStatus(String userId, String status);

    @Query("{ 'returnDate' : { $gte : ?0, $lt : ?1 }, 'status' : 'RETURNED' }")
    List<LoanDocument> findReturnedLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
