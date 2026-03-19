package edu.eci.dosw.DOSW_Library.core.model;

import java.time.LocalDate;

public class Loan {
    private String id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LoanStatus status;
    private LocalDate returnDate;

    public Loan() {
    }

    public Loan(String id, Book book, User user, LocalDate loanDate) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.status = LoanStatus.ACTIVE;
        this.returnDate = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Loan{id='" + id + "', book=" + book + ", user=" + user +
                ", loanDate=" + loanDate + ", status=" + status + ", returnDate=" + returnDate + "}";
    }
}