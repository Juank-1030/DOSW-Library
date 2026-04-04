package edu.eci.dosw.DOSW_Library.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utilidad para generar identificadores únicos consistentes en toda la
 * aplicación.
 * 
 * <p>
 * <b>Propósito:</b>
 * </p>
 * <ul>
 * <li>✅ Garantizar formato consistente de IDs (BOOK-XXXX, USR-XXXX,
 * LOAN-XXXX)</li>
 * <li>✅ Centralizar estrategia de generación de IDs</li>
 * <li>✅ Facilitar cambios de estrategia (UUID → secuencial, etc.)</li>
 * <li>✅ Logging automático de IDs generados para auditoría</li>
 * </ul>
 * 
 * <p>
 * <b>Formato de IDs generados:</b>
 * </p>
 * <table border="1">
 * <tr>
 * <th>Entidad</th>
 * <th>Formato</th>
 * <th>Ejemplo</th>
 * </tr>
 * <tr>
 * <td>Book</td>
 * <td>BOOK-XXXXXXXX</td>
 * <td>BOOK-A1B2C3D4</td>
 * </tr>
 * <tr>
 * <td>User</td>
 * <td>USR-XXXXXXXX</td>
 * <td>USR-F9E8D7C6</td>
 * </tr>
 * <tr>
 * <td>Loan</td>
 * <td>LOAN-XXXXXXXX</td>
 * <td>LOAN-3B4C5D6E</td>
 * </tr>
 * </table>
 * 
 * <p>
 * <b>Ventajas sobre generación manual:</b>
 * </p>
 * <ul>
 * <li>✅ Formato uniforme en toda la aplicación</li>
 * <li>✅ Un solo lugar para cambiar estrategia</li>
 * <li>✅ Más fácil de testear (mock del componente)</li>
 * <li>✅ Logging centralizado</li>
 * </ul>
 * 
 * <p>
 * <b>Ejemplo de uso:</b>
 * </p>
 * 
 * <pre>{@code
 * @Service
 * public class BookService {
 *     private final IdGeneratorUtil idGenerator;
 * 
 *     public Book addBook(CreateBookDTO dto) {
 *         Book book = new Book();
 * 
 *         // ❌ ANTES (manual, inconsistente):
 *         // book.setId("BOOK-" + UUID.randomUUID().toString().substring(0, 8));
 * 
 *         // ✅ AHORA (consistente, centralizado):
 *         book.setId(idGenerator.generateBookId());
 * 
 *         return bookRepository.save(book);
 *     }
 * }
 * }</pre>
 * 
 * @author DOSW Company
 * @version 1.0 - Reducida
 */
