package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.controller.dto.CreateLoanDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.LoanSummaryDTO;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LoanPersistenceMapper {

    @Autowired(required = false)
    private BookPersistenceMapper bookMapper;

    @Autowired(required = false)
    private UserPersistenceMapper userMapper;

    public Loan toDomain(LoanEntity entity) {
        if (entity == null)
            return null;
        return Loan.builder()
                .id(entity.getId())
                .book(null)
                .user(null)
                .loanDate(entity.getLoanDate())
                .dueDate(entity.getDueDate())
                .returnDate(entity.getReturnDate())
                .status(convertStatusFromEntity(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Loan toDomain(LoanEntity entity, BookEntity bookEntity, UserEntity userEntity) {
        if (entity == null)
            return null;
        Book book = bookMapper != null && bookEntity != null ? new Book() : null;
        User user = userMapper != null && userEntity != null ? new User() : null;
        return Loan.builder()
                .id(entity.getId())
                .book(book)
                .user(user)
                .loanDate(entity.getLoanDate())
                .dueDate(entity.getDueDate())
                .returnDate(entity.getReturnDate())
                .status(convertStatusFromEntity(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public LoanEntity toEntity(Loan domain) {
        if (domain == null)
            return null;
        return LoanEntity.builder()
                .id(domain.getId())
                .loanDate(domain.getLoanDate())
                .dueDate(domain.getDueDate())
                .returnDate(domain.getReturnDate())
                .status(convertStatusToEntity(domain.getStatus()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Loan toEntity(CreateLoanDTO createDTO) {
        if (createDTO == null)
            return null;
        LocalDateTime now = LocalDateTime.now();
        return Loan.builder()
                .loanDate(now)
                .dueDate(now.plusDays(14))
                .status(LoanStatus.ACTIVE)
                .build();
    }

    public LoanDTO toDTO(Loan domain) {
        if (domain == null)
            return null;
        return LoanDTO.builder()
                .id(domain.getId())
                .user(null)
                .book(null)
                .loanDate(domain.getLoanDate())
                .dueDate(domain.getDueDate())
                .returnDate(domain.getReturnDate())
                .status(domain.getStatus() != null ? domain.getStatus().toString() : null)
                .build();
    }

    public List<LoanDTO> toDTOList(List<Loan> loans) {
        if (loans == null)
            return List.of();
        return loans.stream().map(this::toDTO).toList();
    }

    public LoanSummaryDTO toSummaryDTO(Loan domain) {
        if (domain == null)
            return null;
        return LoanSummaryDTO.builder()
                .id(domain.getId())
                .userId(domain.getUser() != null ? domain.getUser().getId() : null)
                .userName(domain.getUser() != null ? domain.getUser().getName() : "UNKNOWN")
                .bookId(domain.getBook() != null ? domain.getBook().getId() : null)
                .bookTitle(domain.getBook() != null ? domain.getBook().getTitle() : "UNKNOWN")
                .loanDate(domain.getLoanDate() != null ? domain.getLoanDate().toLocalDate() : null)
                .status(domain.getStatus() != null ? domain.getStatus().toString() : null)
                .build();
    }

    public List<LoanSummaryDTO> toSummaryDTOList(List<Loan> loans) {
        if (loans == null)
            return List.of();
        return loans.stream().map(this::toSummaryDTO).toList();
    }

    public LoanDTO toDTO(Loan domain, User user, Book book) {
        if (domain == null)
            return null;
        return LoanDTO.builder()
                .id(domain.getId())
                .user(userMapper != null && user != null ? userMapper.toDTO(user) : null)
                .book(bookMapper != null && book != null ? bookMapper.toDTO(book) : null)
                .loanDate(domain.getLoanDate())
                .dueDate(domain.getDueDate())
                .returnDate(domain.getReturnDate())
                .status(domain.getStatus() != null ? domain.getStatus().toString() : null)
                .build();
    }

    public List<LoanDTO> toDTOList(List<Loan> loans, Map<String, User> users, Map<String, Book> books) {
        if (loans == null)
            return List.of();
        return loans.stream()
                .map(loan -> toDTO(loan,
                        users != null && loan.getUser() != null ? users.get(loan.getUser().getId()) : null,
                        books != null && loan.getBook() != null ? books.get(loan.getBook().getId()) : null))
                .toList();
    }

    public LoanSummaryDTO toSummaryDTO(Loan domain, User user, Book book) {
        if (domain == null)
            return null;
        return LoanSummaryDTO.builder()
                .id(domain.getId())
                .userId(domain.getUser() != null ? domain.getUser().getId() : null)
                .userName(user != null ? user.getName()
                        : (domain.getUser() != null ? domain.getUser().getName() : "UNKNOWN"))
                .bookId(domain.getBook() != null ? domain.getBook().getId() : null)
                .bookTitle(book != null ? book.getTitle()
                        : (domain.getBook() != null ? domain.getBook().getTitle() : "UNKNOWN"))
                .loanDate(domain.getLoanDate() != null ? domain.getLoanDate().toLocalDate() : null)
                .status(domain.getStatus() != null ? domain.getStatus().toString() : null)
                .build();
    }

    public List<LoanSummaryDTO> toSummaryDTOList(List<Loan> loans, Map<String, User> users, Map<String, Book> books) {
        if (loans == null)
            return List.of();
        return loans.stream()
                .map(loan -> toSummaryDTO(loan,
                        users != null && loan.getUser() != null ? users.get(loan.getUser().getId()) : null,
                        books != null && loan.getBook() != null ? books.get(loan.getBook().getId()) : null))
                .toList();
    }

    private LoanStatus convertStatusFromEntity(
            edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanStatus entityStatus) {
        if (entityStatus == null)
            return null;
        return switch (entityStatus) {
            case ACTIVE -> LoanStatus.ACTIVE;
            case RETURNED -> LoanStatus.RETURNED;
        };
    }

    private edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanStatus convertStatusToEntity(
            LoanStatus domainStatus) {
        if (domainStatus == null)
            return null;
        return switch (domainStatus) {
            case ACTIVE -> edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanStatus.ACTIVE;
            case RETURNED -> edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanStatus.RETURNED;
        };
    }
}
