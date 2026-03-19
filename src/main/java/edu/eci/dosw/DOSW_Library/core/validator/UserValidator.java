package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;

public class UserValidator {

    private UserValidator() {
    }

    public static void validate(UserDTO dto) {
        ValidationUtil.validateNotNull(dto, "UserDTO");
        ValidationUtil.validateNotEmpty(dto.getId(), "User ID");
        ValidationUtil.validateNotEmpty(dto.getName(), "User Name");
    }
}
