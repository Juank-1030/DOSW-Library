package edu.eci.dosw.DOSW_Library.persistence.mongodb.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento MongoDB para la colección de usuarios.
 * Mapea la entidad User de JPA a documento NoSQL en MongoDB.
 * 
 * Contiene información de usuarios con especialización:
 * - LibrarianUser: Con permisos y departamento
 * - RegularUser: Con nivel de membresía y límite de préstamos
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDocument {

    /**
     * ID único del usuario, sincronizado con SQL
     */
    @Id
    private String id;

    /**
     * Nombre completo del usuario con índice para búsqueda
     */
    @Indexed
    private String name;

    /**
     * Email del usuario (único)
     */
    @Indexed(unique = true)
    private String email;

    /**
     * Usuario de login (único)
     */
    @Indexed(unique = true)
    private String username;

    /**
     * Hash BCrypt de la contraseña
     */
    private String passwordHash;

    /**
     * Rol del usuario: USUARIO o BIBLIOTECARIO
     */
    @Indexed
    private String role;

    /**
     * Permisos específicos del usuario (para BIBLIOTECARIO)
     * Embebido: [MANAGE_BOOKS, MANAGE_USERS, VIEW_ALL_LOANS]
     */
    private List<String> permissions;

    /**
     * Departamento al que pertenece (para BIBLIOTECARIO)
     */
    private String department;

    /**
     * Nivel de membresía (para USUARIO regular)
     * STANDARD, PREMIUM, VIP
     */
    private String membershipLevel;

    /**
     * Máximo de préstamos simultáneos permitidos (para USUARIO)
     */
    private Integer maxLoans;

    /**
     * Último acceso del usuario
     */
    private LocalDateTime lastLogin;

    /**
     * Intentos de login fallidos (para seguridad)
     */
    private Integer loginAttempts;

    /**
     * Fecha de creación del usuario
     */
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización
     */
    private LocalDateTime updatedAt;
}
