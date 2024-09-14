package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.Loan;
import org.evpro.bookshopV5.model.LoanDetail;

import java.time.LocalDate;
import java.util.List;

public interface LoanFunctions {
    Loan createLoan(Integer userId, List<LoanDetail> loanDetails);
    List<Loan> getLoansByUser(Integer userId);
    Loan getLoanById(Integer loanId);
    void extendLoanDueDate(Integer loanId, LocalDate newDueDate);
    void returnLoan(Integer loanId);
    List<Loan> getOverdueLoans();
    void sendLoanReminders();
    List<LoanDetail> getLoanDetailsByLoanId(Integer loanId);
}
