package edu.eci.dosw.DOSW_Library.persistence.jpa.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad User.
 * Spring Data JPA genera la implementación automáticamente.
 *
 * NOTA: Esta es una interfaz interna de la capa JPA.
 * Los servicios usan la interfaz genérica:
 * persistence.repository.UserRepository
 * que es implementada por UserRepositoryJpaImpl.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, String> {
}
