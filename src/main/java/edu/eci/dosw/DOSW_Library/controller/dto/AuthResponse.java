package edu.eci.dosw.DOSW_Library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta de autenticacion con el token JWT")
public class AuthResponse {

    @Schema(description = "Token JWT para autorizar peticiones", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType;
}
