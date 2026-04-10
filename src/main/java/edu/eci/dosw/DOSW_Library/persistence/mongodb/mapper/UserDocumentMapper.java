package edu.eci.dosw.DOSW_Library.persistence.mongodb.mapper;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.mongodb.document.UserDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte entre documento MongoDB UserDocument y modelo de dominio
 * User.
 * 
 * Realiza conversión bidireccional entre la representación NoSQL y el modelo
 * de dominio agnóstico de persistencia.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Component
public class UserDocumentMapper {

    /**
     * Convierte modelo de dominio User a documento MongoDB.
     *
     * @param user modelo de dominio
     * @return documento MongoDB
     */
    public UserDocument toDocument(User user) {
        if (user == null) {
            return null;
        }

        return UserDocument.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Convierte documento MongoDB a modelo de dominio User.
     *
     * @param document documento MongoDB
     * @return modelo de dominio
     */
    public User toDomain(UserDocument document) {
        if (document == null) {
            return null;
        }

        return User.builder()
                .id(document.getId())
                .name(document.getName())
                .email(document.getEmail())
                .username(document.getUsername())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
