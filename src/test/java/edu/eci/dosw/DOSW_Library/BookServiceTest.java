package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
    }

    @Test
    void shouldAddBookSuccessfully() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        Book result = bookService.addBook(book, 3);

        assertNotNull(result);
        assertEquals("B001", result.getId());
        assertEquals(3, bookService.getAvailableCopies("B001"));
        assertTrue(bookService.isBookAvailable("B001"));
    }

    @Test
    void shouldThrowWhenAddingDuplicateBook() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 2);
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 3));
    }

    @Test
    void shouldGetAllBooks() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 2);
        bookService.addBook(new Book("B002", "Design Patterns", "GoF"), 1);
        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
    }

    @Test
    void shouldGetBookById() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);
        Book book = bookService.getBookById("B001");
        assertEquals("Clean Code", book.getTitle());
    }

    @Test
    void shouldUpdateAvailabilityDecrementAndIncrement() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);

        bookService.updateAvailability("B001", -1);
        assertEquals(0, bookService.getAvailableCopies("B001"));
        assertFalse(bookService.getBookById("B001").isAvailable());

        bookService.updateAvailability("B001", +1);
        assertEquals(1, bookService.getAvailableCopies("B001"));
        assertTrue(bookService.getBookById("B001").isAvailable());
    }

    @Test
    void shouldSetBookCopies() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);

        bookService.setBookCopies("B001", 0);
        assertEquals(0, bookService.getAvailableCopies("B001"));
        assertFalse(bookService.isBookAvailable("B001"));

        bookService.setBookCopies("B001", 5);
        assertEquals(5, bookService.getAvailableCopies("B001"));
        assertTrue(bookService.isBookAvailable("B001"));
    }

    @Test
    void shouldThrowWhenUpdateAvailabilityWouldGoNegative() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 0);

        assertThrows(IllegalStateException.class,
                () -> bookService.updateAvailability("B001", -1));
    }

    @Test
    void shouldDeleteBook() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 3);

        bookService.deleteBook("B001");

        assertFalse(bookService.existsById("B001"));
        assertEquals(0, bookService.getTotalBooks());
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById("B001"));
    }

    @Test
    void shouldReturnEmptyListWhenNoBooks() {
        assertTrue(bookService.getAllBooks().isEmpty());
    }

    @Test
    void shouldThrowWhenGettingBookByNonExistentId() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById("NONE"));
    }

    @Test
    void shouldThrowWhenCheckingAvailabilityOfNonExistentBook() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.isBookAvailable("NONE"));
    }

    @Test
    void shouldThrowWhenGetAvailableCopiesOfNonExistentBook() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.getAvailableCopies("NONE"));
    }

    @Test
    void shouldThrowWhenSettingNegativeCopies() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);
        assertThrows(IllegalArgumentException.class,
                () -> bookService.setBookCopies("B001", -1));
    }

    @Test
    void shouldReturnNotAvailableWhenNoCopies() {
        bookService.addBook(new Book("B001", "Clean Code", "Martin"), 1);
        bookService.updateAvailability("B001", -1);
        assertFalse(bookService.isBookAvailable("B001"));
    }
}
