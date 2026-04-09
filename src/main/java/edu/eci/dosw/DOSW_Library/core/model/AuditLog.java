package edu.eci.dosw.DOSW_Library.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Modelo de auditoría para registrar cambios en la BD.
 * 
 * <p>
 * <b>Propósito: INTEGRIDAD</b> - Garantizar que se puede rastrear:
 * </p>
 * <ul>
 * <li>Quién cambió un registro</li>
 * <li>Qué cambió exactamente</li>
 * <li>Cuándo se cambió</li>
 * <li>Por qué se cambió (razón)</li>
 * </ul>
 * 
 * <p>
 * <b>Ejemplo:</b> Si un usuario suspende a otro:
 * </p>
 * 
 * <pre>
 * entity="User"
 * entityId="USR-002"
 * action="UPDATE"
 * field="status"
 * oldValue="ACTIVE"
 * newValue="SUSPENDED"
 * changedBy="USR-001" (el bibliotecario que hizo el cambio)
 * reason="Límite de préstamos excedido"
 * </pre>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la entidad que cambió (ej: "User", "Book", "Loan")
     */
    @Column(nullable = false, length = 50)
    private String entity;

    /**
     * ID de la entidad que cambió (ej: "USR-001")
     */
    @Column(nullable = false, length = 100)
    private String entityId;

    /**
     * Tipo de acción (INSERT, UPDATE, DELETE)
     */
    @Column(nullable = false, length = 20)
    private String action;

    /**
     * Campo que cambió (para UPDATE)
     */
    @Column(length = 100)
    private String field;

    /**
     * Valor anterior
     */
    @Column(length = 500)
    private String oldValue;

    /**
     * Valor nuevo
     */
    @Column(length = 500)
    private String newValue;

    /**
     * Usuario que hizo el cambio
     */
    @Column(nullable = false, length = 100)
    private String changedBy;

    /**
     * Razón del cambio (ej: "Suspensión por límite excedido")
     */
    @Column(length = 500)
    private String reason;

    /**
     * IP del cliente que hizo el cambio
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * Timestamp cuando se hizo el cambio
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        if (this.changedAt == null) {
            this.changedAt = LocalDateTime.now();
        }
    }
}
