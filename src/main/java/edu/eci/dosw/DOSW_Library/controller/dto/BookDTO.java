package edu.eci.dosw.DOSW_Library.controller.dto;

public class BookDTO {
    private String id;
    private String title;
    private String author;
    private int copies;

    public BookDTO() {}

    public BookDTO(String id, String title, String author, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = copies;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
}
