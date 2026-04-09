package edu.eci.dosw.DOSW_Library.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para acceso a datos de usuarios
 * 
 * Proporciona operaciones CRUD + consultas personalizadas para UserEntity
 * 
 * Verbo del cambio: Importar de persistence.relational.entity, no core.model
 * 
 * @author DOSW-Library Team
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Buscar usuario por email
     * 
     * @param email Email del usuario
     * @return Usuario si existe, vacio si no
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Buscar usuario por username (para login con JWT)
     * 
     * @param username Username del usuario
     * @return Usuario si existe, vacio si no
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Validar si existe un usuario por username
     * 
     * @param username Username a validar
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);

    /**
     * Validar si existe un usuario por email
     * 
     * @param email Email a validar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Consulta custom: contar usuarios por rol
     * 
     * @param role Rol a contar
     * @return Cantidad de usuarios con ese rol
     */
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = ?1")
    long countByRole(String role);

}
