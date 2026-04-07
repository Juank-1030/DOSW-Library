package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo libro en el sistema.
 * 
 * <p>
 * <b>Usado en:</b>
 * </p>
 * <ul>
 * <li>POST /api/books - Crear nuevo libro</li>
 * </ul>
 * 
 * <p>
 * <b>Validaciones aplicadas:</b>
 * </p>
 * <ul>
 * <li>id: No puede estar vacío, longitud 3-50 caracteres</li>
 * <li>title: No puede estar vacío, longitud 1-200 caracteres</li>
 * <li>author: No puede estar vacío, longitud 1-100 caracteres</li>
 * <li>copies: No puede ser nulo, mínimo 0</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para registrar un nuevo libro en la biblioteca")
public class CreateBookDTO {

    @NotBlank(message = "Book ID cannot be empty")
    @Size(min = 3, max = 50, message = "Book ID must be between 3 and 50 characters")
    @Schema(description = "Identificador único del libro (ISBN, código interno, etc.)", example = "ISBN-978-0132350884", required = true, minLength = 3, maxLength = 50)
    @JsonProperty("id")
    private String id;

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Schema(description = "Título del libro", example = "Clean Code: A Handbook of Agile Software Craftsmanship", required = true, maxLength = 200)
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "Author cannot be empty")
    @Size(min = 1, max = 100, message = "Author must be between 1 and 100 characters")
    @Schema(description = "Autor del libro", example = "Robert C. Martin", required = true, maxLength = 100)
    @JsonProperty("author")
    private String author;

    @NotNull(message = "Copies cannot be null")
    @Min(value = 0, message = "Copies must be at least 0")
    @Schema(description = "Cantidad inicial de copias/ejemplares del libro", example = "5", required = true, minimum = "0")
    @JsonProperty("copies")
    private Integer copies;
}