package edu.eci.dosw.DOSW_Library.persistence.mongodb.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.LoanDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte entre documento MongoDB LoanDocument y modelo de dominio
 * Loan.
 * 
 * Realiza conversión entre la representación NoSQL (con estrategia HYBRID:
 * referencias + embedded documents) y el modelo de dominio agnóstico de
 * la capa de persistencia.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Component
public class LoanDocumentMapper {

    /**
     * Convierte modelo de dominio Loan a documento MongoDB.
     * 
     * Mapeo:
     * - Loan.id → LoanDocument.id
     * - Loan.status → LoanDocument.status
     * - Loan.loanDate → LoanDocument.loanDate
     * - Loan.dueDate → LoanDocument.dueDate
     * - Loan.returnDate → LoanDocument.returnDate
     * - Referencias y historial se mapean según necesidad
     *
     * @param loan modelo de dominio
     * @return documento MongoDB
     */
    public LoanDocument toDocument(Loan loan) {
        if (loan == null) {
            return null;
        }

        return LoanDocument.builder()
                .id(loan.getId())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus() != null ? loan.getStatus().toString() : null)
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .build();
    }

    /**
     * Convierte documento MongoDB a modelo de dominio Loan.
     * 
     * Extrae la información del documento HYBRID (referencias + embedded)
     * y la transforma en modelo de dominio.
     *
     * @param document documento MongoDB
     * @return modelo de dominio
     */
    public Loan toDomain(LoanDocument document) {
        if (document == null) {
            return null;
        }

        return Loan.builder()
                .id(document.getId())
                .loanDate(document.getLoanDate())
                .dueDate(document.getDueDate())
                .returnDate(document.getReturnDate())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
