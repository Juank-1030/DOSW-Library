package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import edu.eci.dosw.DOSW_Library.core.validator.LoanValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanDTO loanDTO) throws BookNotAvailableException {
        LoanValidator.validate(loanDTO);
        Loan loan = loanService.createLoan(loanDTO.getBookId(), loanDTO.getUserId());
        return new ResponseEntity<>(loan, HttpStatus.CREATED);
    }

    @PutMapping("/{loanId}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable String loanId) {
        Loan loan = loanService.returnBook(loanId);
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return new ResponseEntity<>(loanService.getAllLoans(), HttpStatus.OK);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<Loan> getLoanById(@PathVariable String loanId) {
        return new ResponseEntity<>(loanService.getLoanById(loanId), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Loan>> getActiveLoansByUser(@PathVariable String userId) {
        return new ResponseEntity<>(loanService.getActiveLoansByUser(userId), HttpStatus.OK);
    }
}
