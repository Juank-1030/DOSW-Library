package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

/**
 * Enum que define los estados posibles de un usuario en la capa de
 * persistencia.
 * 
 * <p>
 * Espejo del enum en core.model.UserStatus para sincronización de datos.
 * </p>
 * <ul>
 * <li><b>ACTIVE</b> - Usuario activo y disponible para préstamos</li>
 * <li><b>SUSPENDED</b> - Usuario suspendido temporalmente (por límite de
 * préstamos excedido)</li>
 * <li><b>BLOCKED</b> - Usuario bloqueado permanentemente (por decisión del
 * bibliotecario)</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
public enum UserStatus {
    ACTIVE("Activo", "Usuario puede realizar préstamos"),
    SUSPENDED("Suspendido", "Usuario no puede hacer préstamos (límite excedido)"),
    BLOCKED("Bloqueado", "Usuario no puede hacer préstamos (decisión del bibliotecario)");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
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
