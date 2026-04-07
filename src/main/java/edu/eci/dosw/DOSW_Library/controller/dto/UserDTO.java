package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir información completa de un usuario.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información completa de un usuario registrado")
public class UserDTO {

    @Schema(description = "Identificador único del usuario", example = "USR-001", required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "Nombre completo del usuario", example = "John Doe", required = true)
    @JsonProperty("name")
    private String name;

    @Schema(description = "Correo electrónico del usuario", example = "john.doe@example.com")
    @JsonProperty("email")
    private String email;
}