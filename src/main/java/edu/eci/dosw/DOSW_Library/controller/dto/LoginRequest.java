package edu.eci.dosw.DOSW_Library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Credenciales para autenticarse y obtener un token JWT")
public class LoginRequest {

    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;

    @Schema(description = "Clave del usuario", example = "admin1234")
    private String password;
}
