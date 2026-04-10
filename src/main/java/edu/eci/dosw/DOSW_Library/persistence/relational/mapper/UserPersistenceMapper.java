package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.model.UserRole;
import edu.eci.dosw.DOSW_Library.core.model.UserStatus;
import edu.eci.dosw.DOSW_Library.controller.dto.CreateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UpdateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapeador bidirecional entre UserEntity (persistencia) y User (core.model)
 * 
 * Responsabilidades:
 * - Convertir UserEntity → User (resultado de consultas JPA)
 * - Convertir User → UserEntity (preparar para guardar en BD)
 * - Copiar enums entre capas: UserRole y UserStatus
 * 
 * Nota: Los valores de enum son identicos entre capas (USUARIO, BIBLIOTECARIO,
 * ACTIVE, SUSPENDED, BLOCKED)
 * 
 * @author DOSW-Library Team
 */
@Component
public class UserPersistenceMapper {

    /**
     * Convertir entidad JPA a modelo de dominio
     * 
     * @param entity UserEntity desde BD
     * @return User modelo de negocio, null si entity es null
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .role(convertRoleFromEntity(entity.getRole()))
                .status(convertStatusFromEntity(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convertir modelo de dominio a entidad JPA
     * 
     * Nota: No convierte timestamps (los manage PrePersist/PreUpdate)
     * 
     * @param domain User modelo de negocio
     * @return UserEntity lista para persistir, null si domain es null
     */
    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .username(domain.getUsername())
                .password(domain.getPassword())
                .role(convertRoleToEntity(domain.getRole()))
                .status(convertStatusToEntity(domain.getStatus()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    /**
     * Convertir UserRole de entidad a dominio
     */
    private UserRole convertRoleFromEntity(
            edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserRole entityRole) {
        if (entityRole == null) {
            return UserRole.USER; // Default
        }

        return switch (entityRole) {
            case USUARIO -> UserRole.USER;
            case BIBLIOTECARIO -> UserRole.LIBRARIAN;
        };
    }

    /**
     * Convertir UserRole de dominio a entidad
     */
    private edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserRole convertRoleToEntity(UserRole domainRole) {
        if (domainRole == null) {
            return edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserRole.USUARIO; // Default
        }

        return switch (domainRole) {
            case USER -> edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserRole.USUARIO;
            case LIBRARIAN -> edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserRole.BIBLIOTECARIO;
        };
    }

    /**
     * Convertir UserStatus de entidad a dominio
     */
    private UserStatus convertStatusFromEntity(
            edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserStatus entityStatus) {
        if (entityStatus == null) {
            return UserStatus.ACTIVE; // Default
        }

        return switch (entityStatus) {
            case ACTIVE -> UserStatus.ACTIVE;
            case SUSPENDED -> UserStatus.SUSPENDED;
            case BLOCKED -> UserStatus.BLOCKED;
        };
    }

    /**
     * Convertir UserStatus de dominio a entidad
     */
    private edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserStatus convertStatusToEntity(
            UserStatus domainStatus) {
        if (domainStatus == null) {
            return edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserStatus.ACTIVE; // Default
        }

        return switch (domainStatus) {
            case ACTIVE -> edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserStatus.ACTIVE;
            case SUSPENDED -> edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserStatus.SUSPENDED;
            case BLOCKED -> edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserStatus.BLOCKED;
        };
    }

    // ============== CONVERSIONES DTO → DOMAIN ==============

    /**
     * Convertir CreateUserDTO a model.User
     * 
     * @param createDTO DTO de creación desde controlador
     * @return User modelo, null si dto es null
     */
    public User toEntity(CreateUserDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        return User.builder()
                .id(createDTO.getId())
                .name(createDTO.getName())
                .email(createDTO.getEmail())
                .username(createDTO.getUsername())
                .password(createDTO.getPassword())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    /**
     * Convertir UpdateUserDTO a model.User
     * 
     * @param updateDTO    DTO de actualización
     * @param existingUser Usuario existente (para completar datos)
     * @return User modelo actualizado
     */
    public User toEntity(UpdateUserDTO updateDTO, User existingUser) {
        if (updateDTO == null || existingUser == null) {
            return existingUser;
        }

        return User.builder()
                .id(existingUser.getId())
                .name(updateDTO.getName() != null ? updateDTO.getName() : existingUser.getName())
                .email(updateDTO.getEmail() != null ? updateDTO.getEmail() : existingUser.getEmail())
                .username(existingUser.getUsername())
                .password(existingUser.getPassword())
                .role(existingUser.getRole())
                .status(existingUser.getStatus())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(existingUser.getUpdatedAt())
                .build();
    }

    /**
     * Actualiza un usuario existente con datos del DTO
     * Modifica el objeto directamente (in-place)
     * 
     * @param user      User existente (será modificado)
     * @param updateDTO DTO con datos para actualizar
     */
    public void updateEntity(User user, UpdateUserDTO updateDTO) {
        if (user == null || updateDTO == null) {
            return;
        }

        if (updateDTO.getName() != null && !updateDTO.getName().isBlank()) {
            user.setName(updateDTO.getName());
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) {
            user.setEmail(updateDTO.getEmail());
        }
    }

    // ============== CONVERSIONES DOMAIN → DTO ==============

    /**
     * Convertir model.User a UserDTO (para respuestas)
     * 
     * @param domain User modelo de negocio
     * @return UserDTO para enviar al cliente, null si domain es null
     */
    public UserDTO toDTO(User domain) {
        if (domain == null) {
            return null;
        }

        return UserDTO.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .build();
    }

    /**
     * Convertir lista de model.User a lista de UserDTO
     * 
     * @param users Lista de usuarios
     * @return Lista de DTOs, lista vacía si users es null
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
                .map(this::toDTO)
                .toList();
    }
}
