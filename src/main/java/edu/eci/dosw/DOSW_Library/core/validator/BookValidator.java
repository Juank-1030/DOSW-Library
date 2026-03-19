package edu.eci.dosw.DOSW_Library.core.validator;


import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;

public class BookValidator {

    private BookValidator() {}

    
    public static void validate(BookDTO dto) {
        ValidationUtil.validateNotNull(dto, "BookDTO");
        ValidationUtil.validateNotEmpty(dto.getId(), "Book ID");
        ValidationUtil.validateNotEmpty(dto.getTitle(), "Book Title");
        ValidationUtil.validateNotEmpty(dto.getAuthor(), "Book Author");
        if (dto.getCopies() <= 0) {
            throw new IllegalArgumentException("Number of copies must be greater than 0");
        }
    }
}
