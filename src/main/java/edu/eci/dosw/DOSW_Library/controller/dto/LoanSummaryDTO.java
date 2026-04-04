package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO simplificado de préstamo para listados.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen de un préstamo (versión ligera para listados)")
public class LoanSummaryDTO {

    @Schema(description = "ID del préstamo", example = "LOAN-001")
    @JsonProperty("id")
    private String id;

    @Schema(description = "ID del libro", example = "BOOK-001")
    @JsonProperty("bookId")
    private String bookId;

    @Schema(description = "Título del libro", example = "Clean Code")
    @JsonProperty("bookTitle")
    private String bookTitle;

    @Schema(description = "ID del usuario", example = "USR-001")
    @JsonProperty("userId")
    private String userId;

    @Schema(description = "Nombre del usuario", example = "John Doe")
    @JsonProperty("userName")
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha del préstamo", example = "2024-01-15")
    @JsonProperty("loanDate")
    private LocalDate loanDate;

    @Schema(description = "Estado del préstamo", example = "ACTIVE")
    @JsonProperty("status")
    private String status;
}