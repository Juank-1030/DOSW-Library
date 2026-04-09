package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad LoanEntity - Mapeo JPA de la tabla LOAN en base de datos.
 * 
 * Responsabilidades:
 * - Representar un prestamo de libro a usuario con ciclo de vida completo
 * - Mantener relaciones N:1 con User y Book (foreign keys)
 * - Rastrear fechas: prestamo, vencimiento, devolucion
 * - Persistir estado del prestamo (ACTIVE, RETURNED)
 * 
 * Cambios vs version anterior:
 * - NUEVO: dueDate (fecha de vencimiento: loan_date + 14 dias)
 * - MEJORADO: timestamps para auditoria (createdAt, updatedAt)
 * - MEJORADO: relacion bidireccional con cascades
 * 
 * Ciclo de vida:
 * 1. Se crea prestamo: status = ACTIVE, return_date = NULL
 * 2. Usuario devuelve: status = RETURNED, return_date = ahora
 * 
 * @author DOSW-Library Team
 * @version 2.0 (Con normalizacion a 3FN)
 */
@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "user", "book" })
public class LoanEntity {

    /**
     * Identificador unico del prestamo (LOAN-001, LOAN-002, etc.)
     * Generado por IdGeneratorUtil con prefijo "LOAN"
     */
    @Id
    @Column(name = "id", length = 20)
    private String id;

    /**
     * Referencia al usuario que solicita el prestamo
     * 
     * @ManyToOne: muchos prestamos pertenecen a un usuario
     * @JoinColumn: crea columna user_id que apunta a users.id
     *              nullable = false: todo prestamo DEBE tener usuario
     *              fetch = LAZY: carga el usuario bajo demanda (optimizacion)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_user"))
    private UserEntity user;

    /**
     * Referencia al libro siendo prestado
     * 
     * @ManyToOne: muchos prestamos pueden ser del mismo libro
     * @JoinColumn: crea columna book_id que apunta a books.id
     *              nullable = false: todo prestamo DEBE tener libro
     *              fetch = LAZY: carga el libro bajo demanda (optimizacion)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_book"))
    private BookEntity book;

    /**
     * Fecha en que se solicito el prestamo (cuando se creo este registro)
     * Tipo: TIMESTAMP en BD
     * Ejemplo: 2026-04-04 14:30:00
     */
    @Column(nullable = false)
    private LocalDateTime loanDate;

    /**
     * Fecha de vencimiento del prestamo (loan_date + 14 dias)
     * NUEVO: Permite generar reportes de prestamos atrasados
     * 
     * Regla de negocio: si hoy > dueDate y status = ACTIVE, el prestamo esta en
     * mora
     */
    @Column(nullable = false)
    private LocalDateTime dueDate;

    /**
     * Fecha en que el usuario devolvio el libro
     * 
     * NULL si prestamo aun no fue devuelto (status = ACTIVE)
     * Tiene valor si prestamo fue devuelto (status = RETURNED)
     * 
     * Invariante:
     * - Si status = ACTIVE → returnDate DEBE ser NULL
     * - Si status = RETURNED → returnDate DEBE tener fecha
     */
    @Column(nullable = true)
    private LocalDateTime returnDate;

    /**
     * Estado del prestamo
     * 
     * ACTIVE: libro en poder del usuario, aun no devuelto
     * RETURNED: libro fue devuelto, transaccion cerrada
     * 
     * Persitido en BD como STRING (vea @Enumerated(EnumType.STRING))
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    /**
     * Fecha de creacion del registro (cuando se creo el prestamo)
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de ultima actualizacion (puede cambiar si se corrige estado, etc.)
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Pre-persiste: inicializa timestamps y estado por defecto
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = LoanStatus.ACTIVE;
        }
        if (this.loanDate == null) {
            this.loanDate = LocalDateTime.now();
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
