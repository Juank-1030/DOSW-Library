package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.model.Loan;

public class LoanMapper {

    private LoanMapper() {
    }

    public static LoanDTO toDTO(Loan loan) {
        return new LoanDTO(loan.getBook().getId(), loan.getUser().getId());
    }
}
