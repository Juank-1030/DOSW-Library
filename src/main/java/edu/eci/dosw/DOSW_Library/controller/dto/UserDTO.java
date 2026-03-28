package edu.eci.dosw.DOSW_Library.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar un usuario")
public class UserDTO {
    @Schema(description = "Identificador unico del usuario", example = "U-200")
    private String id;

    @Schema(description = "Nombre del usuario", example = "Juan Perez")
    private String name;
}
