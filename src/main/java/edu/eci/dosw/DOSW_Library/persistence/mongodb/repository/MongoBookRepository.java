package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz Spring Data MongoDB para la colección de libros.
 * Proporciona operaciones CRUD y consultas personalizadas para BookDocument.
 */
public interface MongoBookRepository extends MongoRepository<BookDocument, String> {

    @Query("{ 'title' : ?0 }")
    Optional<BookDocument> findByTitle(String title);

    @Query("{ 'isbn' : ?0 }")
    Optional<BookDocument> findByIsbn(String isbn);

    @Query("{ 'author' : ?0 }")
    List<BookDocument> findByAuthor(String author);

    @Query("{ 'category' : ?0 }")
    List<BookDocument> findByCategory(String category);

    @Query("{ 'inventory' : { $gt : ?0 } }")
    List<BookDocument> findByInventoryGreaterThan(Integer minInventory);

    @Query("{ 'price' : { $gte : ?0, $lte : ?1 } }")
    List<BookDocument> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("{ 'title' : { $regex : ?0, $options : 'i' } }")
    List<BookDocument> findByTitleContaining(String title);

    @Query("{ 'author' : { $regex : ?0, $options : 'i' } }")
    List<BookDocument> findByAuthorContaining(String author);

    @Query("{ 'inventory' : { $lte : ?0 } }")
    List<BookDocument> findLowInventoryBooks(Integer threshold);
}
