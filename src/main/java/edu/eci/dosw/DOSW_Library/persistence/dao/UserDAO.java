package edu.eci.dosw.DOSW_Library.persistence.dao;

import edu.eci.dosw.DOSW_Library.core.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO UserDAO - Data Access Object para operaciones complejas sobre usuarios
 * 
 * IMPORTANTE: User es una entidad JPA (@Entity en core.model)
 * El DAO accede directamente a EntityManager para:
 * - Queries complejas (ej: busquedas por rol con filtros)
 * - Updates batch (cambio masivo de roles, etc.)
 * - Operaciones de auditoria
 * 
 * @author DOSW-Library Team
 */
@Component
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Obtener todos los usuarios con un rol especifico
     * 
     * @param role nombre del rol (ej: "BIBLIOTECARIO", "USUARIO")
     * @return lista de usuarios con ese rol
     */
    public List<User> findByRole(String role) {
        String jpql = "SELECT u FROM User u WHERE u.role = :role ORDER BY u.name";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("role", role)
                .getResultList();
    }

    /**
     * Contar cantidad de usuarios con un rol especifico
     * 
     * @param role nombre del rol
     * @return cantidad de usuarios
     */
    public long countByRole(String role) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.role = :role";
        Long result = (Long) entityManager.createQuery(jpql)
                .setParameter("role", role)
                .getSingleResult();
        return result != null ? result : 0L;
    }

    /**
     * Obtener estadisticas de usuarios totales
     * 
     * @return cantidad total de usuarios registrados
     */
    public long getTotalUsersCount() {
        String jpql = "SELECT COUNT(u) FROM User u";
        Long result = (Long) entityManager.createQuery(jpql).getSingleResult();
        return result != null ? result : 0L;
    }

    /**
     * Busqueda avanzada: usuarios por nombre (substring insensible a mayusculas)
     * 
     * @param nameKeyword palabra clave del nombre
     * @return lista de usuarios que coinciden
     */
    public List<User> searchByName(String nameKeyword) {
        String jpql = "SELECT u FROM User u WHERE LOWER(u.name) LIKE CONCAT('%', LOWER(:keyword), '%') ORDER BY u.name";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("keyword", nameKeyword)
                .getResultList();
    }

    /**
     * Cambiar rol de usuario (operacion batch, sin cargar entidad)
     * 
     * @param userId  identificador del usuario
     * @param newRole nuevo rol (ej: "BIBLIOTECARIO")
     */
    @Transactional
    public void changeUserRole(String userId, String newRole) {
        String sql = "UPDATE users SET role = :role WHERE id = :id";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("role", newRole);
        query.setParameter("id", userId);
        int updated = query.executeUpdate();

        if (updated == 0) {
            throw new IllegalStateException("Usuario no encontrado: " + userId);
        }
    }

    /**
     * Obtener usuarios registrados recientemente (ultimos N dias)
     * 
     * @param daysAgo cantidad de dias atras
     * @return lista de usuarios creados en ultimos N dias
     */
    public List<User> getRecentUsers(int daysAgo) {
        String jpql = "SELECT u FROM User u WHERE u.createdAt >= CURRENT_TIMESTAMP - :days DAY ORDER BY u.createdAt DESC";
        // Nota: la sintaxis exacta depende del dialecto SQL (H2 en este caso)
        return entityManager.createQuery(jpql, User.class)
                .setParameter("days", daysAgo)
                .getResultList();
    }

    /**
     * Validar credenciales de usuario (para login)
     * IMPORTANTE: Esta operacion DEBERIA hacerla Spring Security con
     * UserDetailsService
     * Este metodo es solo referencia de como acceso directo a EntityManager
     * 
     * @param username     username del usuario
     * @param passwordHash hash BCrypt de contrasena (ya hasheada)
     * @return usuario si credenciales son validas, NULL si no existen
     */
    public User validateCredentials(String username, String passwordHash) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        List<User> results = entityManager.createQuery(jpql, User.class)
                .setParameter("username", username)
                .getResultList();

        // En prod: usar BCryptPasswordEncoder para comparar hashes
        // Aqui es solo para ilustracion (NUNCA hacer esto en produccion)
        if (results.isEmpty()) {
            return null;
        }

        User user = results.get(0);
        // TODO: comparar user.getPassword() con passwordHash usando
        // BCryptPasswordEncoder
        return user;
    }
}
