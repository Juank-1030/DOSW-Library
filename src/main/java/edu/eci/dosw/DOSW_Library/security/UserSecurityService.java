package edu.eci.dosw.DOSW_Library.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import edu.eci.dosw.DOSW_Library.core.repository.LoanRepository;

/**
 * Servicio de autorización personalizada para operaciones basadas en propiedad
 * de recursos.
 * 
 * <p>
 * <b>Propósito:</b> Proporcionar métodos reutilizables para verificaciones de
 * autorización
 * complejas que no pueden ser expresadas fácilmente con hasRole() o
 * hasAuthority() en @PreAuthorize.
 * </p>
 * 
 * <p>
 * <b>Uso en @PreAuthorize:</b>
 * </p>
 * 
 * <pre>
 * &#64;PreAuthorize("hasRole('LIBRARIAN') or @userSecurityService.isOwner(#userId, authentication)")
 * public ResponseEntity<UserDTO> getUser(@PathVariable String userId, Authentication authentication) {
 *     // ...
 * }
 * </pre>
 * 
 * <p>
 * <b>El prefijo @ en SpEL:</b> Indica que se llama a un Spring Bean por nombre
 * </p>
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Service("userSecurityService")
public class UserSecurityService {

    private static final Logger logger = LoggerFactory.getLogger(UserSecurityService.class);

    private final LoanRepository loanRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param loanRepository Repositorio de préstamos
     */
    public UserSecurityService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
        logger.debug("UserSecurityService initialized");
    }

    /**
     * Verifica si el usuario autenticado es el propietario del usuario
     * especificado.
     * 
     * <p>
     * Útil para permitir que usuarios vean/modifiquen solo su propio perfil,
     * pero los LIBRARIAN pueden ver/modificar cualquier usuario.
     * </p>
     * 
     * <p>
     * <b>Uso en @PreAuthorize:</b>
     * </p>
     * 
     * <pre>
     * &#64;PreAuthorize("hasRole('LIBRARIAN') or @userSecurityService.isOwner(#userId, authentication)")
     * public ResponseEntity<UserDTO> getUserById(@PathVariable String userId,
     *         Authentication authentication) {
     *     // Solo permite si es LIBRARIAN O si userId es el usuario autenticado
     * }
     * </pre>
     * 
     * @param userId         ID del usuario a verificar
     * @param authentication Token de autenticación con el usuario actual
     * @return true si el usuario autenticado es propietario de este usuario; false
     *         si no
     */
    public boolean isOwner(String userId, Authentication authentication) {
        if (authentication == null) {
            logger.warn("Authentication is null in isOwner check");
            return false;
        }

        String currentUsername = authentication.getName();
        boolean isOwner = userId.equals(currentUsername);

        logger.debug("Owner check: userId={}, currentUser={}, isOwner={}",
                userId, currentUsername, isOwner);

        return isOwner;
    }

    /**
     * Verifica si el usuario autenticado es el propietario (solicitante) del
     * préstamo.
     * 
     * <p>
     * Permite que usuarios vean/modifiquen solo sus propios préstamos,
     * pero los LIBRARIAN pueden ver/modificar cualquier préstamo.
     * </p>
     * 
     * <p>
     * <b>Uso en @PreAuthorize:</b>
     * </p>
     * 
     * <pre>
     * &#64;PreAuthorize("hasRole('LIBRARIAN') or @userSecurityService.isLoanOwner(#loanId, authentication)")
     * public ResponseEntity<LoanDTO> getLoanById(@PathVariable String loanId,
     *         Authentication authentication) {
     *     // Solo permite si es LIBRARIAN O si el préstamo pertenece al usuario actual
     * }
     * </pre>
     * 
     * @param loanId         ID del préstamo a verificar
     * @param authentication Token de autenticación con el usuario actual
     * @return true si el usuario autenticado es propietario de este préstamo; false
     *         si no
     */
    public boolean isLoanOwner(String loanId, Authentication authentication) {
        if (authentication == null) {
            logger.warn("Authentication is null in isLoanOwner check");
            return false;
        }

        if (loanId == null || loanId.isEmpty()) {
            logger.warn("LoanId is null or empty in isLoanOwner check");
            return false;
        }

        String currentUsername = authentication.getName();

        try {
            // Buscar el préstamo en la BD
            var loan = loanRepository.findById(loanId);

            if (loan.isEmpty()) {
                logger.warn("Loan not found: {}", loanId);
                return false;
            }

            // Verificar si el user_id del préstamo coincide con el usuario actual
            String loanOwnerId = loan.get().getUser().getId();
            boolean isOwner = loanOwnerId.equals(currentUsername);

            logger.debug("Loan owner check: loanId={}, loanOwnerId={}, currentUser={}, isOwner={}",
                    loanId, loanOwnerId, currentUsername, isOwner);

            return isOwner;

        } catch (Exception e) {
            logger.error("Error checking loan ownership: loanId={}, currentUser={}, error={}",
                    loanId, currentUsername, e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si el usuario tiene rol LIBRARIAN.
     * 
     * <p>
     * Método auxiliar para expresiones complejas en @PreAuthorize
     * </p>
     * 
     * @param authentication Token de autenticación
     * @return true si el usuario tiene rol LIBRARIAN; false en caso contrario
     */
    public boolean isLibrarian(Authentication authentication) {
        if (authentication == null) {
            logger.warn("Authentication is null in isLibrarian check");
            return false;
        }

        boolean isLibrarian = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));

        logger.debug("Librarian check: currentUser={}, isLibrarian={}",
                authentication.getName(), isLibrarian);

        return isLibrarian;
    }

    /**
     * Verifica si el usuario accede a préstamos de sí mismo o es LIBRARIAN.
     * 
     * <p>
     * Útil para endpoints como GET /api/loans/user/{userId} donde queremos
     * que los usuarios vean solo sus propios préstamos, pero LIBRARIAN pueda ver
     * todos.
     * </p>
     * 
     * <p>
     * <b>Uso en @PreAuthorize:</b>
     * </p>
     * 
     * <pre>
     * &#64;PreAuthorize("@userSecurityService.canAccessUserLoans(#userId, authentication)")
     * public ResponseEntity<List<LoanDTO>> getLoansByUser(@PathVariable String userId,
     *         Authentication authentication) {
     * }
     * </pre>
     * 
     * @param userId         ID del usuario cuyos préstamos se solicitan
     * @param authentication Token de autenticación
     * @return true si es propietario o es LIBRARIAN; false si no
     */
    public boolean canAccessUserLoans(String userId, Authentication authentication) {
        if (authentication == null) {
            logger.warn("Authentication is null in canAccessUserLoans check");
            return false;
        }

        String currentUsername = authentication.getName();
        boolean isLibrarian = isLibrarian(authentication);
        boolean isOwner = userId.equals(currentUsername);
        boolean hasAccess = isLibrarian || isOwner;

        logger.debug("User loans access check: requestedUserId={}, currentUser={}, " +
                "isLibrarian={}, isOwner={}, hasAccess={}",
                userId, currentUsername, isLibrarian, isOwner, hasAccess);

        return hasAccess;
    }
}
