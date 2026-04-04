package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir información completa de un libro en respuestas.
 * 
 * <p>
 * <b>Usado en:</b>
 * </p>
 * <ul>
 * <li>GET /api/books/{id} - Obtener libro por ID</li>
 * <li>GET /api/books - Listar todos los libros</li>
 * <li>POST /api/books - Respuesta al crear libro</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de un libro")
public class BookDTO {

    @Schema(description = "Identificador único del libro", example = "BOOK-001", required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "Título del libro", example = "Clean Code: A Handbook of Agile Software Craftsmanship", required = true)
    @JsonProperty("title")
    private String title;

    @Schema(description = "Autor del libro", example = "Robert C. Martin", required = true)
    @JsonProperty("author")
    private String author;

    @Schema(description = "Cantidad de copias disponibles actualmente", example = "5", minimum = "0", required = true)
    @JsonProperty("copies")
    private int copies;

    @Schema(description = "Indica si el libro está disponible para préstamo (se calcula automáticamente: copies > 0)", example = "true", required = true)
    @JsonProperty("available")
    private boolean available;
}