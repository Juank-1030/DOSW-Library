package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;

public class LoanValidator {

    private LoanValidator() {
    }

    public static void validate(LoanDTO dto) {
        ValidationUtil.validateNotNull(dto, "LoanDTO");
        ValidationUtil.validateNotEmpty(dto.getBookId(), "Book ID");
        ValidationUtil.validateNotEmpty(dto.getUserId(), "User ID");
    }
}