package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad UserEntity - Mapeo JPA de la tabla USER en base de datos.
 * 
 * Responsabilidades:
 * - Representar un usuario con autenticacion y autorizacion
 * - Mantener relacion 1:N con Loan (un usuario puede tener muchos prestamos)
 * - Persistir credenciales (username, password BCrypt), rol y metadata
 * 
 * Cambios vs version anterior:
 * - NUEVO: username (unico, para login)
 * - NUEVO: password (BCrypt hash)
 * - NUEVO: role (ENUM: BIBLIOTECARIO, USUARIO)
 * - NUEVO: timestamps para auditoria
 * 
 * @author DOSW-Library Team
 * @version 2.0 (Con normalizacion a 3FN)
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "loans")
public class UserEntity {

    /**
     * Identificador unico del usuario (USR-001, USR-002, etc.)
     * Generado por IdGeneratorUtil con prefijo "USR"
     */
    @Id
    @Column(name = "id", length = 20)
    private String id;

    /**
     * Nombre completo del usuario
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Email del usuario (unico)
     * Usado para contacto y recuperacion de contrasena
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Username para login (unico)
     * NUEVO: Requerido para autenticacion con JWT
     * Minimo 3 caracteres, sin espacios
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Password en hash BCrypt
     * NUEVO: Requerido para autenticacion
     * IMPORTANTE: NUNCA guardar en texto plano
     * Ejemplo de hash: $2a$10$dXJ3SW6G7P50eS2pFSZte...
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Rol del usuario (ENUM persitido como STRING)
     * NUEVO: Requerido para autorizacion basada en roles
     * 
     * Valores posibles:
     * - BIBLIOTECARIO: puede ver/crear/actualizar/eliminar libros y usuarios, ver
     * todos los prestamos
     * - USUARIO: puede solicitar prestamos, devolver libros, ver solo sus prestamos
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Estado del usuario (ENUM persistido como STRING)
     * NUEVO: Controla si el usuario puede realizar operaciones
     * 
     * Valores posibles:
     * - ACTIVE: Usuario activo y disponible para préstamos
     * - SUSPENDED: Suspendido temporalmente (límite de préstamos excedido)
     * - BLOCKED: Bloqueado permanentemente (decisión del bibliotecario)
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * Fecha de creacion del registro
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de ultima actualizacion
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Relacion inversa 1:N: un usuario puede tener muchos prestamos
     * mappedBy = "user" → se sincroniza bidireccional con LoanEntity.user
     * cascade = ALL → si se elimina el usuario, se eliminan sus prestamos
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanEntity> loans;

    /**
     * Pre-persiste: inicializa timestamps
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Pre-update: actualiza timestamp de modificacion
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
