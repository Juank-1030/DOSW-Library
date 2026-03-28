package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.repository.LoanRepository;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    // Listado de préstamos
    private final List<Loan> loans = new ArrayList<>();
    private final BookService bookService;
    private final UserService userService;
    private int loanCounter = 0;

    public LoanService(BookService bookService, UserService userService) {
        this(bookService, userService, null);
    }

    @Autowired
    public LoanService(BookService bookService, UserService userService, LoanRepository loanRepository) {
        this.bookService = bookService;
        this.userService = userService;
        this.loanRepository = loanRepository;
    }

    public Loan createLoan(String bookId, String userId) throws BookNotAvailableException {
        ValidationUtil.validateNotEmpty(bookId, "Book ID");
        ValidationUtil.validateNotEmpty(userId, "User ID");

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        if (!bookService.isBookAvailable(bookId)) {
            throw new BookNotAvailableException("Book '" + book.getTitle() + "' is not available for loan");
        }

        Loan loan = new Loan(nextLoanId(), book, user, LocalDate.now());
        if (loanRepository != null) {
            loanRepository.save(loan);
        } else {
            loans.add(loan);
        }
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

        if (loanRepository != null) {
            loanRepository.save(loan);
        }

        return loan;
    }

    public List<Loan> getAllLoans() {
        if (loanRepository != null) {
            return loanRepository.findAll();
        }
        return new ArrayList<>(loans);
    }

    public Loan getLoanById(String loanId) {
        ValidationUtil.validateNotEmpty(loanId, "Loan ID");

        if (loanRepository != null) {
            return loanRepository.findById(loanId)
                    .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));
        }

        for (Loan loan : loans) {
            if (loan.getId().equals(loanId)) {
                return loan;
            }
        }
        throw new IllegalArgumentException("Loan not found with ID: " + loanId);
    }

    public List<Loan> getActiveLoansByUser(String userId) {
        ValidationUtil.validateNotEmpty(userId, "User ID");

        if (loanRepository != null) {
            return loanRepository.findByUser_IdAndStatus(userId, LoanStatus.ACTIVE);
        }

        return loans.stream()
                .filter(l -> l.getUser().getId().equals(userId) && l.getStatus() == LoanStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    private String nextLoanId() {
        if (loanRepository != null) {
            return String.valueOf(loanRepository.count() + 1);
        }
        loanCounter++;
        return String.valueOf(loanCounter);
    }
}