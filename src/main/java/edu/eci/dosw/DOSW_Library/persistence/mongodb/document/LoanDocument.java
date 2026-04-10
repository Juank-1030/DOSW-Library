package edu.eci.dosw.DOSW_Library.persistence.mongodb.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Documento MongoDB para la colección de préstamos.
 * 
 * Implementa estrategia HÍBRIDA de persistencia NoSQL:
 * - REFERENCIADO: userRef y bookRef (datos denormalizados de Usuario y Libro)
 * - EMBEBIDO: history[] (historial de cambios sin documento separado)
 * 
 * Ventajas:
 * 1. Una lectura obtiene TODO el préstamo (usuario, libro, historial)
 * 2. Historial auditable sin joins
 * 3. Consultas rápidas por userId, bookId, estado, fecha
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Document(collection = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@CompoundIndexes({
        @CompoundIndex(name = "user_status_idx", def = "{'userRef.userId': 1, 'status': 1}"),
        @CompoundIndex(name = "book_status_idx", def = "{'bookRef.bookId': 1, 'status': 1}"),
        @CompoundIndex(name = "dueDate_idx", def = "{'dueDate': 1, 'status': 1}")
})
public class LoanDocument {

    /**
     * ID único del préstamo, sincronizado con SQL
     */
    @Id
    private String id;

    /**
     * REFERENCIADO: Información desnormalizada del usuario que hace el préstamo
     * Contiene copia de datos para evitar lookup de usuario en cada consulta
     */
    private UserReference userRef;

    /**
     * REFERENCIADO: Información desnormalizada del libro prestado
     * Contiene copia de datos para evitar lookup de libro en cada consulta
     */
    private BookReference bookRef;

    /**
     * Fecha en que se solicito el préstamo
     */
    @Indexed
    private LocalDateTime loanDate;

    /**
     * Fecha de vencimiento del préstamo (loan_date + 14 días)
     */
    @Indexed
    private LocalDateTime dueDate;

    /**
     * Fecha en que se devolvió el libro (null si está vigente)
     */
    private LocalDateTime returnDate;

    /**
     * Estado actual del préstamo: ACTIVE, RETURNED, OVERDUE, CANCELLED
     */
    @Indexed
    private String status;

    /**
     * EMBEBIDO: Historial de cambios de estado del préstamo
     * Cada cambio se registra con fecha y razón
     * Alternativa: antes habría tabla separada LoanStatus
     * Ahora: lista en el mismo documento
     */
    @Builder.Default
    private List<LoanHistoryDocument> history = new ArrayList<>();

    /**
     * Fecha de creación del registro
     */
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización
     */
    private LocalDateTime updatedAt;

    /**
     * Clase anidada para referencia de usuario
     * Desnormaliza datos frecuentemente accedidos
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserReference {
        private String userId;
        private String userName;
        private String userRole;
    }

    /**
     * Clase anidada para referencia de libro
     * Desnormaliza datos frecuentemente accedidos
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookReference {
        private String bookId;
        private String bookTitle;
        private String bookAuthor;
    }
}
