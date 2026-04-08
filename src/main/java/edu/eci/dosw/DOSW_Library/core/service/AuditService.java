package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.model.AuditLog;
import edu.eci.dosw.DOSW_Library.core.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para registrar cambios en la BD (Auditoría).
 * 
 * <p>
 * <b>Propósito: INTEGRIDAD</b> - Registrar quién cambió qué, cuándo y por qué.
 * </p>
 * 
 * <p>
 * <b>Ejemplo de uso:</b>
 * </p>
 * 
 * <pre>{@code
 * auditService.logUpdate(
 *         "User",
 *         "USR-001",
 *         "status",
 *         "ACTIVE",
 *         "SUSPENDED",
 *         "USR-002", // el bibliotecario que hizo el cambio
 *         "Límite de préstamos excedido");
 * }</pre>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    /**
     * Registra una acción INSERT en la auditoría.
     * 
     * @param entity    Nombre de la entidad (ej: "User")
     * @param entityId  ID de la entidad (ej: "USR-001")
     * @param changedBy Usuario que realizó la acción
     * @param reason    Razón del cambio
     */
    public void logInsert(String entity, String entityId, String changedBy, String reason) {
        logAction(entity, entityId, "INSERT", null, null, null, changedBy, reason);
    }

    /**
     * Registra una acción UPDATE en la auditoría.
     * 
     * @param entity    Nombre de la entidad (ej: "User")
     * @param entityId  ID de la entidad
     * @param field     Campo que cambió
     * @param oldValue  Valor anterior
     * @param newValue  Valor nuevo
     * @param changedBy Usuario que realizó el cambio
     * @param reason    Razón del cambio
     */
    public void logUpdate(String entity, String entityId, String field,
            String oldValue, String newValue, String changedBy, String reason) {
        logAction(entity, entityId, "UPDATE", field, oldValue, newValue, changedBy, reason);
    }

    /**
     * Registra una acción DELETE en la auditoría.
     * 
     * @param entity    Nombre de la entidad
     * @param entityId  ID de la entidad
     * @param changedBy Usuario que realizó la eliminación
     * @param reason    Razón de la eliminación
     */
    public void logDelete(String entity, String entityId, String changedBy, String reason) {
        logAction(entity, entityId, "DELETE", null, null, null, changedBy, reason);
    }

    /**
     * Registra una acción de auditoría genérica.
     * 
     * @param entity    Nombre de la entidad
     * @param entityId  ID de la entidad
     * @param action    Tipo de acción (INSERT, UPDATE, DELETE)
     * @param field     Campo que cambió (solo para UPDATE)
     * @param oldValue  Valor anterior (solo para UPDATE)
     * @param newValue  Valor nuevo (solo para UPDATE)
     * @param changedBy Usuario que realizó el cambio
     * @param reason    Razón del cambio
     */
    private void logAction(String entity, String entityId, String action,
            String field, String oldValue, String newValue,
            String changedBy, String reason) {
        try {
            AuditLog log = AuditLog.builder()
                    .entity(entity)
                    .entityId(entityId)
                    .action(action)
                    .field(field)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .changedBy(changedBy)
                    .reason(reason)
                    .ipAddress(getClientIpAddress())
                    .changedAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(log);

            logger.info("AUDIT: {} | {} | {} | Field: {} | By: {} | Reason: {}",
                    action, entity, entityId, field, changedBy, reason);

        } catch (Exception e) {
            logger.error("Error logging audit | Entity: {} | EntityId: {} | Error: {}",
                    entity, entityId, e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los cambios de una entidad específica.
     * 
     * @param entity Nombre de la entidad
     * @return Lista de cambios
     */
    public List<AuditLog> getAuditsByEntity(String entity) {
        return auditLogRepository.findByEntity(entity);
    }

    /**
     * Obtiene todos los cambios de una entidad específica (por ID).
     * 
     * @param entity   Nombre de la entidad
     * @param entityId ID de la entidad
     * @return Lista de cambios
     */
    public List<AuditLog> getAuditsByEntityAndId(String entity, String entityId) {
        return auditLogRepository.findByEntityAndEntityId(entity, entityId);
    }

    /**
     * Obtiene la dirección IP del cliente actual.
     * 
     * @return IP del cliente o "UNKNOWN" si no está disponible
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return "UNKNOWN";
            }
            HttpServletRequest request = attrs.getRequest();

            // Intento obtener la IP real si viene tras un proxy
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            return ip;
        } catch (Exception e) {
            logger.debug("Could not retrieve client IP address", e);
            return "UNKNOWN";
        }
    }
}
