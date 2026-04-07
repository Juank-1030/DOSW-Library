package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar el inventario de un libro existente.
 * 
 * <p><b>IMPORTANTE - Lógica de Biblioteca Real:</b></p>
 * <ul>
 * <li> NO se puede cambiar el título, autor o ISBN (datos inmutables)</li>
 * <li> SÍ se puede agregar o quitar copias del inventario</li>
 * <li> La disponibilidad se calcula automáticamente (copies > 0)</li>
 * </ul>
 * 
 * <p><b>Usado en:</b></p>
 * <ul>
 * <li>PATCH /api/books/{id}/inventory - Ajustar inventario</li>
 * <li>PUT /api/books/{id}/add-copies - Agregar copias</li>
 * <li>PUT /api/books/{id}/remove-copies - Quitar copias</li>
 * </ul>
 * 
 * <p><b>Tipos de operación:</b></p>
 * <ul>
 * <li><b>SET</b>: Establece cantidad absoluta (ej: "ahora hay 10 copias")</li>
 * <li><b>ADD</b>: Incrementa cantidad (ej: "agregar 5 copias más")</li>
 * <li><b>REMOVE</b>: Decrementa cantidad (ej: "quitar 2 copias dañadas")</li>
 * </ul>
 * 
 * <p><b>Ejemplos de uso:</b></p>
 * <pre>{@code
 * // Establecer cantidad absoluta
 * PATCH /api/books/BOOK-001/inventory
 * {
 * "operation": "SET",
 * "quantity": 10
 * }
 * 
 * // Agregar copias nuevas compradas
 * PATCH /api/books/BOOK-001/inventory
 * {
 * "operation": "ADD",
 * "quantity": 5
 * }
 * 
 * // Quitar copias dañadas/perdidas
 * PATCH /api/books/BOOK-001/inventory
 * {
 * "operation": "REMOVE",
 * "quantity": 2
 * }
 * }</pre>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar el inventario de copias de un libro")
public class UpdateBookInventoryDTO {

    @NotNull(message = "Operation cannot be null")
    @Schema(description = "Tipo de operación sobre el inventario", example = "ADD", allowableValues = { "SET", "ADD",
            "REMOVE" }, required = true)
    @JsonProperty("operation")
    private InventoryOperation operation;

    @NotNull(message = "Quantity cannot be null")
    @Schema(description = "Cantidad de copias (significado depende de la operación)", example = "5", required = true, minimum = "0")
    @JsonProperty("quantity")
    private Integer quantity;

    @Schema(description = "Razón del cambio de inventario (opcional, para auditoría)", example = "Compra de nuevos ejemplares")
    @JsonProperty("reason")
    private String reason;

    /**
     * Enum para tipos de operación de inventario.
     */
    public enum InventoryOperation {
        /**
         * Establece la cantidad absoluta de copias.
         * Ejemplo: quantity=10 → El libro tendrá exactamente 10 copias.
         */
        SET,

        /**
         * Agrega copias al inventario actual.
         * Ejemplo: Si hay 5 copias y quantity=3 → Total será 8 copias.
         */
        ADD,

        /**
         * Quita copias del inventario actual.
         * Ejemplo: Si hay 5 copias y quantity=2 → Total será 3 copias.
         */
        REMOVE
    }
}