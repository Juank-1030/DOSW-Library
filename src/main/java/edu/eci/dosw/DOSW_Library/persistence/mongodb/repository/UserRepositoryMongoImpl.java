package edu.eci.dosw.DOSW_Library.persistence.mongodb.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.mongodb.mapper.UserDocumentMapper;
import edu.eci.dosw.DOSW_Library.persistence.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación MongoDB de UserRepository.
 * Esta implementación está activa cuando el perfil "mongo" está habilitado.
 *
 * Proporciona todas las operaciones CRUD y consultas especializadas usando
 * MongoDB.
 * La conversión entre documentos y dominios se realiza mediante
 * UserDocumentMapper.
 *
 * @see UserRepository - Interfaz genérica
 * @see Profile - Esta implementación solo se activa con @Profile("mongo")
 */
@Repository
@Profile("mongo")
public class UserRepositoryMongoImpl implements UserRepository {

    private final MongoUserRepository repository;
    private final UserDocumentMapper mapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repository Repositorio MongoDB generado por Spring Data
     * @param mapper     Mapper para conversiones Document ↔ Domain
     */
    public UserRepositoryMongoImpl(MongoUserRepository repository, UserDocumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(
                repository.save(mapper.toDocument(user)));
    }

    @Override
    public List<User> saveAll(List<User> users) {
        return repository.saveAll(
                users.stream()
                        .map(mapper::toDocument)
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
        return repository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findByRole(String role) {
        return repository.findByRole(role).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllLibrarians() {
        return repository.findAllLibrarians().stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Optional<User> findLibrarianByEmail(String email) {
        return repository.findLibrarianByEmail(email)
                .map(mapper::toDomain);
    }

    public List<User> findActiveUsersByRole(String role) {
        return repository.findActiveUsersByRole(role).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllRegularUsers() {
        return repository.findByRole("USER").stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.findByUsername(username).isPresent();
    }

    @Override
    public List<User> findSuspiciousAccounts(int attemptThreshold) {
        // Placeholder para consulta personalizada de seguridad
        return List.of();
    }

    @Override
    public void delete(User user) {
        if (user != null && user.getId() != null) {
            repository.deleteById(user.getId());
        }
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
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
