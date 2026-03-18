package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
        userService = new UserService();
        loanService = new LoanService(bookService, userService);
        IdGeneratorUtil.reset();

        bookService.addBook(new Book("B001", "Clean Code", "Robert C. Martin"), 2);
        bookService.addBook(new Book("B002", "Design Patterns", "GoF"), 1);
        userService.registerUser(new User("U001", "John Doe"));
        userService.registerUser(new User("U002", "Jane Doe"));
    }

    // ========== ESCENARIOS EXITOSOS ==========

    @Test
    void shouldCreateLoanSuccessfully() throws BookNotAvailableException {
        Loan loan = loanService.createLoan("B001", "U001");
        assertNotNull(loan);
        assertEquals("B001", loan.getBook().getId());
        assertEquals("U001", loan.getUser().getId());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertNotNull(loan.getLoanDate());
        assertNull(loan.getReturnDate());
    }

    @Test
    void shouldGenerateLoanId() throws BookNotAvailableException {
        Loan loan = loanService.createLoan("B001", "U001");
        assertNotNull(loan.getId());
        assertTrue(loan.getId().matches("\\d+"));
    }

    @Test
    void shouldDecrementCopiesAfterLoan() throws BookNotAvailableException {
        assertEquals(2, bookService.getAvailableCopies("B001"));
        loanService.createLoan("B001", "U001");
        assertEquals(1, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldReturnBookSuccessfully() throws BookNotAvailableException {
        Loan loan = loanService.createLoan("B001", "U001");
        Loan returned = loanService.returnBook(loan.getId());
        assertEquals(LoanStatus.RETURNED, returned.getStatus());
        assertNotNull(returned.getReturnDate());
    }

    @Test
    void shouldIncrementCopiesAfterReturn() throws BookNotAvailableException {
        Loan loan = loanService.createLoan("B001", "U001");
        assertEquals(1, bookService.getAvailableCopies("B001"));
        loanService.returnBook(loan.getId());
        assertEquals(2, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldGetAllLoans() throws BookNotAvailableException {
        loanService.createLoan("B001", "U001");
        loanService.createLoan("B002", "U002");
        assertEquals(2, loanService.getAllLoans().size());
    }

    @Test
    void shouldReturnEmptyListWhenNoLoans() {
        assertTrue(loanService.getAllLoans().isEmpty());
    }

    @Test
    void shouldGetLoanById() throws BookNotAvailableException {
        Loan created = loanService.createLoan("B001", "U001");
        Loan found = loanService.getLoanById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void shouldGetActiveLoansByUser() throws BookNotAvailableException {
        loanService.createLoan("B001", "U001");
        Loan loan2 = loanService.createLoan("B002", "U001");
        loanService.returnBook(loan2.getId());
        List<Loan> active = loanService.getActiveLoansByUser("U001");
        assertEquals(1, active.size());
    }

    @Test
    void shouldAllowMultipleUsersToLoanSameBook() throws BookNotAvailableException {
        loanService.createLoan("B001", "U001");
        loanService.createLoan("B001", "U002");
        assertEquals(0, bookService.getAvailableCopies("B001"));
    }

    // ========== ESCENARIOS DE ERROR ==========

    @Test
    void shouldThrowWhenBookNotAvailable() throws BookNotAvailableException {
        loanService.createLoan("B002", "U001");
        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan("B002", "U002"));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("B001", "U999"));
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("B999", "U001"));
    }

    @Test
    void shouldThrowWhenReturningAlreadyReturnedLoan() throws BookNotAvailableException {
        Loan loan = loanService.createLoan("B001", "U001");
        loanService.returnBook(loan.getId());
        assertThrows(IllegalStateException.class,
                () -> loanService.returnBook(loan.getId()));
    }

    @Test
    void shouldThrowWhenLoanNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.returnBook("NONE"));
    }

    @Test
    void shouldThrowWhenCreatingLoanWithNullBookId() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan(null, "U001"));
    }

    @Test
    void shouldThrowWhenCreatingLoanWithNullUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("B001", null));
    }

    @Test
    void shouldThrowWhenCreatingLoanWithEmptyBookId() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("", "U001"));
    }

    @Test
    void shouldThrowWhenCreatingLoanWithEmptyUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.createLoan("B001", ""));
    }

    @Test
    void shouldThrowWhenBookMarkedUnavailable() {
        bookService.updateBookAvailability("B001", false);
        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan("B001", "U001"));
    }
}