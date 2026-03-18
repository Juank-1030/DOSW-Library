package edu.eci.dosw.DOSW_Library;

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

    // ========== ESCENARIOS EXITOSOS ==========

    @Test
    void shouldAddBookSuccessfully() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        Book result = bookService.addBook(book, 3);
        assertNotNull(result);
        assertEquals("B001", result.getId());
        assertEquals(3, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldAddCopiesToExistingBook() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 2);
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 3);
        assertEquals(5, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldGetAllBooks() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 2);
        bookService.addBook(new Book("B002", "Design Patterns", "GoF"), 1);
        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoBooks() {
        assertTrue(bookService.getAllBooks().isEmpty());
    }

    @Test
    void shouldGetBookById() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);
        Book book = bookService.getBookById("B001");
        assertEquals("Clean Code", book.getTitle());
    }

    @Test
    void shouldUpdateBookAvailability() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);
        bookService.updateBookAvailability("B001", false);
        assertFalse(bookService.getBookById("B001").isAvailable());
    }

    @Test
    void shouldCheckBookIsAvailable() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);
        assertTrue(bookService.isBookAvailable("B001"));
    }

    @Test
    void shouldDecrementCopies() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 3);
        bookService.decrementCopies("B001");
        assertEquals(2, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldIncrementCopies() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);
        bookService.incrementCopies("B001");
        assertEquals(2, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldNotDecrementBelowZero() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);
        bookService.decrementCopies("B001");
        bookService.decrementCopies("B001");
        assertEquals(0, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldCheckBookExists() {
        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 1);
        assertTrue(bookService.bookExists("B001"));
        assertFalse(bookService.bookExists("B999"));
    }

    // ========== ESCENARIOS DE ERROR ==========

    @Test
    void shouldThrowWhenAddingNullBook() {
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(null, 1));
    }

    @Test
    void shouldThrowWhenAddingBookWithNullId() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(new Book(null, "Title", "Author"), 1));
    }

    @Test
    void shouldThrowWhenAddingBookWithEmptyTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(new Book("B001", "", "Author"), 1));
    }

    @Test
    void shouldThrowWhenAddingBookWithEmptyAuthor() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(new Book("B001", "Title", ""), 1));
    }

    @Test
    void shouldThrowWhenAddingBookWithZeroCopies() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(new Book("B001", "Clean Code", "Martin"), 0));
    }

    @Test
    void shouldThrowWhenAddingBookWithNegativeCopies() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(new Book("B001", "Clean Code", "Martin"), -1));
    }

    @Test
    void shouldThrowWhenGettingBookByNonExistentId() {
        assertThrows(IllegalArgumentException.class, () -> bookService.getBookById("NONE"));
    }

    @Test
    void shouldThrowWhenGettingBookByNullId() {
        assertThrows(IllegalArgumentException.class, () -> bookService.getBookById(null));
    }

    @Test
    void shouldThrowWhenGettingBookByEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> bookService.getBookById(""));
    }

    @Test
    void shouldReturnNotAvailableWhenBookDoesNotExist() {
        assertFalse(bookService.isBookAvailable("NONE"));
    }

    @Test
    void shouldReturnNotAvailableWhenSetUnavailable() {
        bookService.addBook(new Book("B001", "Clean Code", "Martin"), 1);
        bookService.updateBookAvailability("B001", false);
        assertFalse(bookService.isBookAvailable("B001"));
    }

    @Test
    void shouldReturnNotAvailableWhenNoCopies() {
        bookService.addBook(new Book("B001", "Clean Code", "Martin"), 1);
        bookService.decrementCopies("B001");
        assertFalse(bookService.isBookAvailable("B001"));
    }
}
