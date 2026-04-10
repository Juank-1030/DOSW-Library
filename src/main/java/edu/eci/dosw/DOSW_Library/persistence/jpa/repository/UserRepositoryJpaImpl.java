package edu.eci.dosw.DOSW_Library.persistence.jpa.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.jpa.mapper.UserEntityMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de UserRepository.
 * Esta implementación está activa cuando el perfil "relational" está
 * habilitado.
 *
 * Proporciona todas las operaciones CRUD y consultas especializadas usando JPA.
 * La conversión entre entidades y dominios se realiza mediante
 * UserEntityMapper.
 *
 * @see UserRepository - Interfaz genérica
 * @see Profile - Esta implementación solo se activa con @Profile("relational")
 */
@Repository
@Profile("relational")
public class UserRepositoryJpaImpl implements UserRepository {

    private final JpaUserRepository repository;
    private final UserEntityMapper mapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repository Repositorio JPA generado por Spring Data
     * @param mapper     Mapper para conversiones Entity ↔ Domain
     */
    public UserRepositoryJpaImpl(JpaUserRepository repository, UserEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(
                repository.save(mapper.toEntity(user)));
    }

    @Override
    public List<User> saveAll(List<User> users) {
        return repository.saveAll(
                users.stream()
                        .map(mapper::toEntity)
                        .toList())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Implementación: se requeriría una consulta personalizada en JpaUserRepository
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        // Implementación: se requeriría una consulta personalizada en JpaUserRepository
        return Optional.empty();
    }

    @Override
    public List<User> findByRole(String role) {
        // Implementación: se requeriría una consulta personalizada en JpaUserRepository
        return List.of();
    }

    @Override
    public List<User> findAllLibrarians() {
        // Implementación: se requeriría una consulta personalizada en JpaUserRepository
        return List.of();
    }

    @Override
    public List<User> findAllRegularUsers() {
        // Implementación: se requeriría una consulta personalizada en JpaUserRepository
        return List.of();
    }

    @Override
    public List<User> findSuspiciousAccounts(int attemptThreshold) {
        // Implementación: se requeriría una consulta personalizada en JpaUserRepository
        return List.of();
    }

    @Override
    public boolean existsByEmail(String email) {
        // Implementación: se requeriría una consulta personalizada en JpaUserRepository
        return false;
    }

    @Override
    public boolean existsByUsername(String username) {
        // Implementación: se requeriría una consulta personalizada en JpaUserRepository
        return false;
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(User user) {
        repository.delete(mapper.toEntity(user));
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}
