package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.ErrorResponse;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import edu.eci.dosw.DOSW_Library.core.validator.LoanValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans", description = "Operaciones relacionadas con prestamos")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @Operation(summary = "Crear un prestamo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prestamo creado"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Libro no disponible", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Loan> createLoan(@RequestBody LoanDTO loanDTO) throws BookNotAvailableException {
        LoanValidator.validate(loanDTO);
        Loan loan = loanService.createLoan(loanDTO.getBookId(), loanDTO.getUserId());
        return new ResponseEntity<>(loan, HttpStatus.CREATED);
    }

    @PutMapping("/{loanId}/return")
    @Operation(summary = "Registrar devolucion de un prestamo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestamo devuelto"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Prestamo ya devuelto", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Loan> returnBook(@PathVariable String loanId) {
        Loan loan = loanService.returnBook(loanId);
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Listar todos los prestamos")
    @ApiResponse(responseCode = "200", description = "Listado de prestamos")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return new ResponseEntity<>(loanService.getAllLoans(), HttpStatus.OK);
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Consultar un prestamo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestamo encontrado"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Loan> getLoanById(@PathVariable String loanId) {
        return new ResponseEntity<>(loanService.getLoanById(loanId), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Consultar prestamos activos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestamos activos"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<Loan>> getActiveLoansByUser(@PathVariable String userId) {
        return new ResponseEntity<>(loanService.getActiveLoansByUser(userId), HttpStatus.OK);
    }
}