@Component
public class IdGeneratorUtil {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorUtil.class);

    // ============================================
    // GENERACIÓN DE IDs POR ENTIDAD
    // ============================================

    /**
     * Genera un ID único para un libro.
     * 
     * <p>
     * <b>Formato:</b> BOOK-XXXXXXXX
     * </p>
     * <ul>
     * <li>Prefijo: {@link Constants#BOOK_ID_PREFIX}</li>
     * <li>Separador: Guión (-)</li>
     * <li>Identificador: 8 caracteres hexadecimales en mayúsculas</li>
     * </ul>
     * 
     * <p>
     * <b>Ejemplo de salida:</b> "BOOK-A1B2C3D4"
     * </p>
     * 
     * <p>
     * <b>Garantía de unicidad:</b> UUID v4 tiene probabilidad de colisión
     * prácticamente nula
     * (~1 en 2^122 para 8 caracteres).
     * </p>
     * 
     * @return ID único de libro con formato estandarizado
     */
    public String generateBookId() {
        String id = Constants.BOOK_ID_PREFIX + "-" + generateShortUUID();
        logger.debug("Generated Book ID: {}", id);
        return id;
    }

    /**
     * Genera un ID único para un usuario.
     * 
     * <p>
     * <b>Formato:</b> USR-XXXXXXXX
     * </p>
     * 
     * <p>
     * <b>Ejemplo de salida:</b> "USR-F9E8D7C6"
     * </p>
     * 
     * @return ID único de usuario con formato estandarizado
     */
    public String generateUserId() {
        String id = Constants.USER_ID_PREFIX + "-" + generateShortUUID();
        logger.debug("Generated User ID: {}", id);
        return id;
    }

    /**
     * Genera un ID único para un préstamo.
     * 
     * <p>
     * <b>Formato:</b> LOAN-XXXXXXXX
     * </p>
     * 
     * <p>
     * <b>Ejemplo de salida:</b> "LOAN-3B4C5D6E"
     * </p>
     * 
     * @return ID único de préstamo con formato estandarizado
     */
    public String generateLoanId() {
        String id = Constants.LOAN_ID_PREFIX + "-" + generateShortUUID();
        logger.debug("Generated Loan ID: {}", id);
        return id;
    }

    // ============================================
    // GENERACIÓN GENÉRICA CON PREFIJO
    // ============================================

    /**
     * Genera un ID con prefijo personalizado.
     * 
     * <p>
     * <b>Uso avanzado:</b> Para nuevas entidades que se agreguen en el futuro.
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * String reservationId = idGenerator.generateWithPrefix("RESV");
     * // Result: "RESV-A1B2C3D4"
     * 
     * String categoryId = idGenerator.generateWithPrefix("CAT");
     * // Result: "CAT-F9E8D7C6"
     * }</pre>
     * 
     * @param prefix Prefijo del ID (se convierte automáticamente a mayúsculas)
     * @return ID con formato PREFIX-XXXXXXXX
     */
    public String generateWithPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            logger.warn("Attempted to generate ID with empty prefix, using 'ID' as default");
            prefix = "ID";
        }

        String id = prefix.toUpperCase() + "-" + generateShortUUID();
        logger.debug("Generated ID with custom prefix '{}': {}", prefix, id);
        return id;
    }

    // ============================================
    // MÉTODO HELPER PRIVADO
    // ============================================

    /**
     * Genera un UUID corto de 8 caracteres hexadecimales.
     * 
     * <p>
     * <b>Implementación:</b>
     * </p>
     * <ul>
     * <li>Genera UUID v4 completo (36 caracteres)</li>
     * <li>Toma los primeros 8 caracteres</li>
     * <li>Convierte a mayúsculas para consistencia</li>
     * </ul>
     * 
     * <p>
     * <b>Formato:</b> XXXXXXXX (8 dígitos hexadecimales en mayúsculas)
     * </p>
     * 
     * <p>
     * <b>Ejemplo de salida:</b> "A1B2C3D4"
     * </p>
     * 
     * <p>
     * <b>⚠️ Nota sobre unicidad:</b>
     * </p>
     * <p>
     * Aunque es menos único que un UUID completo, 8 caracteres hexadecimales
     * proporcionan 4.3 mil millones de combinaciones posibles (16^8), lo cual es
     * más que suficiente para una biblioteca pequeña/mediana.
     * </p>
     * 
     * @return 8 caracteres hexadecimales en mayúsculas
     */
    private String generateShortUUID() {
        String shortUUID = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        logger.trace("Generated short UUID: {}", shortUUID);

        return shortUUID;
    }

    // ============================================
    // VALIDACIÓN DE IDs (OPCIONAL)
    // ============================================

    /**
     * Verifica si un ID tiene formato válido.
     * 
     * <p>
     * <b>Validaciones:</b>
     * </p>
     * <ul>
     * <li>No es null</li>
     * <li>No está vacío después de trim</li>
     * <li>No contiene espacios</li>
     * </ul>
     * 
     * <p>
     * <b>Ejemplo de uso:</b>
     * </p>
     * 
     * <pre>{@code
     * if (!idGenerator.isValidId(bookId)) {
     *     throw new IllegalArgumentException("Invalid book ID format");
     * }
     * }</pre>
     * 
     * @param id ID a validar
     * @return true si es válido, false en caso contrario
     */
    public boolean isValidId(String id) {
        if (id == null || id.trim().isEmpty()) {
            logger.warn("Invalid ID: null or empty");
            return false;
        }

        if (id.contains(" ")) {
            logger.warn("Invalid ID: contains spaces - '{}'", id);
            return false;
        }

        logger.trace("ID '{}' is valid", id);
        return true;
    }

    /**
     * Extrae el prefijo de un ID.
     * 
     * <p>
     * <b>Útil para:</b> Identificar el tipo de entidad desde el ID.
     * </p>
     * 
     * <p>
     * <b>Ejemplo:</b>
     * </p>
     * 
     * <pre>{@code
     * String prefix = idGenerator.extractPrefix("BOOK-A1B2C3D4");
     * // Result: "BOOK"
     * 
     * String userPrefix = idGenerator.extractPrefix("USR-F9E8D7C6");
     * // Result: "USR"
     * }</pre>
     * 
     * @param id ID del cual extraer el prefijo
     * @return Prefijo del ID, o null si no tiene formato PREFIX-XXXX
     */
    public String extractPrefix(String id) {
        if (id == null || !id.contains("-")) {
            logger.warn("Cannot extract prefix from ID: {}", id);
            return null;
        }

        String prefix = id.substring(0, id.indexOf("-"));
        logger.trace("Extracted prefix '{}' from ID '{}'", prefix, id);
        return prefix;
    }
}