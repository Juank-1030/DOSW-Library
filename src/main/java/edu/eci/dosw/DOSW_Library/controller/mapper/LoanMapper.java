package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.LoanSummaryDTO;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Loan y DTOs relacionados.
 * 
 * <p>
 * <b>Características especiales:</b>
 * </p>
 * <ul>
 * <li>Maneja relaciones con Book y User (objetos anidados)</li>
 * <li>Convierte enum LoanStatus a String para JSON</li>
 * <li>Evita problemas de lazy loading en relaciones JPA</li>
 * <li>Proporciona versión resumida (LoanSummaryDTO) para listados</li>
 * </ul>
 * 
 * <p>
 * <b>Dependencias:</b>
 * </p>
 * <ul>
 * <li>BookMapper - Para convertir Book → BookDTO</li>
 * <li>UserMapper - Para convertir User → UserDTO</li>
 * </ul>
 * 
 * <p>
 * <b>⚠️ IMPORTANTE - Lazy Loading:</b>
 * </p>
 * <p>
 * Este mapper asume que las relaciones (book, user) están cargadas
 * antes de la conversión. Si usas FetchType.LAZY, asegúrate de hacer
 * JOIN FETCH en tus consultas o usar @EntityGraph.
 * </p>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@Component
public class LoanMapper {

    private static final Logger logger = LoggerFactory.getLogger(LoanMapper.class);

    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    /**
     * Constructor con inyección de dependencias.
     * 
     * <p>
     * Spring inyecta automáticamente BookMapper y UserMapper.
     * </p>
     * 
     * @param bookMapper Mapper de libros
     * @param userMapper Mapper de usuarios
     */
    public LoanMapper(BookMapper bookMapper, UserMapper userMapper) {
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
        logger.debug("LoanMapper initialized with BookMapper and UserMapper");
    }

    // ============================================
    // ENTITY → DTO (Respuesta completa)
    // ============================================

    /**
     * Convierte una entidad Loan a LoanDTO completo para respuestas HTTP.
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>GET /api/loans/{id} - Obtener préstamo específico</li>
     * <li>POST /api/loans - Respuesta después de crear préstamo</li>
     * <li>PUT /api/loans/{id}/return - Respuesta después de devolver</li>
     * </ul>
     * 
     * <p>
     * <b>Estructura del DTO:</b>
     * </p>
     * <ul>
     * <li>Incluye BookDTO completo (no solo ID)</li>
     * <li>Incluye UserDTO completo (no solo ID)</li>
     * <li>Convierte LoanStatus (enum) a String</li>
     * <li>Formatea fechas como ISO-8601 (yyyy-MM-dd)</li>
     * </ul>
     * 
     * <p>
     * <b>Manejo de errores:</b>
     * </p>
     * <ul>
     * <li>Si loan.getBook() lanza LazyInitializationException →
     * RuntimeException</li>
     * <li>Si loan.getUser() lanza LazyInitializationException →
     * RuntimeException</li>
     * </ul>
     * 
     * <p>
     * <b>Logging:</b>
     * </p>
     * 
     * <pre>
     * DEBUG - "Converting Loan to DTO | ID: LOAN-001 | Status: ACTIVE"
     * TRACE - "Loan converted successfully: LoanID=LOAN-001, BookID=BOOK-001, UserID=USR-001"
     * ERROR - "Error converting Loan LOAN-001 to DTO: LazyInitializationException..."
     * </pre>
     * 
     * @param loan Entidad Loan a convertir
     * @return LoanDTO completo con información de libro y usuario, o null si loan
     *         es null
     * @throws RuntimeException Si hay problemas de lazy loading
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
                    .book(bookMapper.toDTO(loan.getBook())) // ⚠️ Puede lanzar LazyInitializationException
                    .user(userMapper.toDTO(loan.getUser())) // ⚠️ Puede lanzar LazyInitializationException
                    .loanDate(loan.getLoanDate())
                    .status(loan.getStatus().name()) // Enum → String
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

    /**
     * Convierte una lista de entidades Loan a lista de LoanDTOs.
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>GET /api/loans - Listar todos los préstamos</li>
     * <li>GET /api/users/{id}/loans - Préstamos de un usuario</li>
     * </ul>
     * 
     * @param loans Lista de entidades Loan
     * @return Lista de LoanDTOs, o lista vacía si loans es null
     */
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

    // ============================================
    // ENTITY → SUMMARY DTO (Versión ligera)
    // ============================================

    /**
     * Convierte una entidad Loan a LoanSummaryDTO (versión resumida).
     * 
     * <p>
     * <b>Diferencias con toDTO():</b>
     * </p>
     * <ul>
     * <li>NO incluye objetos completos (solo IDs y nombres)</li>
     * <li>Más eficiente para listados grandes</li>
     * <li>Reduce tráfico de red</li>
     * </ul>
     * 
     * <p>
     * <b>Usado en:</b>
     * </p>
     * <ul>
     * <li>GET /api/loans?summary=true</li>
     * <li>Dashboards y reportes</li>
     * </ul>
     * 
     * @param loan Entidad Loan a convertir
     * @return LoanSummaryDTO con información resumida, o null si loan es null
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
                    .loanDate(loan.getLoanDate())
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

    /**
     * Convierte una lista de Loans a LoanSummaryDTOs.
     * 
     * @param loans Lista de entidades Loan
     * @return Lista de LoanSummaryDTOs, o lista vacía si loans es null
     */
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