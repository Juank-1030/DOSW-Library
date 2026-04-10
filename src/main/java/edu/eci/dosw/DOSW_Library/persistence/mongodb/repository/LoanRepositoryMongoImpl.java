package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.persistence.mongodb.mapper.LoanDocumentMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.LoanRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación MongoDB de LoanRepository.
 * Esta implementación está activa cuando el perfil "mongo" está habilitado.
 *
 * Proporciona todas las operaciones CRUD y consultas especializadas usando
 * MongoDB.
 * La conversión entre documentos y dominios se realiza mediante
 * LoanDocumentMapper.
 *
 * @see LoanRepository - Interfaz genérica
 * @see Profile - Esta implementación solo se activa con @Profile("mongo")
 */
@Repository
@Profile("mongo")
public class LoanRepositoryMongoImpl implements LoanRepository {

    private final MongoLoanRepository repository;
    private final LoanDocumentMapper mapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repository Repositorio MongoDB generado por Spring Data
     * @param mapper     Mapper para conversiones Document ↔ Domain
     */
    public LoanRepositoryMongoImpl(MongoLoanRepository repository, LoanDocumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Loan save(Loan loan) {
        return mapper.toDomain(
                repository.save(mapper.toDocument(loan)));
    }

    @Override
    public List<Loan> saveAll(List<Loan> loans) {
        return repository.saveAll(
                loans.stream()
                        .map(mapper::toDocument)
                        .toList())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Loan> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findByUserId(String userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findByUserIdAndStatus(String userId, String status) {
        return repository.findByUserIdAndStatus(userId, status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findByBookId(String bookId) {
        return repository.findByBookId(bookId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findByBookIdAndStatus(String bookId, String status) {
        return repository.findByBookIdAndStatus(bookId, status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findByStatus(String status) {
        return repository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findOverdueLoans(LocalDateTime baseDate) {
        return repository.findOverdueLoans(baseDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findUpcomingDueLoans(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findUpcomingDueLoans(startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Loan> findLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findLoansByDateRange(startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Loan> findMostRecentLoanByUserAndBook(String userId, String bookId) {
        return repository.findMostRecentLoanByUserAndBook(userId, bookId)
                .map(mapper::toDomain);
    }

    @Override
    public long countByUserIdAndStatus(String userId, String status) {
        return repository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Loan> findReturnedLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findReturnedLoansByDateRange(startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Loan loan) {
        if (loan != null && loan.getId() != null) {
            repository.deleteById(loan.getId());
        }
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}
