package edu.eci.dosw.DOSW_Library.core.repository;

import edu.eci.dosw.DOSW_Library.core.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar registros de auditoría.
 * 
 * <p>
 * <b>Propósito:</b> Proporcionar acceso a logs de cambios en la BD
 * </p>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Obtiene todos los cambios de una entidad específica.
     * 
     * @param entity Nombre de la entidad (ej: "User", "Book")
     * @return Lista de cambios para esa entidad
     */
    List<AuditLog> findByEntity(String entity);

    /**
     * Obtiene todos los cambios de una entidad específica (por ID).
     * 
     * @param entity   Nombre de la entidad
     * @param entityId ID de la entidad
     * @return Lista de cambios para esa entidad específica
     */
    List<AuditLog> findByEntityAndEntityId(String entity, String entityId);

    /**
     * Obtiene todos los cambios realizados por un usuario.
     * 
     * @param changedBy Usuario que hizo los cambios
     * @return Lista de cambios hechos por ese usuario
     */
    List<AuditLog> findByChangedBy(String changedBy);

    /**
     * Obtiene cambios en un rango de fechas.
     * 
     * @param startDate Fecha inicial
     * @param endDate   Fecha final
     * @return Lista de cambios en ese rango
     */
    @Query("SELECT a FROM AuditLog a WHERE a.changedAt BETWEEN :startDate AND :endDate ORDER BY a.changedAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
