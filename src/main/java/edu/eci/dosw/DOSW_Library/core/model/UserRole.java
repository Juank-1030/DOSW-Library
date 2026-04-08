package edu.eci.dosw.DOSW_Library.core.model;

/**
 * Enum que define los roles disponibles en el sistema.
 * 
 * <p>
 * <b>Roles:</b>
 * </p>
 * <ul>
 * <li><b>USER</b> - Usuario regular (puede solicitar préstamos)</li>
 * <li><b>LIBRARIAN</b> - Bibliotecario (puede gestionar préstamos, usuarios,
 * inventario)</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
public enum UserRole {
    USER("Usuario Regular", "Puede solicitar y devolver préstamos"),
    LIBRARIAN("Bibliotecario", "Puede gestionar préstamos, usuarios e inventario");

    private final String displayName;
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
