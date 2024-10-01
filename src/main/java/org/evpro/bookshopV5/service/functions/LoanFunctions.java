package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.DTO.request.AddItemToLoanRequest;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDetailsDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface LoanFunctions {
    //User Functionality
    LoanDTO createDirectLoan(String email, AddItemToLoanRequest request);
    List<LoanDTO> getMyLoans(String email);
    LoanDTO getMyLastLoan(String email);
    LoanDTO returnLoan(Integer loanId);
    List<LoanDTO> getMyActiveLoans(String email);

    //Admin functionality
    List<LoanDTO> getAllLoans();
    List<LoanDTO> getUserLoan(Integer userId);
    boolean deleteAllLoans();
    boolean deleteLoanById(Integer loanId);
    Set<LoanDetailsDTO> getLoanDetailsByLoanId(Integer loanId);
    LocalDate extendLoanDueDate(Integer loanId);
    void sendLoanReminders();
    List<LoanDTO> getOverdueLoans();
}
