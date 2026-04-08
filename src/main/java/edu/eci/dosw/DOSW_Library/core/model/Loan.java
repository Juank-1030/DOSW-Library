package edu.eci.dosw.DOSW_Library.core.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "book", "user" })
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Fecha de solicitud del prestamo (cambio: LocalDate → LocalDateTime)
     */
    @Column(nullable = false)
    private LocalDateTime loanDate;

    /**
     * NUEVO: Fecha de vencimiento del prestamo (loan_date + 14 dias)
     */
    @Column(nullable = false)
    private LocalDateTime dueDate;

    /**
     * Fecha de devolucion (NULL si aun no devuelto, cambio: LocalDate →
     * LocalDateTime)
     */
    @Column(nullable = true)
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Loan(String id, Book book, User user, LocalDateTime loanDate) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(14);
        this.status = LoanStatus.ACTIVE;
        this.returnDate = null;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.loanDate == null) {
            this.loanDate = LocalDateTime.now();
        }
        if (this.dueDate == null) {
            this.dueDate = this.loanDate.plusDays(14);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}