package edu.eci.dosw.DOSW_Library.persistence.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.LoanSummaryDTO;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import edu.eci.dosw.DOSW_Library.persistence.entity.LoanEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper consolidado para todas las transformaciones de Loan.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>✅ LoanEntity ↔ Loan (persistencia ↔ dominio)</li>
 * <li>✅ Loan ↔ LoanDTO (dominio ↔ API Response completo)</li>
 * <li>✅ Loan ↔ LoanSummaryDTO (dominio ↔ API Response resumido)</li>
 * <li>✅ Maneja relaciones anidadas (Book, User)</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 3.0 - Mapper consolidado (sin controller/mapper)
 */
@Component
public class LoanPersistenceMapper {

    private static final Logger logger = LoggerFactory.getLogger(LoanPersistenceMapper.class);

    private final BookPersistenceMapper bookMapper;
    private final UserPersistenceMapper userMapper;

    public LoanPersistenceMapper(BookPersistenceMapper bookMapper, UserPersistenceMapper userMapper) {
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
        logger.debug("LoanPersistenceMapper initialized with BookPersistenceMapper and UserPersistenceMapper");
    }

    // ============================================
    // PERSISTENCE LAYER (LoanEntity ↔ Loan)
    // ============================================

    public Loan toDomain(LoanEntity entity) {
        if (entity == null) {
            return null;
        }

        return Loan.builder()
                .id(entity.getId())
                .user(userMapper.toDomain(entity.getUser()))
                .book(bookMapper.toDomain(entity.getBook()))
                .loanDate(entity.getLoanDate())
                .dueDate(entity.getDueDate())
                .returnDate(entity.getReturnDate())
                .status(LoanStatus.valueOf(entity.getStatus().name()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public LoanEntity toEntity(Loan loan) {
        if (loan == null) {
            return null;
        }

        return LoanEntity.builder()
                .id(loan.getId())
                .user(userMapper.toEntity(loan.getUser()))
                .book(bookMapper.toEntity(loan.getBook()))
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(edu.eci.dosw.DOSW_Library.persistence.entity.LoanStatus.valueOf(loan.getStatus().name()))
                .build();
    }

    // ============================================
    // API LAYER (Loan ↔ LoanDTO & LoanSummaryDTO)
    // ============================================

    /**
     * Loan → LoanDTO completo (con objetos anidados completos)
     */
    public LoanDTO toDTO(Loan loan) {
        if (loan == null) {
            logger.warn("Attempted to convert null Loan to LoanDTO");
            return null;
        }

        logger.debug("Converting Loan to DTO | ID: {} | Status: {}",
                loan.getId(),
                loan.getStatus());

        try {
            LoanDTO dto = LoanDTO.builder()
                    .id(loan.getId())
                    .book(bookMapper.toDTO(loan.getBook()))
                    .user(userMapper.toDTO(loan.getUser()))
                    .loanDate(loan.getLoanDate())
                    .dueDate(loan.getDueDate())
                    .status(loan.getStatus().name())
                    .returnDate(loan.getReturnDate())
                    .build();

            logger.trace("Loan converted successfully: LoanID={}, BookID={}, UserID={}",
                    dto.getId(),
                    dto.getBook() != null ? dto.getBook().getId() : "null",
                    dto.getUser() != null ? dto.getUser().getId() : "null");

            return dto;

        } catch (Exception e) {
            logger.error("Error converting Loan {} to DTO: {}",
                    loan.getId(),
                    e.getMessage(),
                    e);
            throw new RuntimeException("Failed to convert Loan to DTO: " + e.getMessage(), e);
        }
    }

    public List<LoanDTO> toDTOList(List<Loan> loans) {
        if (loans == null) {
            logger.warn("Attempted to convert null Loan list to DTO list");
            return List.of();
        }

        logger.debug("Converting {} loans to DTO list", loans.size());

        List<LoanDTO> dtoList = loans.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        logger.debug("Converted {} loans to DTOs successfully", dtoList.size());

        return dtoList;
    }

    /**
     * Loan → LoanSummaryDTO (resumen sin objetos completos)
     */
    public LoanSummaryDTO toSummaryDTO(Loan loan) {
        if (loan == null) {
            logger.warn("Attempted to convert null Loan to LoanSummaryDTO");
            return null;
        }

        logger.debug("Converting Loan to SummaryDTO | ID: {}", loan.getId());

        try {
            LoanSummaryDTO dto = LoanSummaryDTO.builder()
                    .id(loan.getId())
                    .bookId(loan.getBook().getId())
                    .bookTitle(loan.getBook().getTitle())
                    .userId(loan.getUser().getId())
                    .userName(loan.getUser().getName())
                    .loanDate(loan.getLoanDate().toLocalDate())
                    .status(loan.getStatus().name())
                    .build();

            logger.trace("Loan converted to summary successfully: {}", dto);

            return dto;

        } catch (Exception e) {
            logger.error("Error converting Loan {} to SummaryDTO: {}",
                    loan.getId(),
                    e.getMessage(),
                    e);
            throw new RuntimeException("Failed to convert Loan to SummaryDTO: " + e.getMessage(), e);
        }
    }

    public List<LoanSummaryDTO> toSummaryDTOList(List<Loan> loans) {
        if (loans == null) {
            logger.warn("Attempted to convert null Loan list to SummaryDTO list");
            return List.of();
        }

        logger.debug("Converting {} loans to SummaryDTO list", loans.size());

        List<LoanSummaryDTO> dtoList = loans.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());

        logger.debug("Converted {} loans to SummaryDTOs successfully", dtoList.size());

        return dtoList;
    }
}
