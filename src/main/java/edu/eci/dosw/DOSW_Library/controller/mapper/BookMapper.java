package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;

public class BookMapper {

    private BookMapper() {
    }

    public static edu.eci.dosw.DOSW_Library.core.model.Book toEntity(BookDTO dto) {
        ValidationUtil.validateNotNull(dto, "BookDTO");
        ValidationUtil.validateNotEmpty(dto.getId(), "Book ID");
        ValidationUtil.validateNotEmpty(dto.getTitle(), "Book Title");
        ValidationUtil.validateNotEmpty(dto.getAuthor(), "Book Author");

        return new edu.eci.dosw.DOSW_Library.core.model.Book(dto.getId(), dto.getTitle(), dto.getAuthor());
    }

    public static BookDTO toDTO(edu.eci.dosw.DOSW_Library.core.model.Book book, int copies) {
        ValidationUtil.validateNotNull(book, "Book");
        ValidationUtil.validateNotEmpty(book.getId(), "Book ID");
        ValidationUtil.validateNotEmpty(book.getTitle(), "Book Title");
        ValidationUtil.validateNotEmpty(book.getAuthor(), "Book Author");
        ValidationUtil.validateNonNegative(copies, "Copies");

        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), copies);
    }
}