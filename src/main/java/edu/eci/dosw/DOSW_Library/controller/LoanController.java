package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.CreateLoanDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.LoanSummaryDTO;
import edu.eci.dosw.DOSW_Library.persistence.relational.mapper.LoanPersistenceMapper;
import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de préstamos de libros.
 * 
 * <p>
 * <b>Endpoints disponibles:</b>
 * </p>
 * <ul>
 * <li>POST /api/loans - Crear préstamo</li>
 * <li>GET /api/loans - Listar todos los préstamos</li>
 * <li>GET /api/loans/{id} - Obtener préstamo por ID</li>
 * <li>PUT /api/loans/{id}/return - Devolver libro</li>
 * <li>GET /api/loans/user/{userId} - Préstamos de un usuario</li>
 * <li>GET /api/loans/user/{userId}/active - Préstamos activos de un
 * usuario</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans", description = "API para gestión de préstamos de libros")
public class LoanController {

        private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

        private final LoanService loanService;
        private final LoanPersistenceMapper loanMapper;

        public LoanController(LoanService loanService, LoanPersistenceMapper loanMapper) {
                this.loanService = loanService;
                this.loanMapper = loanMapper;
                logger.info("LoanController initialized");
        }

        // ============================================
        // POST - CREAR PRÉSTAMO
        // ============================================

