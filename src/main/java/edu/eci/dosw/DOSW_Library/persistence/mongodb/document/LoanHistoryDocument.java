package edu.eci.dosw.DOSW_Library.persistence.mongodb.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Documento anidado (Embebido) en LoanDocument para auditoría.
 * 
 * Representa cada cambio de estado en un préstamo, almacenando:
 * - Estado anterior y posterior
 * - Fecha del cambio
 * - Razón del cambio
 * 
 * Embebido en: LoanDocument.history[]
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanHistoryDocument {

    /**
     * Estado del préstamo en este historial
     * ACTIVE, RETURNED, OVERDUE, CANCELLED
     */
    private String status;

    /**
     * Fecha y hora del cambio de estado
     */
    private LocalDateTime changedAt;

    /**
     * Razón del cambio
     * Ejemplo: "Préstamo creado", "Devuelto exitosamente", "Vencimiento"
     */
    private String reason;
}
