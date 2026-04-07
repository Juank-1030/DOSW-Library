package edu.eci.dosw.DOSW_Library.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utilidad para cálculos de fechas relacionados con la lógica de negocio de la
 * biblioteca.
 * 
 * <p>
 * <b>Diferencia con Java estándar:</b>
 * </p>
 * <ul>
 * <li>❌ NO duplica funcionalidad básica de LocalDate</li>
 * <li>✅ SÍ centraliza REGLAS DE NEGOCIO específicas de préstamos</li>
 * <li>✅ SÍ agrega logging automático para auditoría</li>
 * <li>✅ SÍ valida entradas con null-safety</li>
 * </ul>
 * 
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>✅ Calcular fechas de vencimiento según reglas de negocio</li>
 * <li>✅ Determinar si un préstamo está vencido</li>
 * <li>✅ Calcular días restantes/transcurridos de préstamos</li>
 * <li>✅ Logging de operaciones para auditoría</li>
 * </ul>
 * 
 * <p>
 * <b>Ejemplo de uso:</b>
 * </p>
 * 
 * <pre>{@code
 * @Service
 * public class LoanService {
 *     private final DateUtil dateUtil;
 * 
 *     public Loan createLoan(String bookId, String userId) {
 *         Loan loan = new Loan();
 *         loan.setLoanDate(LocalDate.now());
 * 
 *         // ✅ Usar DateUtil para reglas de negocio
 *         LocalDate dueDate = dateUtil.calculateDueDate(loan.getLoanDate());
 *         logger.info("Loan due date: {}", dueDate);
 * 
 *         return loanRepository.save(loan);
 *     }
 * 
 *     public List<Loan> getOverdueLoans() {
 *         return loanRepository.findAll().stream()
 *                 .filter(loan -> dateUtil.isLoanOverdue(loan.getLoanDate()))
 *                 .collect(Collectors.toList());
 *     }
 * }
 * }</pre>
 * 
 * @author DOSW Company
 * @version 1.0 - Reducida (solo lógica de negocio)
 */
