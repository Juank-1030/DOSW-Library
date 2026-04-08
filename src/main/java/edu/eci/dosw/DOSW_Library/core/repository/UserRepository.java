package edu.eci.dosw.DOSW_Library.core.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gestionar persistencia de usuarios.
 * 
 * <p>
 * <b>Métodos heredados automáticamente de JpaRepository:</b>
 * </p>
 * <ul>
 * <li>save(User user) - Guardar o actualizar usuario</li>
 * <li>findById(String id) - Buscar usuario por ID</li>
 * <li>findAll() - Obtener todos los usuarios</li>
 * <li>deleteById(String id) - Eliminar usuario</li>
 * <li>count() - Contar usuarios</li>
 * <li>existsById(String id) - Verificar si existe</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // ============================================
    // MÉTODOS QUERY (generación automática SQL)
    // ============================================

    /**
     * Busca un usuario por su email.
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM users WHERE email = ?
     * </pre>
     * 
     * <p>
     * <b>Uso típico:</b>
     * </p>
     * 
     * <pre>{@code
     * Optional<User> user = userRepository.findByEmail("john@example.com");
     * if (user.isPresent()) {
     *     // Usuario existe
     * }
     * }</pre>
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su username (para autenticación).
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM users WHERE username = ?
     * </pre>
     * 
     * @param username Username del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca usuarios cuyo nombre contenga un texto.
     * 
     * <p>
     * <b>SQL generado automáticamente:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM users WHERE name LIKE %?%
     * </pre>
     * 
     * @param name Texto a buscar en el nombre
     * @return Lista de usuarios cuyo nombre contiene el texto
     */
    List<User> findByNameContaining(String name);

    /**
     * Busca usuarios por nombre exacto.
     * 
     * @param name Nombre exacto del usuario
     * @return Lista de usuarios con ese nombre
     */
    List<User> findByName(String name);

    /**
     * Busca usuarios cuyo nombre empiece con un texto.
     * 
     * <p>
     * <b>SQL generado:</b>
     * </p>
     * 
     * <pre>
     * SELECT * FROM users WHERE name LIKE ?%
     * </pre>
     * 
     * @param prefix Prefijo del nombre
     * @return Lista de usuarios cuyo nombre empieza con el prefijo
     */
    List<User> findByNameStartingWith(String prefix);

    // ============================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ============================================

    /**
     * Busca usuarios por nombre o email (búsqueda flexible).
     * 
     * <p>
     * <b>JPQL personalizado:</b>
     * </p>
     * 
     * <pre>
     * SELECT u FROM User u 
     * WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
     *    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
     * </pre>
     * 
     * <p>
     * <b>Ejemplo de uso:</b>
     * </p>
     * 
     * <pre>{@code
     * List<User> results = userRepository.searchByNameOrEmail("john");
     * // Retorna: "John Doe" (por nombre)
     * // "alice@john.com" (por email)
     * }</pre>
     * 
     * @param searchTerm Término de búsqueda (case-insensitive)
     * @return Lista de usuarios que coinciden
     */
    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchByNameOrEmail(@Param("searchTerm") String searchTerm);

    /**
     * Obtiene usuarios ordenados por nombre.
     * 
     * @return Lista de usuarios ordenados alfabéticamente
     */
    @Query("SELECT u FROM User u ORDER BY u.name ASC")
    List<User> findAllOrderByName();

    /**
     * Cuenta usuarios con email registrado.
     * 
     * <p>
     * <b>JPQL:</b>
     * </p>
     * 
     * <pre>
     * SELECT COUNT(u) FROM User u WHERE u.email IS NOT NULL
     * </pre>
     * 
     * @return Cantidad de usuarios con email
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.email IS NOT NULL")
    long countUsersWithEmail();

    /**
     * Obtiene usuarios sin email registrado.
     * 
     * @return Lista de usuarios sin email
     */
    @Query("SELECT u FROM User u WHERE u.email IS NULL OR u.email = ''")
    List<User> findUsersWithoutEmail();

    // ============================================
    // MÉTODOS DE VERIFICACIÓN
    // ============================================

    /**
     * Verifica si existe un usuario con un email específico.
     * 
     * <p>
     * <b>Uso típico:</b> Validar email único antes de registrar
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * if (userRepository.existsByEmail("john@example.com")) {
     *     throw new IllegalArgumentException("Email already registered");
     * }
     * }</pre>
     * 
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con un username específico.
     * 
     * <p>
     * <b>Uso típico:</b> Validar username único antes de registrar
     * </p>
     * 
     * @param username Username a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con un nombre específico.
     * 
     * @param name Nombre a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);
}