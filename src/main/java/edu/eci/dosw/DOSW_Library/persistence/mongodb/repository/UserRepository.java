package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio MongoDB para la colección de usuarios.
 * Proporciona operaciones CRUD y consultas personalizadas para UserDocument.
 *
 * Métodos heredados de MongoRepository:
 * - save(UserDocument) → UserDocument
 * - saveAll(Iterable) → Iterable
 * - findById(String) → Optional
 * - findAll() → List
 * - deleteById(String) → void
 * - delete(UserDocument) → void
 * - count() → long
 */
@Repository
public interface UserRepository extends MongoRepository<UserDocument, String> {

    /**
     * Busca un usuario por su correo electrónico (único).
     *
     * @param email el correo del usuario
     * @return Optional con el usuario si existe
     */
    @Query("{ 'email' : ?0 }")
    Optional<UserDocument> findByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario (único).
     *
     * @param username el nombre de usuario
     * @return Optional con el usuario si existe
     */
    @Query("{ 'username' : ?0 }")
    Optional<UserDocument> findByUsername(String username);

    /**
     * Busca todos los usuarios con un rol específico.
     *
     * @param role el rol (USER o LIBRARIAN)
     * @return lista de usuarios con el rol especificado
     */
    @Query("{ 'role' : ?0 }")
    List<UserDocument> findByRole(String role);

    /**
     * Busca todos los bibliotecarios.
     *
     * @return lista de todos los usuarios con rol LIBRARIAN
     */
    @Query(value = "{ 'role' : 'LIBRARIAN' }", fields = "{ 'name' : 1, 'email' : 1, 'permissions' : 1, 'department' : 1 }")
    List<UserDocument> findAllLibrarians();

    /**
     * Busca todos los usuarios regulares.
     *
     * @return lista de todos los usuarios con rol USER
     */
    @Query(value = "{ 'role' : 'USER' }", fields = "{ 'name' : 1, 'email' : 1, 'membershipLevel' : 1, 'maxLoans' : 1 }")
    List<UserDocument> findAllRegularUsers();

    /**
     * Busca usuarios con intentos de login fallidos (posible cuenta comprometida).
     *
     * @param attemptThreshold cantidad mínima de intentos
     * @return lista de usuarios sospechosos
     */
    @Query("{ 'loginAttempts' : { $gte : ?0 } }")
    List<UserDocument> findSuspiciousAccounts(int attemptThreshold);

    /**
     * Verifica si un email ya existe (para validación de registro).
     *
     * @param email el correo a validar
     * @return true si el email ya existe
     */
    @Query("{ 'email' : ?0 }")
    boolean existsByEmail(String email);

    /**
     * Verifica si un username ya existe (para validación de registro).
     *
     * @param username el nombre de usuario a validar
     * @return true si el username ya existe
     */
    @Query("{ 'username' : ?0 }")
    boolean existsByUsername(String username);

}
