package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar un nuevo usuario en el sistema.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para registrar un nuevo usuario")
public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Nombre de usuario para login (único)", example = "johndoe", required = true, minLength = 3, maxLength = 50)
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    @Schema(description = "Contraseña del usuario (será hasheada con BCrypt)", example = "SecurePassword123!", required = true, minLength = 6)
    @JsonProperty("password")
    private String password;

    @Schema(description = "Rol del usuario (USER por defecto, solo LIBRARIAN puede crear LIBRARIAN)", example = "USER", allowableValues = {
            "USER", "LIBRARIAN" }, required = false)
    @JsonProperty("role")
    private String role;

    @Schema(description = "Nombre completo del usuario (opcional)", example = "John Doe", maxLength = 100)
    @JsonProperty("name")
    private String name;

    @Schema(description = "Email del usuario (opcional)", example = "john@example.com", format = "email")
    @JsonProperty("email")
    private String email;
}
