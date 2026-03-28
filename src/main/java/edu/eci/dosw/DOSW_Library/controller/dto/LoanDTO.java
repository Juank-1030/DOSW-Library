package edu.eci.dosw.DOSW_Library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un prestamo de libro")
public class LoanDTO {
    @Schema(description = "ID del libro a prestar", example = "B-101")
    private String bookId;

    @Schema(description = "ID del usuario solicitante", example = "U-200")
    private String userId;
}