package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.CreateBookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UpdateBookInventoryDTO;
import edu.eci.dosw.DOSW_Library.persistence.relational.mapper.BookPersistenceMapper;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de libros de la biblioteca.
 * 
 * <p>
 * <b>Responsabilidades (según diapositivas):</b>
 * </p>
 * <ul>
 * <li>✅ Recibir solicitudes HTTP (requests)</li>
 * <li>✅ Devolver respuestas HTTP (responses)</li>
 * <li>✅ Comunicarse con el Service (NUNCA directamente con BD)</li>
 * <li>✅ Validar DTOs con @Valid</li>
 * <li>✅ Convertir Entity ↔ DTO usando Mappers</li>
 * <li>✅ NO contener lógica de negocio</li>
 * </ul>
 * 
 * <p>
 * <b>Anotaciones importantes:</b>
 * </p>
 * <ul>
 * <li>@RestController - Marca como controlador REST</li>
 * <li>@RequestMapping("/api/books") - Base path</li>
 * <li>@Tag - Documentación Swagger</li>
 * </ul>
 * 
 * <p>
 * <b>Patrón de respuesta:</b>
 * </p>
 * <ul>
 * <li>Operaciones exitosas → DTO en ResponseEntity</li>
 * <li>Errores → Manejados por GlobalExceptionHandler</li>
 * </ul>
 * 
 * @author DOSW Company
 * @version 2.0 - Con Logging
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "API para gestión de libros de la biblioteca")
public class BookController {

        private static final Logger logger = LoggerFactory.getLogger(BookController.class);

        private final BookService bookService;
        private final BookPersistenceMapper bookMapper;

        /**
         * Constructor con inyección de dependencias.
         * 
         * @param bookService Servicio de libros
         * @param bookMapper  Mapper de conversión DTO ↔ Entity
         */
        public BookController(BookService bookService, BookPersistenceMapper bookMapper) {
                this.bookService = bookService;
                this.bookMapper = bookMapper;
                logger.info("BookController initialized");
        }

        // ============================================
        // POST - CREAR LIBRO
        // ============================================

