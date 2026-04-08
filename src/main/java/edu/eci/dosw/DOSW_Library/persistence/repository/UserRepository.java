package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para User
 * 
 * IMPORTANTE: User es una entidad JPA (@Entity en core.model)
 * No usamos UserEntity - el modelo de dominio ES la entidad JPA
 * 
 * Proporciona:
 * - CRUD automatico heredado de JpaRepository
 * - Query methods para buscar por username, email, role
 * - Consultas JPQL personalizadas
 * 
 * @author DOSW-Library Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Query method automatico: SELECT * FROM users WHERE username = ?
     * IMPORTANTE: username es UNIQUE, por eso retorna Optional
     * 
     * Requerido para login: obtener usuario por username sin saber su ID
     */
    Optional<User> findByUsername(String username);

    /**
     * Query method automatico: SELECT * FROM users WHERE email = ?
     * Email es UNIQUE, por eso retorna Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Consulta JPQL: verificar si existe un usuario con ese email
     * Util para validar unicidad ANTES de inserciones
     * 
     * @param email email a verificar
     * @return true si existe usuario con ese email
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Consulta JPQL: verificar si existe un usuario con ese username
     * Util para validar unicidad ANTES de inserciones
     * 
     * @param username username a verificar
     * @return true si existe usuario con ese username
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);
}
