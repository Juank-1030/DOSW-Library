package edu.eci.dosw.DOSW_Library.persistence.jpa.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.persistence.jpa.mapper.LoanEntityMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.LoanRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de LoanRepository.
 * Esta implementación está activa cuando el perfil "relational" está
 * habilitado.
 *
 * Proporciona todas las operaciones CRUD y consultas especializadas usando JPA.
 * La conversión entre entidades y dominios se realiza mediante
 * LoanEntityMapper.
 *
 * @see LoanRepository - Interfaz genérica
 * @see Profile - Esta implementación solo se activa con @Profile("relational")
 */
@Repository
@Profile("relational")
public class LoanRepositoryJpaImpl implements LoanRepository {

    private final JpaLoanRepository repository;
    private final LoanEntityMapper mapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repository Repositorio JPA generado por Spring Data
     * @param mapper     Mapper para conversiones Entity ↔ Domain
     */
    public LoanRepositoryJpaImpl(JpaLoanRepository repository, LoanEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Loan save(Loan loan) {
        return mapper.toDomain(
                repository.save(mapper.toEntity(loan)));
    }

    @Override
    public List<Loan> saveAll(List<Loan> loans) {
        return repository.saveAll(
                loans.stream()
                        .map(mapper::toEntity)
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
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        // Por ahora, retorna lista vacía como placeholder
        return List.of();
    }

    @Override
    public List<Loan> findByUserIdAndStatus(String userId, String status) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return List.of();
    }

    @Override
    public List<Loan> findByBookId(String bookId) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return List.of();
    }

    @Override
    public List<Loan> findByBookIdAndStatus(String bookId, String status) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return List.of();
    }

    @Override
    public List<Loan> findByStatus(String status) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return List.of();
    }

    @Override
    public List<Loan> findOverdueLoans(LocalDateTime now) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return List.of();
    }

    @Override
    public List<Loan> findUpcomingDueLoans(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return List.of();
    }

    @Override
    public List<Loan> findLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return List.of();
    }

    @Override
    public Optional<Loan> findMostRecentLoanByUserAndBook(String userId, String bookId) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return Optional.empty();
    }

    @Override
    public long countByUserIdAndStatus(String userId, String status) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return 0;
    }

    @Override
    public List<Loan> findReturnedLoansByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementación: se requeriría una consulta personalizada en JpaLoanRepository
        return List.of();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(Loan loan) {
        repository.delete(mapper.toEntity(loan));
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
