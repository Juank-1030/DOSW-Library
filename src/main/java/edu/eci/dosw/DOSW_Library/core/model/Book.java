package edu.eci.dosw.DOSW_Library.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "books")
public class Book {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int copies;

    @Column(nullable = false)
    private boolean available = true;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = 0;
        this.available = true;
    }

    public Book(String id, String title, String author, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.available = copies > 0;
    }
}