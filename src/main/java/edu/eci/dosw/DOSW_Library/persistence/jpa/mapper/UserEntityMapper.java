package edu.eci.dosw.DOSW_Library.persistence.jpa.mapper;

import edu.eci.dosw.DOSW_Library.core.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte entre entidad JPA User y modelo de dominio User.
 *
 * En este proyecto, se utilizan los mismos modelos como entidades JPA,
 * por lo que el mapping es directo (identity mapping).
 *
 * En proyectos más grandes, aquí se haría la conversión:
 * UserEntity (JPA) ↔ User (Domain)
 */
@Component
public class UserEntityMapper {

    /**
     * Convierte modelo de dominio a entidad JPA.
     * (Actualmente: identity mapping)
     *
     * @param user modelo de dominio
     * @return entidad JPA
     */
    public User toEntity(User user) {
        // En esta arquitectura, el modelo es a la vez entidad JPA
        return user;
    }

    /**
     * Convierte entidad JPA a modelo de dominio.
     * (Actualmente: identity mapping)
     *
     * @param entity entidad JPA
     * @return modelo de dominio
     */
    public User toDomain(User entity) {
        // En esta arquitectura, el modelo es a la vez entidad JPA
        return entity;
    }
}
