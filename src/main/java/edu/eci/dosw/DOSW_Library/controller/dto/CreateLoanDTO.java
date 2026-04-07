package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo préstamo de libro.
 * 
 * <p>
 * <b>Validaciones de negocio (en Service):</b>
 * </p>
 * <ul>
 * <li>El libro debe existir en el sistema</li>
 * <li>El libro debe tener copias disponibles (copies > 0)</li>
 * <li>El usuario debe existir en el sistema</li>
 * <li>El usuario no debe tener más de 3 préstamos activos</li>
 * <li>El usuario no puede pedir prestado el mismo libro dos veces
 * simultáneamente</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para crear un nuevo préstamo")
public class CreateLoanDTO {

    @NotBlank(message = "Book ID cannot be empty")
    @Schema(description = "ID del libro que se desea prestar", example = "BOOK-001", required = true)
    @JsonProperty("bookId")
    private String bookId;

    @NotBlank(message = "User ID cannot be empty")
    @Schema(description = "ID del usuario que solicita el préstamo", example = "USR-001", required = true)
    @JsonProperty("userId")
    private String userId;
}