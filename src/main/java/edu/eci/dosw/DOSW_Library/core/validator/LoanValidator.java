package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanStatus;
import edu.eci.dosw.DOSW_Library.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador de lógica de negocio para préstamos.
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>✅ Validar reglas de préstamo</li>
 * <li>✅ Validar límites de préstamos por usuario</li>
 * <li>✅ Validar fechas de préstamo y devolución</li>
 * <li>✅ Validar transiciones de estado</li>
 * <li>✅ Logging detallado de validaciones</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Component
public class LoanValidator {

    private static final Logger logger = LoggerFactory.getLogger(LoanValidator.class);

    /**
     * Máximo de préstamos activos permitidos por usuario.
     */
    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;

    /**
     * Días máximos de préstamo antes de considerarlo vencido.
     */
    private static final int MAX_LOAN_DAYS = 30;

    private final BookValidator bookValidator;
    private final UserValidator userValidator;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param bookValidator Validador de libros
     * @param userValidator Validador de usuarios
     */
    public LoanValidator(BookValidator bookValidator, UserValidator userValidator) {
        this.bookValidator = bookValidator;
        this.userValidator = userValidator;
        logger.debug("LoanValidator initialized with dependencies");
    }

    // ============================================
    // VALIDACIÓN COMPLETA DE PRÉSTAMO
    // ============================================

    /**
     * Valida todos los aspectos de un préstamo.
     * 
     * <p>
     * <b>Validaciones aplicadas:</b>
     * </p>
     * <ul>
     * <li>ID no puede ser nulo o vacío</li>
     * <li>Libro no puede ser nulo</li>
     * <li>Usuario no puede ser nulo</li>
     * <li>Fecha de préstamo no puede ser nula</li>
     * <li>Fecha de préstamo no puede ser futura</li>
     * <li>Estado no puede ser nulo</li>
     * <li>Si estado es RETURNED, debe tener fecha de devolución</li>
     * <li>Fecha de devolución no puede ser anterior a fecha de préstamo</li>
     * </ul>
     * 
     * @param loan Préstamo a validar
     * @return Lista de mensajes de error (vacía si no hay errores)
     */
    public List<String> validate(Loan loan) {
        logger.debug("Validating loan: {}", loan != null ? loan.getId() : "null");

        List<String> errors = new ArrayList<>();

        if (loan == null) {
            logger.warn("Loan is null - validation failed");
            errors.add("Loan cannot be null");
            return errors;
        }

        // Validar ID
        errors.addAll(validateId(loan.getId()));

        // Validar libro
        errors.addAll(validateBook(loan.getBook()));

        // Validar usuario
        errors.addAll(validateUser(loan.getUser()));

        // Validar fecha de préstamo
        errors.addAll(validateLoanDate(loan.getLoanDate()));

        // Validar estado
        errors.addAll(validateStatus(loan.getStatus()));

        // Validar consistencia de estado y fecha de devolución
        errors.addAll(validateStatusConsistency(loan));

        // Validar fechas de devolución
        if (loan.getReturnDate() != null) {
            errors.addAll(validateReturnDate(loan.getLoanDate(), loan.getReturnDate()));
        }

        if (errors.isEmpty()) {
            logger.debug("Loan {} validated successfully", loan.getId());
        } else {
            logger.warn("Loan {} validation failed with {} errors: {}",
                    loan.getId(),
                    errors.size(),
                    errors);
        }

        return errors;
    }

    // ============================================
    // VALIDACIONES INDIVIDUALES
    // ============================================

    /**
     * Valida el ID del préstamo.
     * 
     * @param id ID del préstamo
     * @return Lista de errores de validación
     */
    public List<String> validateId(String id) {
        List<String> errors = new ArrayList<>();

        if (id == null) {
            errors.add("Loan ID cannot be null");
            return errors;
        }

        if (id.trim().isEmpty()) {
            errors.add("Loan ID cannot be empty");
        }

        return errors;
    }

    /**
     * Valida el libro asociado al préstamo.
     * 
     * @param book Libro del préstamo
     * @return Lista de errores de validación
     */
    public List<String> validateBook(Book book) {
        List<String> errors = new ArrayList<>();

        if (book == null) {
            errors.add("Loan must have an associated book");
            return errors;
        }

        // Validar que el libro sea válido
        List<String> bookErrors = bookValidator.validate(book);
        if (!bookErrors.isEmpty()) {
            errors.add("Associated book is invalid: " + String.join(", ", bookErrors));
        }

        return errors;
    }

    /**
     * Valida el usuario asociado al préstamo.
     * 
     * @param user Usuario del préstamo
     * @return Lista de errores de validación
     */
    public List<String> validateUser(User user) {
        List<String> errors = new ArrayList<>();

        if (user == null) {
            errors.add("Loan must have an associated user");
            return errors;
        }

        // Validar que el usuario sea válido
        List<String> userErrors = userValidator.validate(user);
        if (!userErrors.isEmpty()) {
            errors.add("Associated user is invalid: " + String.join(", ", userErrors));
        }

        return errors;
    }

    /**
     * Valida la fecha de préstamo.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser nula</li>
     * <li>No puede ser fecha futura</li>
     * </ul>
     * 
     * @param loanDate Fecha de préstamo (LocalDateTime)
     * @return Lista de errores de validación
     */
    public List<String> validateLoanDate(LocalDateTime loanDate) {
        List<String> errors = new ArrayList<>();

        if (loanDate == null) {
            errors.add("Loan date cannot be null");
            return errors;
        }

        if (loanDate.isAfter(LocalDateTime.now())) {
            errors.add("Loan date cannot be in the future");
        }

        return errors;
    }

