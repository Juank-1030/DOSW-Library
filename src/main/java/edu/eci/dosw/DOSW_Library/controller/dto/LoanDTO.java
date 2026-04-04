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
 * DTO para transferir información completa de un préstamo.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de un préstamo de libro")
public class LoanDTO {

    @Schema(description = "Identificador único del préstamo", example = "LOAN-001", required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "Información completa del libro prestado", required = true)
    @JsonProperty("book")
    private BookDTO book;

    @Schema(description = "Información completa del usuario que solicitó el préstamo", required = true)
    @JsonProperty("user")
    private UserDTO user;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha en que se realizó el préstamo", example = "2024-01-15", type = "string", format = "date", required = true)
    @JsonProperty("loanDate")
    private LocalDate loanDate;

    @Schema(description = "Estado actual del préstamo", example = "ACTIVE", allowableValues = { "ACTIVE",
            "RETURNED" }, required = true)
    @JsonProperty("status")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha en que se devolvió el libro (solo si status es RETURNED)", example = "2024-01-30", type = "string", format = "date")
    @JsonProperty("returnDate")
    private LocalDate returnDate;
}