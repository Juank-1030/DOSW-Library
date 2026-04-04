package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar información de un usuario existente.
 * 
 * <p>
 * <b>Campos editables:</b>
 * </p>
 * <ul>
 * <li>✅ Nombre - Puede cambiar (ej: cambio legal de nombre)</li>
 * <li>✅ Email - Puede cambiar (ej: nuevo correo)</li>
 * <li>❌ ID - NO se puede cambiar (es el identificador único)</li>
 * </ul>
 * 
 * <p>
 * <b>Usado en:</b>
 * </p>
 * <ul>
 * <li>PATCH /api/users/{id} - Actualización parcial</li>
 * <li>PUT /api/users/{id} - Actualización completa</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar información de un usuario (campos opcionales)")
public class UpdateUserDTO {

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Schema(description = "Nuevo nombre del usuario (opcional)", example = "Jane Doe", maxLength = 100)
    @JsonProperty("name")
    private String name;

    @Email(message = "Email must be a valid email address")
    @Schema(description = "Nuevo email del usuario (opcional)", example = "jane.doe@example.com", format = "email")
    @JsonProperty("email")
    private String email;
}