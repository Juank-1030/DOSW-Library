package edu.eci.dosw.DOSW_Library.controller.dto;

public class LoanDTO {
    private String bookId;
    private String userId;

    public LoanDTO() {
    }

    public LoanDTO(String bookId, String userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}