package edu.eci.dosw.DOSW_Library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar un libro y su cantidad de ejemplares")
public class BookDTO {
    @Schema(description = "Identificador unico del libro", example = "B-101")
    private String id;

    @Schema(description = "Titulo del libro", example = "Clean Code")
    private String title;

    @Schema(description = "Autor del libro", example = "Robert C. Martin")
    private String author;

    @Schema(description = "Cantidad de ejemplares a registrar", example = "3")
    private int copies;
}
