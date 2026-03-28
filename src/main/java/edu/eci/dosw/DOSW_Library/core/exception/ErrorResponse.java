package edu.eci.dosw.DOSW_Library.core.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Estructura estandar de error para respuestas de la API")
public class ErrorResponse {

    @Schema(description = "Marca de tiempo del error en formato ISO-8601", example = "2026-03-27T14:30:15")
    String timestamp;

    @Schema(description = "Codigo HTTP", example = "404")
    int status;

    @Schema(description = "Nombre del error HTTP", example = "Not Found")
    String error;

    @Schema(description = "Detalle del error", example = "Book not found with ID: B-100")
    String message;

    @Schema(description = "Ruta HTTP donde ocurrio el error", example = "/api/books/B-100")
    String path;
}
