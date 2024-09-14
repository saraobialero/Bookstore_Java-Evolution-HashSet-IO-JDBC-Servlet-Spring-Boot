package org.evpro.bookshopV5.service;

import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.Loan;
import org.evpro.bookshopV5.model.LoanDetail;
import org.evpro.bookshopV5.service.functions.LoanFunctions;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService implements LoanFunctions {


    @Override
    public Loan createLoan(Integer userId, List<LoanDetail> loanDetails) {
        return null;
    }

    @Override
    public List<Loan> getLoansByUser(Integer userId) {
        return List.of();
    }

    @Override
    public Loan getLoanById(Integer loanId) {
        return null;
    }

    @Override
    public void extendLoanDueDate(Integer loanId, LocalDate newDueDate) {

    }

    @Override
    public void returnLoan(Integer loanId) {

    }

    @Override
    public List<Loan> getOverdueLoans() {
        return List.of();
    }

    @Override
    public void sendLoanReminders() {

    }

    @Override
    public List<LoanDetail> getLoanDetailsByLoanId(Integer loanId) {
        return List.of();
    }
}
