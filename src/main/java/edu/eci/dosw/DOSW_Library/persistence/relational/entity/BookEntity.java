package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad BookEntity - Mapeo JPA de la tabla BOOK en base de datos.
 * 
 * Responsabilidades:
 * - Representar un libro con su inventario (copies, available)
 * - Mantener relacion 1:N con Loan (un libro puede tener muchos prestamos)
 * - Persistir en tabla 'books' con restricciones de integridad
 * 
 * Cambio critico vs version anterior:
 * - available: boolean → Integer (cantidad exacta de copias disponibles, no
 * binario)
 * 
 * @author DOSW-Library Team
 * @version 2.0 (Con normalizacion a 3FN)
 */
@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "title", "author" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "loans")
public class BookEntity {

    /**
     * Identificador unico del libro (BK-001, BK-002, etc.)
     * Generado por IdGeneratorUtil con prefijo "BK"
     */
    @Id
    @Column(name = "id", length = 20)
    private String id;

    /**
     * Titulo del libro
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Autor del libro
     */
    @Column(nullable = false, length = 100)
    private String author;

    /**
     * Stock total de ejemplares del libro
     * 
     * Restriccion: copies > 0
     * Si copies es 0, el libro se considera descontinuado
     */
    @Column(nullable = false)
    private Integer copies;

    /**
     * Cantidad de ejemplares disponibles para prestamo
     * 
     * CAMBIO CRITICO: Ahora es Integer, no boolean
     * - Permite rastrear cantidad exacta de copias disponibles
     * - Facilita reportes de inventario
     * - Mejora manejo de multiples copias del mismo libro
     * 
     * Restriccion: available >= 0 AND available <= copies
     */
    @Column(nullable = false)
    private Integer available;

    /**
     * Fecha de creacion del registro
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de ultima actualizacion
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Relacion inversa 1:N: un libro puede tener muchos prestamos
     * mappedBy = "book" → se sincroniza bidireccional con LoanEntity.book
     * cascade = ALL → si se elimina el libro, se eliminan sus prestamos (CASCADE
     * DELETE)
     */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanEntity> loans;

    /**
     * Pre-persiste: inicializa timestamps
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Pre-update: actualiza timestamp de modificacion
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