    /**
     * Valida la fecha de devolución.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser anterior a fecha de préstamo</li>
     * <li>No puede ser fecha futura</li>
     * </ul>
     * 
     * @param loanDate   Fecha de préstamo (LocalDateTime)
     * @param returnDate Fecha de devolución (LocalDateTime)
     * @return Lista de errores de validación
     */
    public List<String> validateReturnDate(LocalDateTime loanDate, LocalDateTime returnDate) {
        List<String> errors = new ArrayList<>();

        if (returnDate == null) {
            return errors; // Es válido que sea null si aún no se devuelve
        }

        if (loanDate != null && returnDate.isBefore(loanDate)) {
            errors.add("Return date cannot be before loan date");
        }

        if (returnDate.isAfter(LocalDateTime.now())) {
            errors.add("Return date cannot be in the future");
        }

        return errors;
    }

    /**
     * Valida el estado del préstamo.
     * 
     * @param status Estado del préstamo
     * @return Lista de errores de validación
     */
    public List<String> validateStatus(LoanStatus status) {
        List<String> errors = new ArrayList<>();

        if (status == null) {
            errors.add("Loan status cannot be null");
        }

        return errors;
    }

    /**
     * Valida la consistencia entre estado y fecha de devolución.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>Si status = RETURNED, debe tener returnDate</li>
     * <li>Si status = ACTIVE, NO debe tener returnDate</li>
     * </ul>
     * 
     * @param loan Préstamo a validar
     * @return Lista de errores de validación
     */
    public List<String> validateStatusConsistency(Loan loan) {
        List<String> errors = new ArrayList<>();

        if (loan == null || loan.getStatus() == null) {
            return errors;
        }

        if (loan.getStatus() == LoanStatus.RETURNED && loan.getReturnDate() == null) {
            errors.add("Loan marked as RETURNED must have a return date");
        }

        if (loan.getStatus() == LoanStatus.ACTIVE && loan.getReturnDate() != null) {
            errors.add("Active loan should not have a return date");
        }

        return errors;
    }

    // ============================================
    // VALIDACIONES DE REGLAS DE NEGOCIO
    // ============================================

    /**
     * Valida que un usuario no exceda el límite de préstamos activos.
     * 
     * <p>
     * <b>Regla de negocio:</b> Máximo 3 préstamos activos por usuario
     * </p>
     * 
     * @param currentActiveLoans Cantidad actual de préstamos activos
     * @return true si puede realizar más préstamos, false si alcanzó el límite
     */
    public boolean canUserBorrowMore(int currentActiveLoans) {
        boolean canBorrow = currentActiveLoans < MAX_ACTIVE_LOANS_PER_USER;

        logger.debug("User can borrow more? {} (current: {}, max: {})",
                canBorrow,
                currentActiveLoans,
                MAX_ACTIVE_LOANS_PER_USER);

        return canBorrow;
    }

    /**
     * Valida que un préstamo no esté vencido.
     * 
     * <p>
     * <b>Regla de negocio:</b> Préstamos vencen después de 30 días
     * </p>
     * 
     * @param loan Préstamo a validar
     * @return true si está vencido, false en caso contrario
     */
    public boolean isLoanOverdue(Loan loan) {
        if (loan == null || loan.getStatus() != LoanStatus.ACTIVE) {
            return false;
        }

        LocalDateTime dueDate = loan.getLoanDate().plusDays(MAX_LOAN_DAYS);
        boolean overdue = LocalDateTime.now().isAfter(dueDate);

        logger.debug("Loan {} is overdue? {} (loan date: {}, due date: {})",
                loan.getId(),
                overdue,
                loan.getLoanDate(),
                dueDate);

        return overdue;
    }

    /**
     * Calcula los días restantes de un préstamo.
     * 
     * @param loan Préstamo a evaluar
     * @return Días restantes (negativo si está vencido)
     */
    public long getDaysRemaining(Loan loan) {
        if (loan == null || loan.getStatus() != LoanStatus.ACTIVE) {
            return 0;
        }

        LocalDateTime dueDate = loan.getLoanDate().plusDays(MAX_LOAN_DAYS);
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate);

        logger.debug("Loan {} has {} days remaining", loan.getId(), daysRemaining);

        return daysRemaining;
    }

    /**
     * Valida que un préstamo pueda ser devuelto.
     * 
     * <p>
     * <b>Regla:</b> Solo préstamos ACTIVE pueden devolverse
     * </p>
     * 
     * @param loan Préstamo a validar
     * @return true si puede devolverse, false en caso contrario
     */
    public boolean canBeReturned(Loan loan) {
        if (loan == null) {
            logger.warn("Cannot validate return - loan is null");
            return false;
        }

        boolean canReturn = loan.getStatus() == LoanStatus.ACTIVE;

        logger.debug("Loan {} can be returned? {} (status: {})",
                loan.getId(),
                canReturn,
                loan.getStatus());

        return canReturn;
    }

    /**
     * Obtiene el máximo de préstamos activos permitidos.
     * 
     * @return Máximo de préstamos activos
     */
    public int getMaxActiveLoans() {
        return MAX_ACTIVE_LOANS_PER_USER;
    }

    /**
     * Obtiene los días máximos de préstamo.
     * 
     * @return Días máximos de préstamo
     */
    public int getMaxLoanDays() {
        return MAX_LOAN_DAYS;
    }
}