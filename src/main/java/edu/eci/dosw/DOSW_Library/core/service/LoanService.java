package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import edu.eci.dosw.DOSW_Library.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de préstamos de libros.
 * 
 * <p>
 * <b>Responsabilidades (LÓGICA DE NEGOCIO COMPLEJA):</b>
 * </p>
 * <ul>
 * <li>✅ Crear préstamos validando todas las reglas de negocio</li>
 * <li>✅ Validar límite de préstamos activos por usuario (MAX 3)</li>
 * <li>✅ Validar disponibilidad de libros</li>
 * <li>✅ Actualizar inventario al prestar/devolver</li>
 * <li>✅ Gestionar estado de préstamos (ACTIVE → RETURNED)</li>
 * <li>✅ Prevenir préstamos duplicados del mismo libro al mismo usuario</li>
 * </ul>
 * 
 * <p>
 * <b>Reglas de negocio implementadas:</b>
 * </p>
 * <table border="1">
 * <tr>
 * <th>Regla</th>
 * <th>Validación</th>
 * </tr>
 * <tr>
 * <td>Límite de préstamos</td>
 * <td>Máximo 3 préstamos ACTIVOS simultáneos</td>
 * </tr>
 * <tr>
 * <td>Disponibilidad</td>
 * <td>El libro debe tener copies > 0</td>
 * </tr>
 * <tr>
 * <td>Usuario válido</td>
 * <td>El usuario debe existir en el sistema</td>
 * </tr>
 * <tr>
 * <td>Préstamos duplicados</td>
 * <td>No puede pedir el mismo libro dos veces activamente</td>
 * </tr>
 * <tr>
 * <td>Devolución</td>
 * <td>Solo préstamos ACTIVE pueden devolverse</td>
 * </tr>
 * </table>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@Service
