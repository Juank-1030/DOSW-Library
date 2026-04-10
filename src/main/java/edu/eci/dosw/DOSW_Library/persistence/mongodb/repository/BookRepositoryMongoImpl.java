package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.mongodb.mapper.BookDocumentMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.BookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación MongoDB de BookRepository.
 * Esta implementación está activa cuando el perfil "mongo" está habilitado.
 *
 * Proporciona todas las operaciones CRUD y consultas especializadas usando
 * MongoDB.
 * La conversión entre documentos y dominios se realiza mediante
 * BookDocumentMapper.
 *
 * @see BookRepository - Interfaz genérica
 * @see Profile - Esta implementación solo se activa con @Profile("mongo")
 */
@Repository
@Profile("mongo")
public class BookRepositoryMongoImpl implements BookRepository {

    private final MongoBookRepository repository;
    private final BookDocumentMapper mapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repository Repositorio MongoDB generado por Spring Data
     * @param mapper     Mapper para conversiones Document ↔ Domain
     */
    public BookRepositoryMongoImpl(MongoBookRepository repository, BookDocumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Book save(Book book) {
        return mapper.toDomain(
                repository.save(mapper.toDocument(book)));
    }

    @Override
    public List<Book> saveAll(List<Book> books) {
        return repository.saveAll(
                books.stream()
                        .map(mapper::toDocument)
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
        return repository.findByTitle(title)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return repository.findByIsbn(isbn)
                .map(mapper::toDomain);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return repository.findByAuthor(author).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findByCategory(String category) {
        return repository.findByCategory(category).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        return repository.findByTitleContaining(title).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findByAuthorContaining(String author) {
        return repository.findByAuthorContaining(author).stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Book> findByInventoryGreaterThan(Integer minInventory) {
        return repository.findByInventoryGreaterThan(minInventory).stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        // Placeholder - MongoDB no tiene este método específico
        return List.of();
    }

    @Override
    public List<Book> findByPriceRange(double minPrice, double maxPrice) {
        return findByPriceBetween(
                BigDecimal.valueOf(minPrice),
                BigDecimal.valueOf(maxPrice));
    }

    @Override
    public List<Book> findAvailableBooks() {
        // Libros con disponibilidad > 0
        return repository.findByInventoryGreaterThan(0).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findOutOfStockBooks() {
        // Placeholder para libros sin inventario
        return List.of();
    }

    @Override
    public List<Book> findLowInventoryBooks(int threshold) {
        return repository.findLowInventoryBooks(threshold).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findAvailableBooksByCategory(String category) {
        return repository.findByCategory(category).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findAvailableBooksByAuthor(String author) {
        return repository.findByAuthor(author).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return repository.findByIsbn(isbn).isPresent();
    }

    @Override
    public List<Book> findMostRequestedBooks() {
        // Placeholder para libros más solicitados
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Book book) {
        if (book != null && book.getId() != null) {
            repository.deleteById(book.getId());
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
