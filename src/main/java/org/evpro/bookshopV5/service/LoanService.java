package org.evpro.bookshopV5.service;


import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.exception.BookException;
import org.evpro.bookshopV5.exception.LoanException;
import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.DTO.request.AddItemToLoanRequest;
import org.evpro.bookshopV5.model.DTO.response.ErrorResponse;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDetailDTO;
import org.evpro.bookshopV5.model.Loan;
import org.evpro.bookshopV5.model.LoanDetail;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.model.enums.LoanStatus;
import org.evpro.bookshopV5.repository.BookRepository;
import org.evpro.bookshopV5.repository.LoanDetailRepository;
import org.evpro.bookshopV5.repository.LoanRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.service.functions.LoanFunctions;
import org.evpro.bookshopV5.utils.DTOConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.evpro.bookshopV5.utils.CodeMessages.*;
import static org.evpro.bookshopV5.utils.DTOConverter.*;

@Service
@RequiredArgsConstructor
public class LoanService implements LoanFunctions {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanDetailRepository loanDetailRepository;

    @Override
    public LoanDTO createDirectLoan(String email, AddItemToLoanRequest request) {
        User user = getUser(email);
        Book book = getBook(request.getBookId());

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.save(loan);

        LoanDetail loanDetail = new LoanDetail();
        loanDetail.setLoan(loan);
        loanDetail.setBook(book);
        loanDetailRepository.save(loanDetail);

        loan.setLoanDetails(Set.of(loanDetail));

        return convertToLoanDTO(loan);
    }

    @Override
    public List<LoanDTO> getMyLoans(String email) {
        User user = getUser(email);
        List<Loan> loans = user.getLoans();
        return convertCollection(loans, DTOConverter::convertToLoanDTO, ArrayList::new);
    }

    @Override
    public LoanDTO getMyLastLoan(String email) {
        return null;
    }

    @Override
    public LoanDTO returnLoan(Integer loanId) {
        Loan loan = loanRepository.findLoanById(loanId)
                    .orElseThrow(() -> new LoanException(
                                        new ErrorResponse(
                                                ErrorCode.LNF,
                                                "Loan")));
        if(loan.getReturnDate() != null ||
           loan.getStatus() == LoanStatus.CLOSED) {
            throw new LoanException(
                  new ErrorResponse(
                          ErrorCode.LAR,
                          "Loan already returned or closed in date: " + loan.getReturnDate() + loan.getStatus()));
        }

        if(loan.getLoanDate().isAfter(LocalDate.now())) {
            throw new LoanException(
                    new ErrorResponse(
                            ErrorCode.LAR,
                            "Loan already expired in date: " + loan.getDueDate()));

        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.CLOSED);

        loanRepository.save(loan);
        return convertToLoanDTO(loan);
    }

    @Override
    public List<LoanDTO> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return convertCollection(loans, DTOConverter::convertToLoanDTO, ArrayList::new);
    }

    @Override
    public List<LoanDTO> getUserLoan(Integer userId) {
        return List.of();
    }

    @Override
    public boolean deleteAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        if(loans.isEmpty()) {
            throw new LoanException(
                    new ErrorResponse(
                            ErrorCode.NL,
                            "No content"));
        }
        loanRepository.deleteAll(loans);
        return true;
    }

    @Override
    public boolean deleteLoanById(Integer idLoan) {
        return false;
    }

    @Override
    public List<LoanDetailDTO> getLoanDetailsByLoanId(Integer loanId) {
        return List.of();
    }

    @Override
    public LocalDate extendLoanDueDate(Integer loanId, LocalDate newDueDate) {
        return null;
    }

    @Override
    public void sendLoanReminders() {

    }

    @Override
    public List<LoanDTO> getOverdueLoans() {
        return List.of();
    }

    private void userExists(String email) {
        if(!userRepository.existsByEmail(email)) {
            throw new UserException(
                    (new ErrorResponse(
                            ErrorCode.EUN,
                            UNF_EMAIL + email)));
        }
    }
    private Book getBook(Integer bookId) {
       return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException(
                        new ErrorResponse(
                                ErrorCode.BNF,
                                BNF)));
    }
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EUN,
                                UNF_EMAIL + email)));
    }
}

