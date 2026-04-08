package edu.eci.dosw.DOSW_Library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de autenticación exitosa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de autenticación con token JWT y datos del usuario")
public class AuthResponse {

    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType;

    @Schema(description = "ID del usuario autenticado", example = "USR-001")
    private String userId;

    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;

    @Schema(description = "Rol del usuario", example = "LIBRARIAN")
    private String role;

    @Schema(description = "Estado del usuario", example = "ACTIVE")
    private String status;

    @Schema(description = "Tiempo de expiración del token en milisegundos", example = "3600000")
    private Long expiresIn;
}