        /**
         * Crea un nuevo préstamo de libro a usuario.
         * 
         * <p>
         * <b>Endpoint:</b> POST /api/loans
         * </p>
         * 
         * <p>
         * <b>Validaciones aplicadas:</b>
         * </p>
         * <ul>
         * <li>Usuario existe</li>
         * <li>Libro existe</li>
         * <li>Libro tiene copias disponibles</li>
         * <li>Usuario no ha alcanzado límite de préstamos (3)</li>
         * <li>Usuario no tiene préstamo activo del mismo libro</li>
         * </ul>
         * 
         * <p>
         * <b>Ejemplo de request:</b>
         * </p>
         * 
         * <pre>
         * POST /api/loans
         * {
         *   "bookId": "BOOK-001",
         *   "userId": "USR-001"
         * }
         * </pre>
         * 
         * <p>
         * <b>Ejemplo de response exitosa:</b>
         * </p>
         * 
         * <pre>
         * 201 CREATED
         * {
         *   "id": "LOAN-ABC123",
         *   "book": {
         *     "id": "BOOK-001",
         *     "title": "Clean Code",
         *     "author": "Robert C. Martin",
         *     "copies": 4,
         *     "available": true
         *   },
         *   "user": {
         *     "id": "USR-001",
         *     "name": "John Doe",
         *     "email": "john@example.com"
         *   },
         *   "loanDate": "2024-01-15",
         *   "status": "ACTIVE",
         *   "returnDate": null
         * }
         * </pre>
         * 
         * @param createDTO DTO con bookId y userId
         * @return ResponseEntity con LoanDTO completo y código 201 CREATED
         * @throws UserNotFoundException      Si el usuario no existe
         * @throws BookNotAvailableException  Si el libro no está disponible
         * @throws LoanLimitExceededException Si el usuario alcanzó el límite
         */
        @PostMapping
        @PreAuthorize("hasRole('USER') or hasRole('LIBRARIAN')")
        @Operation(summary = "Crear préstamo de libro", description = "Registra un nuevo préstamo validando disponibilidad y límites del usuario")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Préstamo creado exitosamente", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "403", description = "Usuario ha alcanzado el límite de préstamos (3)"),
                        @ApiResponse(responseCode = "404", description = "Usuario o libro no encontrado"),
                        @ApiResponse(responseCode = "409", description = "Libro no disponible o usuario ya tiene préstamo del mismo libro")
        })
        public ResponseEntity<LoanDTO> createLoan(
                        @Valid @RequestBody CreateLoanDTO createDTO)
                        throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {

                logger.info("POST /api/loans - Creating loan | BookID: {} | UserID: {}",
                                createDTO.getBookId(),
                                createDTO.getUserId());
                logger.debug("Request body: {}", createDTO);

                // Service (toda la lógica de negocio)
                Loan createdLoan = loanService.createLoan(
                                createDTO.getBookId(),
                                createDTO.getUserId());

                // Entity → DTO
                LoanDTO responseDTO = loanMapper.toDTO(createdLoan);

                logger.info("Loan created successfully: {} | Response: {}",
                                createdLoan.getId(),
                                HttpStatus.CREATED);

                return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }

        // ============================================
        // GET - OBTENER TODOS LOS PRÉSTAMOS
        // ============================================

        /**
         * Obtiene la lista de todos los préstamos (activos y devueltos).
         * 
         * <p>
         * <b>Query Parameters opcionales:</b>
         * </p>
         * <ul>
         * <li>?summary=true - Retorna versión resumida (LoanSummaryDTO)</li>
         * </ul>
         * 
         * @param summary Si es true, retorna versión resumida
         * @return ResponseEntity con lista de préstamos
         */
        @GetMapping
        @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(summary = "Listar todos los préstamos", description = "Obtiene la lista completa de préstamos registrados en el sistema")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida exitosamente"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header")
        })
        public ResponseEntity<?> getAllLoans(
                        @Parameter(description = "Retornar versión resumida", example = "false") @RequestParam(required = false, defaultValue = "false") boolean summary) {

                logger.info("GET /api/loans - Retrieving all loans | Summary: {}", summary);

                List<Loan> loans = loanService.getAllLoans();

                if (summary) {
                        List<LoanSummaryDTO> responseDTOs = loanMapper.toSummaryDTOList(loans);
                        logger.info("Retrieved {} loans (summary) | Response: {}",
                                        responseDTOs.size(),
                                        HttpStatus.OK);
                        return ResponseEntity.ok(responseDTOs);
                } else {
                        List<LoanDTO> responseDTOs = loanMapper.toDTOList(loans);
                        logger.info("Retrieved {} loans (full) | Response: {}",
                                        responseDTOs.size(),
                                        HttpStatus.OK);
                        return ResponseEntity.ok(responseDTOs);
                }
        }

        // ============================================
        // GET - OBTENER PRÉSTAMO POR ID
        // ============================================

        @GetMapping("/{id}")
        @PreAuthorize("hasRole('LIBRARIAN') or @userSecurityService.isLoanOwner(#id, authentication)")
        @Operation(summary = "Obtener préstamo por ID", description = "Busca y retorna un préstamo específico por su identificador")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Préstamo encontrado exitosamente", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
        })
        public ResponseEntity<LoanDTO> getLoanById(
                        @Parameter(description = "ID del préstamo", example = "LOAN-ABC123") @PathVariable String id) {

                logger.info("GET /api/loans/{} - Retrieving loan", id);

                Loan loan = loanService.getLoanById(id);
                LoanDTO responseDTO = loanMapper.toDTO(loan);

                logger.info("Loan {} found | Status: {} | Response: {}",
                                id,
                                loan.getStatus(),
                                HttpStatus.OK);

                return ResponseEntity.ok(responseDTO);
        }

        // ============================================
        // PUT - DEVOLVER LIBRO
        // ============================================

        /**
         * Procesa la devolución de un libro prestado.
         * 
         * <p>
         * <b>Endpoint:</b> PUT /api/loans/{id}/return
         * </p>
         * 
         * <p>
         * <b>Acciones realizadas:</b>
         * </p>
         * <ul>
         * <li>Cambia estado de ACTIVE a RETURNED</li>
         * <li>Registra fecha de devolución</li>
         * <li>Devuelve copia al inventario (+1)</li>
         * </ul>
         * 
         * <p>
         * <b>Ejemplo de response:</b>
         * </p>
         * 
         * <pre>
         * 200 OK
         * {
         *   "id": "LOAN-ABC123",
         *   "book": {...},
         *   "user": {...},
         *   "loanDate": "2024-01-15",
         *   "status": "RETURNED",
         *   "returnDate": "2024-01-30"
         * }
         * </pre>
         * 
         * @param id ID del préstamo a devolver
         * @return ResponseEntity con LoanDTO actualizado
         */
        @PutMapping("/{id}/return")
        @PreAuthorize("hasRole('LIBRARIAN') or @userSecurityService.isLoanOwner(#id, authentication)")
        @Operation(summary = "Devolver libro prestado", description = "Marca un préstamo como devuelto y actualiza el inventario")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Libro devuelto exitosamente", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
                        @ApiResponse(responseCode = "409", description = "Préstamo ya fue devuelto anteriormente")
        })
        public ResponseEntity<LoanDTO> returnLoan(
                        @Parameter(description = "ID del préstamo", example = "LOAN-ABC123") @PathVariable String id) {

                logger.info("PUT /api/loans/{}/return - Processing return", id);

                Loan returnedLoan = loanService.returnLoan(id);
                LoanDTO responseDTO = loanMapper.toDTO(returnedLoan);

                logger.info("Loan {} returned successfully | ReturnDate: {} | Response: {}",
                                id,
                                returnedLoan.getReturnDate(),
                                HttpStatus.OK);

                return ResponseEntity.ok(responseDTO);
        }

        // ============================================
        // GET - PRÉSTAMOS DE UN USUARIO
        // ============================================

        @GetMapping("/user/{userId}")
        @PreAuthorize("@userSecurityService.canAccessUserLoans(#userId, authentication)")
        @Operation(summary = "Obtener préstamos de un usuario", description = "Lista todos los préstamos (activos y devueltos) de un usuario específico")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida exitosamente"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        public ResponseEntity<List<LoanDTO>> getLoansByUser(
                        @Parameter(description = "ID del usuario", example = "USR-001") @PathVariable String userId) {

                logger.info("GET /api/loans/user/{} - Retrieving user loans", userId);

                List<Loan> loans = loanService.getLoansByUser(userId);
                List<LoanDTO> responseDTOs = loanMapper.toDTOList(loans);

                logger.info("Retrieved {} loans for user {} | Response: {}",
                                responseDTOs.size(),
                                userId,
                                HttpStatus.OK);

                return ResponseEntity.ok(responseDTOs);
        }

        // ============================================
        // GET - PRÉSTAMOS ACTIVOS DE UN USUARIO
        // ============================================

        @GetMapping("/user/{userId}/active")
        @PreAuthorize("@userSecurityService.canAccessUserLoans(#userId, authentication)")
        @Operation(summary = "Obtener préstamos activos de un usuario", description = "Lista solo los préstamos activos (no devueltos) de un usuario")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de préstamos activos obtenida"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header")
        })
        public ResponseEntity<List<LoanDTO>> getActiveLoans(
                        @Parameter(description = "ID del usuario", example = "USR-001") @PathVariable String userId) {

                logger.info("GET /api/loans/user/{}/active - Retrieving active loans", userId);

                List<Loan> activeLoans = loanService.getActiveLoans(userId);
                List<LoanDTO> responseDTOs = loanMapper.toDTOList(activeLoans);

                logger.info("Retrieved {} active loans for user {} | Response: {}",
                                responseDTOs.size(),
                                userId,
                                HttpStatus.OK);

                return ResponseEntity.ok(responseDTOs);
        }
}