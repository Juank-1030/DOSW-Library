package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    // Listado de préstamos
    private final List<Loan> loans = new ArrayList<>();
    private final BookService bookService;
    private final UserService userService;
    private int loanCounter = 0;

    public LoanService(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    public Loan createLoan(String bookId, String userId) throws BookNotAvailableException {
        ValidationUtil.validateNotEmpty(bookId, "Book ID");
        ValidationUtil.validateNotEmpty(userId, "User ID");

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        if (!bookService.isBookAvailable(bookId)) {
            throw new BookNotAvailableException("Book '" + book.getTitle() + "' is not available for loan");
        }

        loanCounter++;
        Loan loan = new Loan(String.valueOf(loanCounter), book, user, LocalDate.now());
        loans.add(loan);
        bookService.decrementCopies(bookId);

        return loan;
    }

    public Loan returnBook(String loanId) {
        ValidationUtil.validateNotEmpty(loanId, "Loan ID");

        Loan loan = getLoanById(loanId);

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException("Loan has already been returned");
        }

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());
        bookService.incrementCopies(loan.getBook().getId());

        return loan;
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    public Loan getLoanById(String loanId) {
        ValidationUtil.validateNotEmpty(loanId, "Loan ID");
        for (Loan loan : loans) {
            if (loan.getId().equals(loanId)) {
                return loan;
            }
        }
        throw new IllegalArgumentException("Loan not found with ID: " + loanId);
    }

    public List<Loan> getActiveLoansByUser(String userId) {
        ValidationUtil.validateNotEmpty(userId, "User ID");
        return loans.stream()
                .filter(l -> l.getUser().getId().equals(userId) && l.getStatus() == LoanStatus.ACTIVE)
                .collect(Collectors.toList());
    }
}