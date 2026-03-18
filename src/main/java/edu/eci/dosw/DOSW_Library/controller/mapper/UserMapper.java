package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.model.User;

public class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(UserDTO dto) {
        return new User(dto.getId(), dto.getName());
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName());
    }
}