        /**
         * Crea un nuevo libro en la biblioteca.
         * 
         * <p>
         * <b>Endpoint:</b> POST /api/books
         * </p>
         * 
         * <p>
         * <b>Flujo:</b>
         * </p>
         * <ol>
         * <li>Recibir CreateBookDTO (validado con @Valid)</li>
         * <li>Convertir DTO → Entity con Mapper</li>
         * <li>Llamar a Service para lógica de negocio</li>
         * <li>Convertir Entity → BookDTO</li>
         * <li>Retornar ResponseEntity con código 201 CREATED</li>
         * </ol>
         * 
         * <p>
         * <b>Ejemplo de request:</b>
         * </p>
         * 
         * <pre>
         * POST /api/books
         * {
         *   "id": "BOOK-001",
         *   "title": "Clean Code",
         *   "author": "Robert C. Martin",
         *   "copies": 5
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
         *   "id": "BOOK-001",
         *   "title": "Clean Code",
         *   "author": "Robert C. Martin",
         *   "copies": 5,
         *   "available": true
         * }
         * </pre>
         * 
         * @param createDTO DTO con datos del libro a crear (validado automáticamente)
         * @return ResponseEntity con BookDTO y código 201 CREATED
         */
        @PostMapping
        @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(summary = "Crear nuevo libro", description = "Registra un nuevo libro en el sistema de biblioteca con su inventario inicial")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Libro creado exitosamente", content = @Content(schema = @Schema(implementation = BookDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (validación fallida)"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "409", description = "El libro ya existe en el sistema")
        })
        public ResponseEntity<BookDTO> createBook(
                        @Valid @RequestBody CreateBookDTO createDTO) {

                logger.info("POST /api/books - Creating book: {}", createDTO.getId());
                logger.debug("Request body: {}", createDTO);

                // Convertir DTO → Entity
                Book book = bookMapper.toEntity(createDTO);

                // Lógica de negocio (Service)
                Book createdBook = bookService.addBook(book, createDTO.getCopies());

                // Convertir Entity → DTO
                BookDTO responseDTO = bookMapper.toDTO(createdBook);

                logger.info("Book created successfully: {} | Response: {}",
                                createdBook.getId(),
                                HttpStatus.CREATED);

                return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }

        // ============================================
        // GET - OBTENER TODOS LOS LIBROS
        // ============================================

        /**
         * Obtiene la lista de todos los libros registrados.
         * 
         * <p>
         * <b>Endpoint:</b> GET /api/books
         * </p>
         * 
         * <p>
         * <b>Ejemplo de response:</b>
         * </p>
         * 
         * <pre>
         * 200 OK
         * [
         *   {
         *     "id": "BOOK-001",
         *     "title": "Clean Code",
         *     "author": "Robert C. Martin",
         *     "copies": 5,
         *     "available": true
         *   },
         *   {
         *     "id": "BOOK-002",
         *     "title": "The Pragmatic Programmer",
         *     "author": "Andrew Hunt",
         *     "copies": 0,
         *     "available": false
         *   }
         * ]
         * </pre>
         * 
         * @return ResponseEntity con lista de BookDTO y código 200 OK
         */
        @GetMapping
        @Operation(summary = "Listar todos los libros", description = "Obtiene la lista completa de libros registrados en la biblioteca")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header")
        })
        public ResponseEntity<List<BookDTO>> getAllBooks() {
                logger.info("GET /api/books - Retrieving all books");

                // Obtener libros del Service
                List<Book> books = bookService.getAllBooks();

                // Convertir List<Entity> → List<DTO>
                List<BookDTO> responseDTOs = bookMapper.toDTOList(books);

                logger.info("Retrieved {} books | Response: {}",
                                responseDTOs.size(),
                                HttpStatus.OK);

                return ResponseEntity.ok(responseDTOs);
        }

        // ============================================
        // GET - OBTENER LIBRO POR ID
        // ============================================

        /**
         * Obtiene un libro específico por su ID.
         * 
         * <p>
         * <b>Endpoint:</b> GET /api/books/{id}
         * </p>
         * 
         * <p>
         * <b>Ejemplo de request:</b>
         * </p>
         * 
         * <pre>
         * GET / api / books / BOOK - 001
         * </pre>
         * 
         * <p>
         * <b>Ejemplo de response exitosa:</b>
         * </p>
         * 
         * <pre>
         * 200 OK
         * {
         *   "id": "BOOK-001",
         *   "title": "Clean Code",
         *   "author": "Robert C. Martin",
         *   "copies": 5,
         *   "available": true
         * }
         * </pre>
         * 
         * @param id ID del libro a buscar
         * @return ResponseEntity con BookDTO y código 200 OK
         * @throws ResourceNotFoundException Si el libro no existe (manejado por
         *                                   GlobalExceptionHandler)
         */
        @GetMapping("/{id}")
        @Operation(summary = "Obtener libro por ID", description = "Busca y retorna un libro específico por su identificador único")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Libro encontrado exitosamente", content = @Content(schema = @Schema(implementation = BookDTO.class))),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "404", description = "Libro no encontrado")
        })
        public ResponseEntity<BookDTO> getBookById(
                        @Parameter(description = "ID único del libro", example = "BOOK-001") @PathVariable String id) {

                logger.info("GET /api/books/{} - Retrieving book", id);

                // Obtener libro del Service
                Book book = bookService.getBookById(id);

                // Convertir Entity → DTO
                BookDTO responseDTO = bookMapper.toDTO(book);

                logger.info("Book {} found | Response: {}", id, HttpStatus.OK);

                return ResponseEntity.ok(responseDTO);
        }

        // ============================================
        // PATCH - ACTUALIZAR INVENTARIO
        // ============================================

        /**
         * Actualiza el inventario de copias de un libro.
         * 
         * <p>
         * <b>Endpoint:</b> PATCH /api/books/{id}/inventory
         * </p>
         * 
         * <p>
         * <b>Operaciones soportadas:</b>
         * </p>
         * <ul>
         * <li>SET - Establecer cantidad absoluta</li>
         * <li>ADD - Incrementar copias</li>
         * <li>REMOVE - Decrementar copias</li>
         * </ul>
         * 
         * <p>
         * <b>Ejemplo de request (ADD):</b>
         * </p>
         * 
         * <pre>
         * PATCH /api/books/BOOK-001/inventory
         * {
         *   "operation": "ADD",
         *   "quantity": 3,
         *   "reason": "Compra de nuevos ejemplares"
         * }
         * </pre>
         * 
         * <p>
         * <b>Ejemplo de response:</b>
         * </p>
         * 
         * <pre>
         * 200 OK
         * {
         *   "id": "BOOK-001",
         *   "title": "Clean Code",
         *   "author": "Robert C. Martin",
         *   "copies": 8,
         *   "available": true
         * }
         * </pre>
         * 
         * @param id        ID del libro
         * @param updateDTO DTO con operación y cantidad
         * @return ResponseEntity con BookDTO actualizado y código 200 OK
         */
        @PatchMapping("/{id}/inventory")
        @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(summary = "Actualizar inventario de libro", description = "Modifica la cantidad de copias disponibles mediante operaciones SET, ADD o REMOVE")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente", content = @Content(schema = @Schema(implementation = BookDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Operación inválida o datos incorrectos"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
                        @ApiResponse(responseCode = "409", description = "Operación resultaría en inventario negativo")
        })
        public ResponseEntity<BookDTO> updateInventory(
                        @Parameter(description = "ID del libro", example = "BOOK-001") @PathVariable String id,
                        @Valid @RequestBody UpdateBookInventoryDTO updateDTO) {

                logger.info("PATCH /api/books/{}/inventory - Operation: {} | Quantity: {}",
                                id,
                                updateDTO.getOperation(),
                                updateDTO.getQuantity());

                // Delegación al servicio según tipo de operación
                if (updateDTO.getOperation() == UpdateBookInventoryDTO.InventoryOperation.SET) {
                        // Establecer cantidad absoluta
                        bookService.setBookCopies(id, updateDTO.getQuantity());
                } else {
                        // ADD o REMOVE: calcular cambio incremental
                        int change = updateDTO.getOperation() == UpdateBookInventoryDTO.InventoryOperation.ADD
                                        ? updateDTO.getQuantity()
                                        : -updateDTO.getQuantity();
                        bookService.updateAvailability(id, change);
                }

                // Obtener libro actualizado desde BD
                Book updatedBook = bookService.getBookById(id);
                BookDTO responseDTO = bookMapper.toDTO(updatedBook);

                logger.info("Book {} inventory updated | New copies: {} | Response: {}",
                                id,
                                updatedBook.getCopies(),
                                HttpStatus.OK);

                return ResponseEntity.ok(responseDTO);
        }

        // ============================================
        // DELETE - ELIMINAR LIBRO
        // ============================================

        /**
         * Elimina un libro del sistema.
         * 
         * <p>
         * <b>Endpoint:</b> DELETE /api/books/{id}
         * </p>
         * 
         * <p>
         * <b>Validación de negocio:</b> El libro no debe tener préstamos activos
         * </p>
         * 
         * @param id ID del libro a eliminar
         * @return ResponseEntity vacío con código 204 NO CONTENT
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('LIBRARIAN')")
        @Operation(summary = "Eliminar libro", description = "Elimina un libro del sistema (solo si no tiene préstamos activos)")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Libro eliminado exitosamente"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
                        @ApiResponse(responseCode = "409", description = "No se puede eliminar - tiene préstamos activos")
        })
        public ResponseEntity<Void> deleteBook(
                        @Parameter(description = "ID del libro a eliminar", example = "BOOK-001") @PathVariable String id) {

                logger.info("DELETE /api/books/{} - Deleting book", id);

                bookService.deleteBook(id);

                logger.info("Book {} deleted successfully | Response: {}", id, HttpStatus.NO_CONTENT);

                return ResponseEntity.noContent().build();
        }

        // ============================================
        // GET - VERIFICAR DISPONIBILIDAD
        // ============================================

        /**
         * Verifica si un libro está disponible para préstamo.
         * 
         * <p>
         * <b>Endpoint:</b> GET /api/books/{id}/available
         * </p>
         * 
         * <p>
         * <b>Ejemplo de response:</b>
         * </p>
         * 
         * <pre>
         * 200 OK
         * {
         *   "available": true,
         *   "copies": 5
         * }
         * </pre>
         * 
         * @param id ID del libro
         * @return ResponseEntity con información de disponibilidad
         */
        @GetMapping("/{id}/available")
        @Operation(summary = "Verificar disponibilidad", description = "Consulta si un libro tiene copias disponibles para préstamo")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Información de disponibilidad obtenida"),
                        @ApiResponse(responseCode = "401", description = "No autenticado - Falta token en Authorization header"),
                        @ApiResponse(responseCode = "404", description = "Libro no encontrado")
        })
        public ResponseEntity<AvailabilityResponse> checkAvailability(
                        @Parameter(description = "ID del libro", example = "BOOK-001") @PathVariable String id) {

                logger.info("GET /api/books/{}/available - Checking availability", id);

                boolean available = bookService.isBookAvailable(id);
                int copies = bookService.getAvailableCopies(id);

                AvailabilityResponse response = new AvailabilityResponse(available, copies);

                logger.info("Book {} availability: {} | Copies: {}", id, available, copies);

                return ResponseEntity.ok(response);
        }

        /**
         * DTO interno para respuesta de disponibilidad.
         */
        @Schema(description = "Información de disponibilidad de un libro")
        public record AvailabilityResponse(
                        @Schema(description = "Indica si el libro está disponible", example = "true") boolean available,

                        @Schema(description = "Cantidad de copias disponibles", example = "5") int copies) {
        }
}