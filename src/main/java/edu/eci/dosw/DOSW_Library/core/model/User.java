package edu.eci.dosw.DOSW_Library.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "loans")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "uk_users_email"),
        @UniqueConstraint(columnNames = "username", name = "uk_users_username"),
        @UniqueConstraint(columnNames = "dni", name = "uk_users_dni")
})
public class User {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    /**
     * NUEVO: Username para login (unico)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * NUEVO: Password en hash BCrypt
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * NUEVO: Rol del usuario (USER, LIBRARIAN)
     */
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * NUEVO: Estado del usuario (ACTIVE, SUSPENDED, BLOCKED)
     */
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * NUEVO: DNI único del usuario
     */
    @Column(nullable = false, unique = true, length = 15)
    private String dni;

    /**
     * AUDITORÍA: Usuario que creó este registro
     */
    @Column(length = 50)
    private String createdBy;

    /**
     * AUDITORÍA: Usuario que modificó este registro
     */
    @Column(length = 50)
    private String modifiedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Relación 1:N con Loan (un usuario puede tener muchos préstamos)
     * En MongoDB, las relaciones se manejan de forma denormalizada o por referencia
     */
    @Builder.Default
    private List<Loan> loans = new ArrayList<>();

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
        if (this.role == null) {
            this.role = UserRole.USER;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}