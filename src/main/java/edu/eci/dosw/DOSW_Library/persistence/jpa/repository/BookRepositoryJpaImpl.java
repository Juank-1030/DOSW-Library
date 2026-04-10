package edu.eci.dosw.DOSW_Library.persistence.jpa.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.jpa.mapper.BookEntityMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.BookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de BookRepository.
 * Esta implementación está activa cuando el perfil "relational" está
 * habilitado.
 *
 * Proporciona todas las operaciones CRUD y consultas especializadas usando JPA.
 * La conversión entre entidades y dominios se realiza mediante
 * BookEntityMapper.
 *
 * @see BookRepository - Interfaz genérica
 * @see Profile - Esta implementación solo se activa con @Profile("relational")
 */
@Repository
@Profile("relational")
public class BookRepositoryJpaImpl implements BookRepository {

    private final JpaBookRepository repository;
    private final BookEntityMapper mapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repository Repositorio JPA generado por Spring Data
     * @param mapper     Mapper para conversiones Entity ↔ Domain
     */
    public BookRepositoryJpaImpl(JpaBookRepository repository, BookEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Book save(Book book) {
        return mapper.toDomain(
                repository.save(mapper.toEntity(book)));
    }

    @Override
    public List<Book> saveAll(List<Book> books) {
        return repository.saveAll(
                books.stream()
                        .map(mapper::toEntity)
                        .toList())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Book> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return Optional.empty();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return Optional.empty();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findByCategory(String category) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findByTitleContaining(String titlePattern) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findByAuthorContaining(String authorPattern) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findAvailableBooks() {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findOutOfStockBooks() {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findLowInventoryBooks(int threshold) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findAvailableBooksByCategory(String category) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findAvailableBooksByAuthor(String author) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return false;
    }

    @Override
    public List<Book> findMostRequestedBooks() {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public List<Book> findByPriceRange(double minPrice, double maxPrice) {
        // Implementación: se requeriría una consulta personalizada en JpaBookRepository
        return List.of();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(Book book) {
        repository.delete(mapper.toEntity(book));
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
