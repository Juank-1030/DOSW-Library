package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    // Mapa de Libros (Libro y la cantidad de ejemplares que tiene)
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Integer> bookCopies = new HashMap<>();

    public Book addBook(Book book, int copies) {
        ValidationUtil.validateNotNull(book, "Book");
        ValidationUtil.validateNotEmpty(book.getId(), "Book ID");
        ValidationUtil.validateNotEmpty(book.getTitle(), "Book Title");
        ValidationUtil.validateNotEmpty(book.getAuthor(), "Book Author");
        if (copies <= 0) {
            throw new IllegalArgumentException("Number of copies must be greater than 0");
        }
        books.put(book.getId(), book);
        bookCopies.put(book.getId(), bookCopies.getOrDefault(book.getId(), 0) + copies);
        return book;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public Book getBookById(String id) {
        ValidationUtil.validateNotEmpty(id, "Book ID");
        Book book = books.get(id);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ID: " + id);
        }
        return book;
    }

    public void updateBookAvailability(String bookId, boolean available) {
        Book book = getBookById(bookId);
        book.setAvailable(available);
    }

    public boolean isBookAvailable(String bookId) {
        Book book = books.get(bookId);
        return book != null && book.isAvailable() && bookCopies.getOrDefault(bookId, 0) > 0;
    }

    public int getAvailableCopies(String bookId) {
        return bookCopies.getOrDefault(bookId, 0);
    }

    public void decrementCopies(String bookId) {
        int current = bookCopies.getOrDefault(bookId, 0);
        if (current > 0) {
            bookCopies.put(bookId, current - 1);
        }
    }

    public void incrementCopies(String bookId) {
        bookCopies.put(bookId, bookCopies.getOrDefault(bookId, 0) + 1);
    }

    public boolean bookExists(String bookId) {
        return books.containsKey(bookId);
    }
}
