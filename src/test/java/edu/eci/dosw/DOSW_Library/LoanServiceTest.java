package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.repository.LoanRepository;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanServiceTest {

    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanService = new LoanService(loanRepository, bookService, userService);
    }

    @Test
    void shouldCreateLoanSuccessfully()
            throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {
        Loan loan = loanService.createLoan("B001", "U001");

        assertNotNull(loan);
        assertEquals("B001", loan.getBook().getId());
        assertEquals("U001", loan.getUser().getId());
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertNotNull(loan.getLoanDate());
        assertNull(loan.getReturnDate());
    }

    @Test
    void shouldGenerateLoanIdWithExpectedFormat() throws BookNotAvailableException, UserNotFoundException,
            LoanLimitExceededException {
        Loan loan = loanService.createLoan("B001", "U001");

        assertNotNull(loan.getId());
        assertTrue(loan.getId().startsWith("LOAN-"));
    }

    @Test
    void shouldDecrementCopiesAfterLoan()
            throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {
        assertEquals(2, bookService.getAvailableCopies("B001"));
        loanService.createLoan("B001", "U001");
        assertEquals(1, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldReturnLoanSuccessfully()
            throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {
        Loan loan = loanService.createLoan("B001", "U001");
        Loan returned = loanService.returnLoan(loan.getId());

        assertEquals(LoanStatus.RETURNED, returned.getStatus());
        assertNotNull(returned.getReturnDate());
    }

    @Test
    void shouldIncrementCopiesAfterReturn() throws BookNotAvailableException, UserNotFoundException,
            LoanLimitExceededException {
        Loan loan = loanService.createLoan("B001", "U001");
        assertEquals(1, bookService.getAvailableCopies("B001"));
        loanService.returnLoan(loan.getId());
        assertEquals(2, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldGetAllLoans() throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {
        loanService.createLoan("B001", "U001");
        loanService.createLoan("B002", "U002");
        assertEquals(2, loanService.getAllLoans().size());
    }

    @Test
    void shouldReturnEmptyListWhenNoLoans() {
        assertTrue(loanService.getAllLoans().isEmpty());
    }

    @Test
    void shouldGetLoanById() throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {
        Loan created = loanService.createLoan("B001", "U001");
        Loan found = loanService.getLoanById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void shouldGetActiveLoans() throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {
        loanService.createLoan("B001", "U001");
        Loan loan2 = loanService.createLoan("B002", "U001");

        loanService.returnLoan(loan2.getId());

        List<Loan> active = loanService.getActiveLoans("U001");
        assertEquals(1, active.size());
    }

    @Test
    void shouldGetLoansByUserAndBook()
            throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {
        loanService.createLoan("B001", "U001");
        loanService.createLoan("B001", "U002");

        assertEquals(1, loanService.getLoansByUser("U001").size());
        assertEquals(2, loanService.getLoansByBook("B001").size());
    }

    @Test
    void shouldAllowMultipleUsersToLoanSameBook() throws BookNotAvailableException, UserNotFoundException,
            LoanLimitExceededException {
        loanService.createLoan("B001", "U001");
        loanService.createLoan("B001", "U002");

        assertEquals(0, bookService.getAvailableCopies("B001"));
    }

    @Test
    void shouldThrowWhenBookNotAvailable() throws BookNotAvailableException, UserNotFoundException,
            LoanLimitExceededException {
        loanService.createLoan("B002", "U001");
        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan("B002", "U002"));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        assertThrows(UserNotFoundException.class,
                () -> loanService.createLoan("B001", "U999"));
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> loanService.createLoan("B999", "U001"));
    }

    @Test
    void shouldThrowWhenReturningAlreadyReturnedLoan() throws BookNotAvailableException, UserNotFoundException,
            LoanLimitExceededException {
        Loan loan = loanService.createLoan("B001", "U001");
        loanService.returnLoan(loan.getId());

        assertThrows(IllegalStateException.class,
                () -> loanService.returnLoan(loan.getId()));
    }

    @Test
    void shouldThrowWhenLoanNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> loanService.returnLoan("NONE"));
    }

    @Test
    void shouldThrowWhenUserExceedsLoanLimit() throws BookNotAvailableException, UserNotFoundException,
            LoanLimitExceededException {
        loanService.createLoan("B001", "U001");
        loanService.createLoan("B002", "U001");
        loanService.createLoan("B003", "U001");

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan("B004", "U001"));
    }

    @Test
    void shouldThrowWhenCreatingDuplicateActiveLoanForSameUserAndBook() throws BookNotAvailableException,
            UserNotFoundException, LoanLimitExceededException {
        loanService.createLoan("B001", "U001");

        assertThrows(IllegalStateException.class,
                () -> loanService.createLoan("B001", "U001"));
    }
}