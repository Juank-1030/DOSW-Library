package edu.eci.dosw.DOSW_Library.persistence.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.CreateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UpdateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.persistence.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper consolidado para todas las transformaciones de User.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>✅ UserEntity ↔ User (persistencia ↔ dominio)</li>
 * <li>✅ User ↔ UserDTO (dominio ↔ API Response)</li>
 * <li>✅ CreateUserDTO → User (API Request → dominio)</li>
 * <li>✅ UpdateUserDTO → User updates (actualizaciones parciales)</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 3.0 - Mapper consolidado (sin controller/mapper)
 */
@Component
public class UserPersistenceMapper {

    private static final Logger logger = LoggerFactory.getLogger(UserPersistenceMapper.class);

    // ============================================
    // PERSISTENCE LAYER (UserEntity ↔ User)
    // ============================================

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
                .role(entity.getRole().name())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(UserRole.valueOf(user.getRole()))
                .build();
    }

    // ============================================
    // API LAYER (User ↔ UserDTO)
    // ============================================

    /**
     * User → UserDTO (NO expone credenciales por seguridad)
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            logger.warn("Attempted to convert null User to UserDTO");
            return null;
        }

        logger.debug("Converting User to DTO | ID: {} | Name: '{}'",
                user.getId(),
                user.getName());

        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        logger.trace("User converted successfully: {}", dto);

        return dto;
    }

    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            logger.warn("Attempted to convert null User list to DTO list");
            return List.of();
        }

        logger.debug("Converting {} users to DTO list", users.size());

        List<UserDTO> dtoList = users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        logger.debug("Converted {} users to DTOs successfully", dtoList.size());

        return dtoList;
    }

    // ============================================
    // DTO → ENTITY (API Request → Dominio)
    // ============================================

    /**
     * CreateUserDTO → User (para registro)
     */
    public User toEntity(CreateUserDTO createDTO) {
        if (createDTO == null) {
            logger.warn("Attempted to convert null CreateUserDTO to User");
            return null;
        }

        logger.debug("Converting CreateUserDTO to User | ID: {} | Username: '{}'",
                createDTO.getId(),
                createDTO.getUsername());

        User user = User.builder()
                .id(createDTO.getId())
                .name(createDTO.getName())
                .email(createDTO.getEmail())
                .username(createDTO.getUsername())
                .password(createDTO.getPassword())
                .role(createDTO.getRole() != null ? createDTO.getRole() : "USUARIO")
                .build();

        logger.info("User entity created from DTO | ID: {} | Username: {} | Role: {}",
                user.getId(),
                user.getUsername(),
                user.getRole());

        return user;
    }

    // ============================================
    // ACTUALIZACIÓN DE USUARIO
    // ============================================

    /**
     * Actualiza User con datos de UpdateUserDTO
     */
    public void updateEntity(User user, UpdateUserDTO updateDTO) {
        if (user == null) {
            logger.error("Cannot update null User entity");
            throw new IllegalArgumentException("User entity cannot be null");
        }

        if (updateDTO == null) {
            logger.warn("UpdateUserDTO is null, no changes applied to User {}", user.getId());
            return;
        }

        logger.debug("Updating User {} with DTO data", user.getId());

        boolean hasChanges = false;

        if (updateDTO.getName() != null && !updateDTO.getName().equals(user.getName())) {
            logger.debug("Updating name: '{}' -> '{}'", user.getName(), updateDTO.getName());
            user.setName(updateDTO.getName());
            hasChanges = true;
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            logger.debug("Updating email: '{}' -> '{}'", user.getEmail(), updateDTO.getEmail());
            user.setEmail(updateDTO.getEmail());
            hasChanges = true;
        }

        if (hasChanges) {
            logger.info("User {} updated successfully", user.getId());
        } else {
            logger.debug("No changes detected for User {}", user.getId());
        }
    }
}