public class LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);

    /**
     * Límite máximo de préstamos activos simultáneos por usuario.
     * Según las diapositivas del ejercicio.
     */
    private static final int MAX_ACTIVE_LOANS = 3;

    // Simulación de base de datos
    private final List<Loan> loanRepository = new ArrayList<>();

    // Dependencias de otros servicios
    private final BookService bookService;
    private final UserService userService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param bookService Servicio de libros
     * @param userService Servicio de usuarios
     */
    public LoanService(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
        logger.info("LoanService initialized with BookService and UserService");
    }

    // ============================================
    // CREACIÓN DE PRÉSTAMOS (LÓGICA COMPLEJA)
    // ============================================

    /**
     * Crea un nuevo préstamo de libro a usuario.
     * 
     * <p>
     * <b>Flujo de validaciones:</b>
     * </p>
     * <ol>
     * <li>Validar que el usuario existe</li>
     * <li>Validar que el libro existe</li>
     * <li>Validar límite de préstamos del usuario</li>
     * <li>Validar que no tiene préstamo activo del mismo libro</li>
     * <li>Validar disponibilidad del libro</li>
     * <li>Crear préstamo</li>
     * <li>Actualizar inventario (restar 1 copia)</li>
     * </ol>
     * 
     * <p>
     * <b>Logging detallado:</b>
     * </p>
     * 
     * <pre>
     * INFO  - "Creating loan | BookID: BOOK-001 | UserID: USR-001"
     * DEBUG - "User validated: John Doe"
     * DEBUG - "Book validated: Clean Code"
     * DEBUG - "User USR-001 has 1 active loans (max: 3)"
     * DEBUG - "Book BOOK-001 is available. Copies: 5"
     * INFO  - "Loan created successfully | LoanID: LOAN-001 | BookID: BOOK-001 | UserID: USR-001"
     * </pre>
     * 
     * @param bookId ID del libro a prestar
     * @param userId ID del usuario que solicita el préstamo
     * @return El préstamo creado con estado ACTIVE
     * @throws UserNotFoundException      Si el usuario no existe
     * @throws ResourceNotFoundException  Si el libro no existe
     * @throws LoanLimitExceededException Si el usuario ya tiene 3 préstamos activos
     * @throws BookNotAvailableException  Si el libro no tiene copias disponibles
     * @throws IllegalStateException      Si el usuario ya tiene préstamo activo del
     *                                    mismo libro
     */
    public Loan createLoan(String bookId, String userId)
            throws BookNotAvailableException, UserNotFoundException, LoanLimitExceededException {

        logger.info("Creating loan | BookID: {} | UserID: {}", bookId, userId);

        // PASO 1: Validar usuario existe
        User user = userService.getUserById(userId);
        logger.debug("User validated: {}", user.getName());

        // PASO 2: Validar libro existe
        Book book = bookService.getBookById(bookId);
        logger.debug("Book validated: {}", book.getTitle());

        // PASO 3: Validar límite de préstamos
        validateLoanLimit(userId);

        // PASO 4: Validar préstamo duplicado
        validateNoDuplicateLoan(bookId, userId);

        // PASO 5: Validar disponibilidad
        if (!bookService.isBookAvailable(bookId)) {
            logger.warn("Book {} not available for loan | User: {}", bookId, userId);
            throw BookNotAvailableException.noCopiesAvailable(bookId);
        }

        // PASO 6: Crear préstamo
        String loanId = generateLoanId();

        Loan loan = new Loan(loanId, book, user, LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);

        loanRepository.add(loan);

        // PASO 7: Actualizar inventario (restar 1 copia)
        bookService.updateAvailability(bookId, -1);

        logger.info("Loan created successfully | LoanID: {} | BookID: {} | UserID: {} | Date: {}",
                loan.getId(),
                bookId,
                userId,
                loan.getLoanDate());

        return loan;
    }

    // ============================================
    // VALIDACIONES DE NEGOCIO
    // ============================================

    /**
     * Valida que el usuario no exceda el límite de préstamos activos.
     * 
     * <p>
     * <b>Regla de negocio:</b> Máximo 3 préstamos ACTIVOS simultáneos
     * </p>
     * 
     * @param userId ID del usuario
     * @throws LoanLimitExceededException Si ya tiene 3 o más préstamos activos
     */
    private void validateLoanLimit(String userId) throws LoanLimitExceededException {
        long activeLoans = loanRepository.stream()
                .filter(loan -> loan.getUser().getId().equals(userId))
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .count();

        logger.debug("User {} has {} active loans (max: {})",
                userId, activeLoans, MAX_ACTIVE_LOANS);

        if (activeLoans >= MAX_ACTIVE_LOANS) {
            logger.warn("Loan limit exceeded for user: {} ({}/{})",
                    userId, activeLoans, MAX_ACTIVE_LOANS);

            throw LoanLimitExceededException.withLimit(
                    userId,
                    (int) activeLoans,
                    MAX_ACTIVE_LOANS);
        }
    }

    /**
     * Valida que el usuario no tenga préstamo activo del mismo libro.
     * 
     * <p>
     * <b>Regla de negocio:</b> No puede pedir el mismo libro dos veces
     * simultáneamente
     * </p>
     * 
     * @param bookId ID del libro
     * @param userId ID del usuario
     * @throws IllegalStateException Si ya tiene préstamo activo del mismo libro
     */
    private void validateNoDuplicateLoan(String bookId, String userId) {
        boolean hasDuplicate = loanRepository.stream()
                .filter(loan -> loan.getUser().getId().equals(userId))
                .filter(loan -> loan.getBook().getId().equals(bookId))
                .anyMatch(loan -> loan.getStatus() == LoanStatus.ACTIVE);

        if (hasDuplicate) {
            logger.warn("User {} already has active loan for book {}", userId, bookId);
            throw new IllegalStateException(
                    String.format("User %s already has an active loan for book %s", userId, bookId));
        }

        logger.debug("No duplicate loan found for User {} and Book {}", userId, bookId);
    }

    // ============================================
    // DEVOLUCIÓN DE LIBROS
    // ============================================

    /**
     * Procesa la devolución de un libro prestado.
     * 
     * <p>
     * <b>Flujo:</b>
     * </p>
     * <ol>
     * <li>Buscar préstamo por ID</li>
     * <li>Validar que esté en estado ACTIVE</li>
     * <li>Cambiar estado a RETURNED</li>
     * <li>Registrar fecha de devolución</li>
     * <li>Devolver copia al inventario (+1)</li>
     * </ol>
     * 
     * @param loanId ID del préstamo a devolver
     * @return El préstamo actualizado con estado RETURNED
     * @throws ResourceNotFoundException Si el préstamo no existe
     * @throws IllegalStateException     Si el préstamo ya fue devuelto
     */
    public Loan returnLoan(String loanId) {
        logger.info("Processing return for loan: {}", loanId);

        Loan loan = loanRepository.stream()
                .filter(l -> l.getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Loan not found: {}", loanId);
                    return new ResourceNotFoundException("Loan", loanId);
                });

        // Validar que esté activo
        if (loan.getStatus() == LoanStatus.RETURNED) {
            logger.warn("Loan {} already returned on {}", loanId, loan.getReturnDate());
            throw new IllegalStateException("Loan " + loanId + " was already returned");
        }

        // Actualizar estado
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        // Devolver copia al inventario
        bookService.updateAvailability(loan.getBook().getId(), +1);

        logger.info("Loan returned successfully | LoanID: {} | BookID: {} | ReturnDate: {}",
                loanId,
                loan.getBook().getId(),
                loan.getReturnDate());

        return loan;
    }

    // ============================================
    // OPERACIONES DE CONSULTA
    // ============================================

    /**
     * Obtiene un préstamo por su ID.
     * 
     * @param loanId ID del préstamo
     * @return El préstamo encontrado
     * @throws ResourceNotFoundException Si no existe
     */
    public Loan getLoanById(String loanId) {
        logger.debug("Searching for loan with ID: {}", loanId);

        Loan loan = loanRepository.stream()
                .filter(l -> l.getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Loan not found: {}", loanId);
                    return new ResourceNotFoundException("Loan", loanId);
                });

        logger.info("Loan found: {} | Status: {}", loanId, loan.getStatus());
        return loan;
    }

    /**
     * Obtiene todos los préstamos del sistema.
     * 
     * @return Lista de todos los préstamos
     */
    public List<Loan> getAllLoans() {
        logger.debug("Retrieving all loans. Total: {}", loanRepository.size());
        return new ArrayList<>(loanRepository);
    }

    /**
     * Obtiene los préstamos activos de un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de préstamos activos del usuario
     */
    public List<Loan> getActiveLoans(String userId) {
        logger.debug("Getting active loans for user: {}", userId);

        List<Loan> activeLoans = loanRepository.stream()
                .filter(loan -> loan.getUser().getId().equals(userId))
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .collect(Collectors.toList());

        logger.debug("User {} has {} active loans", userId, activeLoans.size());
        return activeLoans;
    }

    /**
     * Obtiene todos los préstamos de un usuario (activos y devueltos).
     * 
     * @param userId ID del usuario
     * @return Lista de todos los préstamos del usuario
     */
    public List<Loan> getLoansByUser(String userId) {
        logger.debug("Getting all loans for user: {}", userId);

        List<Loan> userLoans = loanRepository.stream()
                .filter(loan -> loan.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        logger.debug("User {} has {} total loans", userId, userLoans.size());
        return userLoans;
    }

    /**
     * Obtiene todos los préstamos de un libro.
     * 
     * @param bookId ID del libro
     * @return Lista de préstamos del libro
     */
    public List<Loan> getLoansByBook(String bookId) {
        logger.debug("Getting all loans for book: {}", bookId);

        List<Loan> bookLoans = loanRepository.stream()
                .filter(loan -> loan.getBook().getId().equals(bookId))
                .collect(Collectors.toList());

        logger.debug("Book {} has {} total loans", bookId, bookLoans.size());
        return bookLoans;
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    /**
     * Genera un ID único para un préstamo.
     * 
     * @return ID generado (formato: LOAN-{UUID})
     */
    private String generateLoanId() {
        String id = "LOAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        logger.trace("Generated loan ID: {}", id);
        return id;
    }

    /**
     * Obtiene el total de préstamos registrados.
     * 
     * @return Cantidad total de préstamos
     */
    public int getTotalLoans() {
        int total = loanRepository.size();
        logger.debug("Total loans in system: {}", total);
        return total;
    }

    /**
     * Obtiene la cantidad de préstamos activos en el sistema.
     * 
     * @return Cantidad de préstamos activos
     */
    public long getActiveLoansCount() {
        long count = loanRepository.stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .count();

        logger.debug("Total active loans: {}", count);
        return count;
    }
}