@Component
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    // ============================================
    // CÁLCULO DE FECHAS DE VENCIMIENTO
    // ============================================

    /**
     * Calcula la fecha de vencimiento de un préstamo.
     * 
     * <p>
     * <b>REGLA DE NEGOCIO:</b> Los préstamos vencen después de
     * {@link Constants#MAX_LOAN_DAYS} días desde la fecha de préstamo.
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * LocalDate loanDate = LocalDate.of(2024, 1, 15);
     * LocalDate dueDate = dateUtil.calculateDueDate(loanDate);
     * // Result: 2024-02-14 (30 días después)
     * }</pre>
     * 
     * @param loanDate Fecha del préstamo
     * @return Fecha de vencimiento (loanDate + 30 días), o null si loanDate es null
     */
    public LocalDate calculateDueDate(LocalDate loanDate) {
        if (loanDate == null) {
            logger.warn("Cannot calculate due date - loan date is null");
            return null;
        }

        LocalDate dueDate = loanDate.plusDays(Constants.MAX_LOAN_DAYS);

        logger.debug("Calculated due date: {} -> {} ({} days)",
                loanDate,
                dueDate,
                Constants.MAX_LOAN_DAYS);

        return dueDate;
    }

    /**
     * Calcula cuántos días faltan para el vencimiento de un préstamo.
     * 
     * <p>
     * <b>Interpretación del resultado:</b>
     * </p>
     * <ul>
     * <li><b>Positivo:</b> Días restantes antes del vencimiento</li>
     * <li><b>Cero:</b> Vence hoy</li>
     * <li><b>Negativo:</b> Días de atraso (ya está vencido)</li>
     * </ul>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * // Hoy es 2024-01-25
     * LocalDate loanDate = LocalDate.of(2024, 1, 15);
     * long days = dateUtil.daysUntilDue(loanDate);
     * // Result: 20 días restantes (vence el 2024-02-14)
     * 
     * LocalDate oldLoan = LocalDate.of(2023, 12, 1);
     * long overdue = dateUtil.daysUntilDue(oldLoan);
     * // Result: -25 días (vencido hace 25 días)
     * }</pre>
     * 
     * @param loanDate Fecha del préstamo
     * @return Días restantes (negativo si está vencido), o 0 si loanDate es null
     */
    public long daysUntilDue(LocalDate loanDate) {
        if (loanDate == null) {
            logger.warn("Cannot calculate days until due - loan date is null");
            return 0;
        }

        LocalDate dueDate = calculateDueDate(loanDate);
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

        logger.debug("Days until due for loan from {}: {} days", loanDate, daysRemaining);

        return daysRemaining;
    }

    // ============================================
    // VERIFICACIÓN DE VENCIMIENTO
    // ============================================

    /**
     * Verifica si un préstamo está vencido.
     * 
     * <p>
     * <b>REGLA DE NEGOCIO:</b> Un préstamo está vencido si han pasado más de
     * {@link Constants#MAX_LOAN_DAYS} días desde la fecha de préstamo.
     * </p>
     * 
     * <p>
     * <b>Ejemplo de uso:</b>
     * </p>
     * 
     * <pre>{@code
     * List<Loan> overdueLoans = loanRepository.findByStatus(LoanStatus.ACTIVE)
     *         .stream()
     *         .filter(loan -> dateUtil.isLoanOverdue(loan.getLoanDate()))
     *         .collect(Collectors.toList());
     * 
     * if (dateUtil.isLoanOverdue(loan.getLoanDate())) {
     *     notificationService.sendOverdueNotification(loan.getUser());
     * }
     * }</pre>
     * 
     * @param loanDate Fecha del préstamo
     * @return true si está vencido, false en caso contrario (o si loanDate es null)
     */
    public boolean isLoanOverdue(LocalDate loanDate) {
        if (loanDate == null) {
            logger.warn("Cannot check if loan is overdue - loan date is null");
            return false;
        }

        LocalDate dueDate = calculateDueDate(loanDate);
        boolean overdue = LocalDate.now().isAfter(dueDate);

        logger.debug("Loan from {} is overdue? {} (due date: {})",
                loanDate,
                overdue,
                dueDate);

        return overdue;
    }

    // ============================================
    // CÁLCULO DE DÍAS TRANSCURRIDOS
    // ============================================

    /**
     * Calcula cuántos días han transcurrido desde el préstamo.
     * 
     * <p>
     * <b>Uso típico:</b> Estadísticas, reportes de duración de préstamos.
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * LocalDate loanDate = LocalDate.of(2024, 1, 1);
     * // Hoy es 2024-01-15
     * long days = dateUtil.daysSinceLoan(loanDate);
     * // Result: 14 días
     * }</pre>
     * 
     * @param loanDate Fecha del préstamo
     * @return Días transcurridos desde el préstamo, o 0 si loanDate es null
     */
    public long daysSinceLoan(LocalDate loanDate) {
        if (loanDate == null) {
            logger.warn("Cannot calculate days since loan - loan date is null");
            return 0;
        }

        long days = ChronoUnit.DAYS.between(loanDate, LocalDate.now());

        logger.trace("Days since loan {}: {}", loanDate, days);

        return days;
    }

    /**
     * Calcula días entre dos fechas.
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * LocalDate loanDate = LocalDate.of(2024, 1, 15);
     * LocalDate returnDate = LocalDate.of(2024, 1, 30);
     * long days = dateUtil.daysBetween(loanDate, returnDate);
     * // Result: 15 días
     * }</pre>
     * 
     * @param startDate Fecha inicial
     * @param endDate   Fecha final
     * @return Días entre las fechas (positivo si endDate > startDate), o 0 si
     *         alguna es null
     */
    public long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            logger.warn("Cannot calculate days between - one or both dates are null");
            return 0;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);

        logger.trace("Days between {} and {}: {}", startDate, endDate, days);

        return days;
    }

    // ============================================
    // VALIDACIONES DE FECHAS
    // ============================================

    /**
     * Verifica si una fecha es válida para un préstamo (no futura).
     * 
     * <p>
     * <b>Regla:</b> Los préstamos no pueden tener fecha futura.
     * </p>
     * 
     * @param loanDate Fecha a validar
     * @return true si es válida (hoy o pasado), false si es futura o null
     */
    public boolean isValidLoanDate(LocalDate loanDate) {
        if (loanDate == null) {
            logger.warn("Loan date is null");
            return false;
        }

        boolean valid = !loanDate.isAfter(LocalDate.now());

        logger.trace("Loan date {} is valid? {}", loanDate, valid);

        return valid;
    }

    /**
     * Verifica si una fecha de devolución es válida.
     * 
     * <p>
     * <b>Reglas:</b>
     * </p>
     * <ul>
     * <li>No puede ser anterior a la fecha de préstamo</li>
     * <li>No puede ser futura</li>
     * </ul>
     * 
     * @param loanDate   Fecha del préstamo
     * @param returnDate Fecha de devolución
     * @return true si es válida, false en caso contrario
     */
    public boolean isValidReturnDate(LocalDate loanDate, LocalDate returnDate) {
        if (loanDate == null || returnDate == null) {
            logger.warn("Cannot validate return date - loan date or return date is null");
            return false;
        }

        boolean valid = !returnDate.isBefore(loanDate) && !returnDate.isAfter(LocalDate.now());

        logger.trace("Return date {} for loan {} is valid? {}", returnDate, loanDate, valid);

        return valid;
    }
}