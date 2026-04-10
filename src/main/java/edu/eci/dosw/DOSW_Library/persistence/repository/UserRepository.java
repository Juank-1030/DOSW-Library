package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para operaciones de persistencia sobre User.
 * Define un contrato que puede ser implementado por diferentes fuentes de datos
 * (MongoDB, JPA/PostgreSQL, etc.)
 *
 * Esta es la interfaz de abstracción de persistencia.
 * Las implementaciones concretas (MongoDB, JPA) deben implementar estos
 * métodos.
 */
public interface UserRepository {

    /**
     * Guarda un nuevo usuario o actualiza uno existente.
     *
     * @param user el usuario a guardar
     * @return el usuario guardado
     */
    User save(User user);

    /**
     * Guarda múltiples usuarios en lote.
     *
     * @param users lista de usuarios a guardar
     * @return lista de usuarios guardados
     */
    List<User> saveAll(List<User> users);

    /**
     * Busca un usuario por su ID.
     *
     * @param id el identificador del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findById(String id);

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de todos los usuarios
     */
    List<User> findAll();

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email el correo del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username el nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca todos los usuarios con un rol específico.
     *
     * @param role el rol (USER, LIBRARIAN)
     * @return lista de usuarios con ese rol
     */
    List<User> findByRole(String role);

    /**
     * Busca todos los bibliotecarios.
     *
     * @return lista de todos los usuarios con rol LIBRARIAN
     */
    List<User> findAllLibrarians();

    /**
     * Busca todos los usuarios regulares.
     *
     * @return lista de todos los usuarios con rol USER
     */
    List<User> findAllRegularUsers();

    /**
     * Busca usuarios con intentos de login fallidos (posible cuenta comprometida).
     *
     * @param attemptThreshold cantidad mínima de intentos
     * @return lista de usuarios sospechosos
     */
    List<User> findSuspiciousAccounts(int attemptThreshold);

    /**
     * Verifica si un email ya existe.
     *
     * @param email el correo a validar
     * @return true si el email ya existe
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si un username ya existe.
     *
     * @param username el nombre de usuario a validar
     * @return true si el username ya existe
     */
    boolean existsByUsername(String username);

    /**
     * Elimina un usuario por su ID.
     *
     * @param id el identificador del usuario a eliminar
     */
    void deleteById(String id);

    /**
     * Elimina un usuario.
     *
     * @param user el usuario a eliminar
     */
    void delete(User user);

    /**
     * Elimina todos los usuarios.
     */
    void deleteAll();

    /**
     * Cuenta la cantidad total de usuarios.
     *
     * @return cantidad de usuarios
     */
    long count();

    /**
     * Verifica si existe un usuario con el ID especificado.
     *
     * @param id el identificador a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsById(String id);

}
