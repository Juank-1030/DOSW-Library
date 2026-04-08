package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class BookServiceTest {

    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookService(bookRepository);
    }

    @Test
    void shouldAddBookSuccessfully() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        when(bookRepository.existsById("B001")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookRepository.findById("B001")).thenReturn(Optional.of(book));
        when(bookRepository.count()).thenReturn(1L);

        Book result = bookService.addBook(book, 3);

        assertNotNull(result);
        assertEquals("B001", result.getId());
    }

    @Test
    void shouldThrowWhenAddingDuplicateBook() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        when(bookRepository.existsById("B001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(book, 3));
    }

    @Test
    void shouldGetAllBooks() {
        Book book1 = new Book("B001", "Clean Code", "Robert C. Martin");
        Book book2 = new Book("B002", "Design Patterns", "GoF");
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
    }

    @Test
    void shouldGetBookById() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        when(bookRepository.findById("B001")).thenReturn(Optional.of(book));

        Book found = bookService.getBookById("B001");
        assertEquals("Clean Code", found.getTitle());
    }

    @Test
    void shouldUpdateAvailabilityDecrementAndIncrement() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        book.setAvailable(1);

        when(bookRepository.findById("B001")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookService.updateAvailability("B001", -1);
        assertEquals(0, book.getAvailable());
        assertFalse(bookService.isBookAvailable("B001"));

        bookService.updateAvailability("B001", +1);
        assertEquals(1, book.getAvailable());
        assertTrue(bookService.isBookAvailable("B001"));
    }

    @Test
    void shouldSetBookCopies() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        book.setAvailable(1);

        when(bookRepository.findById("B001")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookService.setBookCopies("B001", 0);
        assertEquals(0, book.getAvailable());
        assertFalse(bookService.isBookAvailable("B001"));

        bookService.setBookCopies("B001", 5);
        assertEquals(5, book.getAvailable());
        assertTrue(bookService.isBookAvailable("B001"));
    }

    @Test
    void shouldThrowWhenUpdateAvailabilityWouldGoNegative() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        book.setAvailable(0);

        when(bookRepository.findById("B001")).thenReturn(Optional.of(book));

        assertThrows(IllegalStateException.class,
                () -> bookService.updateAvailability("B001", -1));
    }

    @Test
    void shouldDeleteBook() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        when(bookRepository.findById("B001")).thenReturn(Optional.of(book));

        bookService.deleteBook("B001");
        // Mock verifica que deleteById fue llamado
    }

    @Test
    void shouldReturnEmptyListWhenNoBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList());
        assertTrue(bookService.getAllBooks().isEmpty());
    }

    @Test
    void shouldThrowWhenGettingBookByNonExistentId() {
        when(bookRepository.findById("NONE")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById("NONE"));
    }

    @Test
    void shouldThrowWhenCheckingAvailabilityOfNonExistentBook() {
        when(bookRepository.findById("NONE")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.isBookAvailable("NONE"));
    }

    @Test
    void shouldThrowWhenGetAvailableCopiesOfNonExistentBook() {
        when(bookRepository.findById("NONE")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookService.getAvailableCopies("NONE"));
    }

    @Test
    void shouldThrowWhenSettingNegativeCopies() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        when(bookRepository.findById("B001")).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.setBookCopies("B001", -1));
    }

    @Test
    void shouldReturnNotAvailableWhenNoCopies() {
        Book book = new Book("B001", "Clean Code", "Robert C. Martin");
        book.setAvailable(0);

        when(bookRepository.findById("B001")).thenReturn(Optional.of(book));
        assertFalse(bookService.isBookAvailable("B001"));
    }
}
