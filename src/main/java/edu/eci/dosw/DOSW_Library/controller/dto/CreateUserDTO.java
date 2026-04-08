package edu.eci.dosw.DOSW_Library.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo usuario en el sistema.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para registrar un nuevo usuario")
public class CreateUserDTO {

    @NotBlank(message = "User ID cannot be empty")
    @Size(min = 3, max = 20, message = "User ID must be between 3 and 20 characters")
    @Schema(description = "Identificador único del usuario", example = "USR-001", required = true, minLength = 3, maxLength = 20)
    @JsonProperty("id")
    private String id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Schema(description = "Nombre completo del usuario", example = "John Doe", required = true, maxLength = 100)
    @JsonProperty("name")
    private String name;

    @Email(message = "Email must be a valid email address")
    @Schema(description = "Correo electrónico del usuario (opcional)", example = "john.doe@example.com", format = "email")
    @JsonProperty("email")
    private String email;

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

    @Schema(description = "Rol del usuario", example = "USUARIO", allowableValues = { "ADMIN", "BIBLIOTECARIO",
            "USUARIO" }, required = false)
    @JsonProperty("role")
    private String role;
}