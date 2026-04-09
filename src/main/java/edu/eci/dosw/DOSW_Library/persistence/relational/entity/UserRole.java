package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

/**
 * Enum UserRole - Roles de usuario persistidos en BD
 * 
 * Persitido en la columna 'role' de la tabla 'users' como STRING
 * (gracias a @Enumerated(EnumType.STRING))
 * 
 * Valores:
 * - BIBLIOTECARIO: acceso administrativo completo
 * - USUARIO: usuario regular con permisos limitados
 * 
 * @author DOSW-Library Team
 */
public enum UserRole {
    /**
     * Rol de bibliotecario - acceso administrativo completo
     */
    BIBLIOTECARIO("Bibliotecario", "Gestiona libros, usuarios, ve todos los prestamos"),

    /**
     * Rol de usuario regular - acceso limitado a prestamos personales
     */
    USUARIO("Usuario", "Solicita y devuelve prestamos, ve solo sus prestamos");

    /**
     * Nombre legible del rol
     */
    private final String displayName;

    /**
     * Descripcion del rol
     */
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
