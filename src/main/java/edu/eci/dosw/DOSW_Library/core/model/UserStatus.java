package edu.eci.dosw.DOSW_Library.core.model;

/**
 * Enum que define los estados posibles de un usuario.
 * 
 * <p>
 * <b>Estados:</b>
 * </p>
 * <ul>
 * <li><b>ACTIVE</b> - Usuario activo y disponible para préstamos</li>
 * <li><b>SUSPENDED</b> - Usuario suspendido temporalmente (por límite de
 * préstamos excedido)</li>
 * <li><b>BLOCKED</b> - Usuario bloqueado permanentemente (por decisión del
 * bibliotecario)</li>
 * </ul>
 * 
 * <p>
 * <b>Transiciones permitidas:</b>
 * </p>
 * <ul>
 * <li>ACTIVE → SUSPENDED (automático si excede límite)</li>
 * <li>SUSPENDED → ACTIVE (bibliotecario desbloquea)</li>
 * <li>ACTIVE → BLOCKED (bibliotecario bloquea)</li>
 * <li>SUSPENDED → BLOCKED (bibliotecario bloquea)</li>
 * <li>BLOCKED → ACTIVE (solo administrador)</li>
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

    /**
     * Verifica si el usuario puede hacer préstamos en este estado.
     * 
     * @return true si solo ACTIVE puede hacer préstamos
     */
    public boolean canMakeLoans() {
        return this == ACTIVE;
    }
}
