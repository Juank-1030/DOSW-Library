package edu.eci.dosw.DOSW_Library.integration;

import edu.eci.dosw.DOSW_Library.controller.dto.CreateUserDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.CreateBookDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.CreateLoanDTO;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.core.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.core.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de Integración Funcionales - DOSW Library
 * Verifica que cada operación de los controladores funciona correctamente
 * y persiste datos reales en la BD.
 * 
 * @author DOSW Company
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Pruebas Funcionales de Integración - DOSW Library")
class FunctionalIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    private static final String USERS_ENDPOINT = "/api/users";
    private static final String BOOKS_ENDPOINT = "/api/books";
    private static final String LOANS_ENDPOINT = "/api/loans";

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ============================================
    // PRUEBAS DE USUARIOS
    // ============================================

    @Test
    @DisplayName("TEST 1.1: POST /api/users - Crear usuario y verificar persistencia en BD")
    void testCreateUserAndVerifyInDatabase() {
        CreateUserDTO dto =  CreateUserDTO.builder()
                .id("USR-001")
                .name("Juan Pérez")
                .email("juan@example.com")
                .username("juanperez")
                .password("securepass123")
                .role("USUARIO")
                .build();

        webTestClient.post()
                .uri(USERS_ENDPOINT)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated();

        User userInDb = userRepository.findById("USR-001").orElse(null);
        assertNotNull(userInDb, "Usuario debe existir en BD");
        assertEquals("Juan Pérez", userInDb.getName());
        assertEquals("juan@example.com", userInDb.getEmail());
        System.out.println("✅ TEST 1.1 PASSED: Usuario creado y persistido en BD");
    }

    @Test
    @DisplayName("TEST 1.2: GET /api/users - Listar usuarios")
    void testGetAllUsers() {
        User user1 = User.builder()
                .id("USR-001").name("User 1").email("user1@test.com")
                .username("user1").password("pass").role("USUARIO").build();
        User user2 = User.builder()
                .id("USR-002").name("User 2").email("user2@test.com")
                .username("user2").password("pass").role("USUARIO").build();

        userRepository.saveAll(java.util.List.of(user1, user2));

        webTestClient.get()
                .uri(USERS_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(2);

        assertEquals(2L, userRepository.count());
        System.out.println("✅ TEST 1.2 PASSED: 2 usuarios listados correctamente");
    }

    @Test
    @DisplayName("TEST 1.3: GET /api/users/{id} - Obtener usuario por ID")
    void testGetUserById() {
        User user = User.builder()
                .id("USR-001").name("Test User").email("test@test.com")
                .username("testuser").password("pass").role("USUARIO").build();
        userRepository.save(user);

        webTestClient.get()
                .uri(USERS_ENDPOINT + "/USR-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test User");

        System.out.println("✅ TEST 1.3 PASSED: Usuario obtenido correctamente");
    }

    // ============================================
    // PRUEBAS DE LIBROS
    // ============================================

    @Test
    @DisplayName("TEST 2.1: POST /api/books - Crear libro")
    void testCreateBook() {
        CreateBookDTO dto = CreateBookDTO.builder()
                .id("BOOK-001")
                .title("Clean Code")
                .author("Robert C. Martin")
                .copies(5)
                .build();

        webTestClient.post()
                .uri(BOOKS_ENDPOINT)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated();

        Book bookInDb = bookRepository.findById("BOOK-001").orElse(null);
        assertNotNull(bookInDb, "Libro debe existir en BD");
        assertEquals("Clean Code", bookInDb.getTitle());
        assertEquals(5, bookInDb.getCopies());
        System.out.println("✅ TEST 2.1 PASSED: Libro creado y persistido en BD");
    }

    @Test
    @DisplayName("TEST 2.2: GET /api/books - Listar libros")
    void testGetAllBooks() {
        Book book1 = Book.builder()
                .id("BOOK-001").title("Book 1").author("Author 1")
                .copies(3).available(3).build();
        Book book2 = Book.builder()
                .id("BOOK-002").title("Book 2").author("Author 2")
                .copies(2).available(2).build();

        bookRepository.saveAll(java.util.List.of(book1, book2));

        webTestClient.get()
                .uri(BOOKS_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(2);

        assertEquals(2L, bookRepository.count());
        System.out.println("✅ TEST 2.2 PASSED: 2 libros listados correctamente");
    }

    @Test
    @DisplayName("TEST 2.3: GET /api/books/{id} - Obtener libro por ID")
    void testGetBookById() {
        Book book = Book.builder()
                .id("BOOK-001").title("Design Patterns").author("Gang of Four")
                .copies(2).available(2).build();
        bookRepository.save(book);

        webTestClient.get()
                .uri(BOOKS_ENDPOINT + "/BOOK-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Design Patterns");

        System.out.println("✅ TEST 2.3 PASSED: Libro obtenido correctamente");
    }

    // ============================================
    // PRUEBAS DE PRÉSTAMOS
    // ============================================

    @Test
    @DisplayName("TEST 3.1: POST /api/loans - Crear préstamo y verificar inventario")
    void testCreateLoanAndCheckInventory() {
        User user = User.builder()
                .id("USR-001").name("Test User").email("test@test.com")
                .username("testuser").password("pass").role("USUARIO").build();
        Book book = Book.builder()
                .id("BOOK-001").title("Test Book").author("Test Author")
                .copies(5).available(5).build();

        userRepository.save(user);
        bookRepository.save(book);

        int copiesBefore = bookRepository.findById("BOOK-001").get().getAvailable();
        assertEquals(5, copiesBefore);

        CreateLoanDTO dto = CreateLoanDTO.builder()
                .bookId("BOOK-001")
                .userId("USR-001")
                .build();

        webTestClient.post()
                .uri(LOANS_ENDPOINT)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated();

        int copiesAfter = bookRepository.findById("BOOK-001").get().getAvailable();
        assertEquals(4, copiesAfter, "Debe haber decrementado el inventario");
        assertEquals(1L, loanRepository.count());
        System.out.println("✅ TEST 3.1 PASSED: Préstamo creado e inventario actualizado en BD");
    }

    @Test
    @DisplayName("TEST 3.2: PUT /api/loans/{id}/return - Devolver libro")
    void testReturnLoanAndCheckInventory() {
        User user = User.builder()
                .id("USR-001").name("Test User").email("test@test.com")
                .username("testuser").password("pass").role("USUARIO").build();
        Book book = Book.builder()
                .id("BOOK-001").title("Test Book").author("Test Author")
                .copies(1).available(0).build();
        Loan loan = Loan.builder()
                .id("LOAN-001").book(book).user(user).build();

        userRepository.save(user);
        bookRepository.save(book);
        loanRepository.save(loan);

        int copiesBefore = bookRepository.findById("BOOK-001").get().getAvailable();
        assertEquals(0, copiesBefore);

        webTestClient.put()
                .uri(LOANS_ENDPOINT + "/LOAN-001/return")
                .exchange()
                .expectStatus().isOk();

        int copiesAfter = bookRepository.findById("BOOK-001").get().getAvailable();
        assertEquals(1, copiesAfter, "Debe haber incrementado el inventario");
        System.out.println("✅ TEST 3.2 PASSED: Libro devuelto e inventario actualizado en BD");
    }

    @Test
    @DisplayName("TEST 3.3: GET /api/loans - Listar préstamos")
    void testGetAllLoans() {
        User user = User.builder()
                .id("USR-001").name("Test User").email("test@test.com")
                .username("testuser").password("pass").role("USUARIO").build();
        Book book = Book.builder()
                .id("BOOK-001").title("Test Book").author("Test Author")
                .copies(1).available(0).build();
        Loan loan = Loan.builder()
                .id("LOAN-001").book(book).user(user).build();

        userRepository.save(user);
        bookRepository.save(book);
        loanRepository.save(loan);

        webTestClient.get()
                .uri(LOANS_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(1);

        assertEquals(1L, loanRepository.count());
        System.out.println("✅ TEST 3.3 PASSED: 1 préstamo listado correctamente");
    }

    @Test
    @DisplayName("TEST 3.4: GET /api/loans/{id} - Obtener préstamo por ID")
    void testGetLoanById() {
        User user = User.builder()
                .id("USR-001").name("Test User").email("test@test.com")
                .username("testuser").password("pass").role("USUARIO").build();
        Book book = Book.builder()
                .id("BOOK-001").title("Test Book").author("Test Author")
                .copies(1).available(0).build();
        Loan loan = Loan.builder()
                .id("LOAN-001").book(book).user(user).build();

        userRepository.save(user);
        bookRepository.save(book);
        loanRepository.save(loan);

        webTestClient.get()
                .uri(LOANS_ENDPOINT + "/LOAN-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("LOAN-001");

        System.out.println("✅ TEST 3.4 PASSED: Préstamo obtenido correctamente");
    }

    @Test
    @DisplayName("TEST 3.5: Escenario Completo - Usuario + Libro + Préstamo")
    void testCompleteScenario() {
        // Step 1: Crear usuario
        CreateUserDTO userDTO = CreateUserDTO.builder()
                .id("USR-COMPLETE")
                .name("Usuario Completo")
                .email("complete@test.com")
                .username("complete")
                .password("pass123")
                .role("USUARIO")
                .build();

        webTestClient.post()
                .uri(USERS_ENDPOINT)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isCreated();
        System.out.println("✅ STEP 1: Usuario creado en BD");

        // Step 2: Crear libro
        CreateBookDTO bookDTO = CreateBookDTO.builder()
                .id("BOOK-COMPLETE")
                .title("Complete Flow Book")
                .author("Flow Author")
                .copies(3)
                .build();

        webTestClient.post()
                .uri(BOOKS_ENDPOINT)
                .bodyValue(bookDTO)
                .exchange()
                .expectStatus().isCreated();
        System.out.println("✅ STEP 2: Libro creado en BD (3 copias)");

        // Step 3: Crear préstamo
        CreateLoanDTO loanDTO = CreateLoanDTO.builder()
                .bookId("BOOK-COMPLETE")
                .userId("USR-COMPLETE")
                .build();

        webTestClient.post()
                .uri(LOANS_ENDPOINT)
                .bodyValue(loanDTO)
                .exchange()
                .expectStatus().isCreated();
        System.out.println("✅ STEP 3: Préstamo creado en BD");

        // Step 4: Verificar cambios en BD
        long usersCount = userRepository.count();
        long booksCount = bookRepository.count();
        long loansCount = loanRepository.count();
        int copiesAfterLoan = bookRepository.findById("BOOK-COMPLETE").get().getAvailable();

        assertEquals(1L, usersCount);
        assertEquals(1L, booksCount);
        assertEquals(1L, loansCount);
        assertEquals(2, copiesAfterLoan, "Copias debe ser 2 (3 - 1 préstamo)");

        System.out.println("✅ STEP 4: BD Verificada");
        System.out.println("✅ TEST 3.5 PASSED: Escenario completo ejecutado exitosamente");
    }
}
