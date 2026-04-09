package edu.eci.dosw.DOSW_Library.persistence.mongodb.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento MongoDB para la colección de libros.
 * Mapea la entidad Book de JPA a documento NoSQL en MongoDB.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@Document(collection = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDocument {

    /**
     * ID único del libro, sincronizado con SQL
     */
    @Id
    private String id;

    /**
     * Título del libro con índice para búsqueda rápida
     */
    @Indexed
    private String title;

    /**
     * Autor del libro
     */
    private String author;

    /**
     * ISBN único del libro (cuando se agregue)
     */
    @Indexed(unique = true, sparse = true)
    private String isbn;

    /**
     * Categorías del libro (múltiples)
     */
    private List<String> categories;

    /**
     * Tipo de publicación: LIBRO, REVISTA, EBOOK, etc
     */
    private String publicationType;

    /**
     * Fecha de publicación original
     */
    private LocalDateTime publicationDate;

    /**
     * Información de disponibilidad del libro
     */
    private BookAvailability availability;

    /**
     * Metadata: páginas, idioma, publisher
     */
    private BookMetadata metadata;

    /**
     * Fecha cuando fue agregado al catálogo
     */
    private LocalDateTime dateAddedToCatalog;

    /**
     * Estado activo/inactivo del libro
     */
    @Indexed
    private Boolean active;

    /**
     * Fecha de última sincronización desde SQL
     */
    private LocalDateTime lastSyncedAt;
}

/**
 * Clase anidada para disponibilidad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class BookAvailability {
    private String status; // AVAILABLE, UNAVAILABLE, MAINTENANCE
    private Integer totalCopies; // Total en catálogo
    private Integer availableCopies; // Disponibles para prestar
    private Integer loanedCopies; // En prestamoactualmente
}

/**
 * Clase anidada para metadata del libro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class BookMetadata {
    private Integer pages; // Número de páginas
    private String language; // Idioma (ES, EN, FR, etc)
    private String publisher; // Editorial/Empresa publicadora
}
