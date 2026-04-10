package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz Spring Data MongoDB para la colección de usuarios.
 * Proporciona operaciones CRUD y consultas personalizadas para UserDocument.
 */
public interface MongoUserRepository extends MongoRepository<UserDocument, String> {

    @Query("{ 'email' : ?0 }")
    Optional<UserDocument> findByEmail(String email);

    @Query("{ 'username' : ?0 }")
    Optional<UserDocument> findByUsername(String username);

    @Query("{ 'role' : ?0 }")
    List<UserDocument> findByRole(String role);

    @Query(value = "{ 'role' : 'LIBRARIAN' }", fields = "{ 'name' : 1, 'email' : 1, 'permissions' : 1, 'department' : 1 }")
    List<UserDocument> findAllLibrarians();

    @Query("{ 'email' : ?0, 'role' : 'LIBRARIAN' }")
    Optional<UserDocument> findLibrarianByEmail(String email);

    @Query("{ 'role' : ?0, 'active' : true }")
    List<UserDocument> findActiveUsersByRole(String role);
}
