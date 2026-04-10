package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

/**
 * Enum LoanStatus - Estados de prestamo persistidos en BD
 * 
 * Persitido en la columna 'status' de la tabla 'loans' como STRING
 * (gracias a @Enumerated(EnumType.STRING))
 * 
 * Valores:
 * - ACTIVE: prestamo vigente, libro en poder del usuario
 * - RETURNED: prestamo completado, libro devuelto
 * 
 * Transicion: ACTIVE → RETURNED (solo cambio unidireccional)
 * 
 * @author DOSW-Library Team
 */
public enum LoanStatus {
    /**
     * Prestamo vigente - libro en poder del usuario
     */
    ACTIVE("Activo", "Prestamo vigente, libro no devuelto"),

    /**
     * Prestamo completado - libro devuelto
     */
    RETURNED("Devuelto", "Prestamo completado, libro devuelto");

    /**
     * Nombre legible del estado
     */
    private final String displayName;

    /**
     * Descripcion del estado
     */
    private final String description;

    LoanStatus(String displayName, String description) {
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
