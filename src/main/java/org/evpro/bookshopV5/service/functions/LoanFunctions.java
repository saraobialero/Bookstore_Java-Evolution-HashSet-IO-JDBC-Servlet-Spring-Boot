package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.DTO.request.AddItemToLoanRequest;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDetailDTO;
import org.evpro.bookshopV5.model.Loan;
import org.evpro.bookshopV5.model.LoanDetail;

import java.time.LocalDate;
import java.util.List;

public interface LoanFunctions {
    //User Functionality
    LoanDTO createDirectLoan(String email, AddItemToLoanRequest request);
    List<LoanDTO> getMyLoans(String email);
    LoanDTO getMyLastLoan(String email);
    LoanDTO returnLoan(Integer loanId);

    //Admin functionality
    List<LoanDTO> getAllLoans();
    List<LoanDTO> getUserLoan(Integer userId);
    boolean deleteAllLoans();
    boolean deleteLoanById(Integer idLoan);
    List<LoanDetailDTO> getLoanDetailsByLoanId(Integer loanId);
    LocalDate extendLoanDueDate(Integer loanId, LocalDate newDueDate);
    void sendLoanReminders();
    List<LoanDTO> getOverdueLoans();
}
