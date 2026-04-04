package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.CreateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UpdateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades User y DTOs relacionados.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Convertir User (Entity) → UserDTO (Response)</li>
 * <li>Convertir CreateUserDTO (Request) → User (Entity)</li>
 * <li>Aplicar actualizaciones desde UpdateUserDTO</li>
 * <li>Logging de conversiones para auditoría</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@Component
public class UserMapper {

    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    // ============================================
    // ENTITY → DTO (Para respuestas)
    // ============================================

    /**
     * Convierte una entidad User a UserDTO para respuestas HTTP.
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>GET /api/users/{id}</li>
     * <li>GET /api/users</li>
     * <li>POST /api/users - Respuesta después de crear</li>
     * <li>Dentro de LoanDTO (información del usuario)</li>
     * </ul>
     * 
     * @param user Entidad User a convertir
     * @return UserDTO con los datos del usuario, o null si user es null
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

    /**
     * Convierte una lista de entidades User a lista de UserDTOs.
     * 
     * @param users Lista de entidades User
     * @return Lista de UserDTOs, o lista vacía si users es null
     */
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
    // DTO → ENTITY (Para crear nuevos usuarios)
    // ============================================

    /**
     * Convierte CreateUserDTO a entidad User para persistencia.
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>POST /api/users - Registrar nuevo usuario</li>
     * </ul>
     * 
     * @param createDTO DTO con datos para crear el usuario
     * @return Nueva entidad User lista para persistir, o null si createDTO es null
     */
    public User toEntity(CreateUserDTO createDTO) {
        if (createDTO == null) {
            logger.warn("Attempted to convert null CreateUserDTO to User");
            return null;
        }

        logger.debug("Converting CreateUserDTO to User | ID: {} | Name: '{}'",
                createDTO.getId(),
                createDTO.getName());

        User user = new User(createDTO.getId(), createDTO.getName());
        user.setEmail(createDTO.getEmail());

        logger.info("User entity created from DTO | ID: {} | Email: {}",
                user.getId(),
                user.getEmail());

        return user;
    }

    // ============================================
    // ACTUALIZACIÓN DE USUARIO
    // ============================================

    /**
     * Actualiza una entidad User con datos de UpdateUserDTO.
     * 
     * <p>
     * <b>Campos actualizables:</b>
     * </p>
     * <ul>
     * <li>name - Nombre del usuario</li>
     * <li>email - Email del usuario</li>
     * </ul>
     * 
     * <p>
     * <b>Campos NO actualizables:</b>
     * </p>
     * <ul>
     * <li>id - El identificador es inmutable</li>
     * </ul>
     * 
     * <p>
     * <b>Estrategia:</b> Solo actualiza campos no nulos del DTO
     * </p>
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Updating User USR-001 with DTO data"
     * DEBUG - "Updating name: 'John Doe' -> 'Jane Doe'"
     * DEBUG - "Updating email: 'john@example.com' -> 'jane@example.com'"
     * INFO  - "User USR-001 updated successfully"
     * </pre>
     * 
     * @param user      Entidad existente a actualizar
     * @param updateDTO DTO con datos a actualizar (campos opcionales)
     * @throws IllegalArgumentException Si user es null
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

        // Actualizar nombre si está presente
        if (updateDTO.getName() != null && !updateDTO.getName().equals(user.getName())) {
            logger.debug("Updating name: '{}' -> '{}'", user.getName(), updateDTO.getName());
            user.setName(updateDTO.getName());
            hasChanges = true;
        }

        // Actualizar email si está presente
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