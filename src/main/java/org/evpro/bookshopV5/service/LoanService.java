package org.evpro.bookshopV5.service;


import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.exception.BookException;
import org.evpro.bookshopV5.exception.LoanException;
import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.DTO.request.AddItemToLoanRequest;
import org.evpro.bookshopV5.model.DTO.response.ErrorResponse;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDetailsDTO;
import org.evpro.bookshopV5.model.Loan;
import org.evpro.bookshopV5.model.LoanDetails;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
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

    @Transactional
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

        LoanDetails loanDetails = new LoanDetails();
        loanDetails.setLoan(loan);
        loanDetails.setBook(book);
        loanDetailRepository.save(loanDetails);

        loan.setLoanDetails(Set.of(loanDetails));

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
        User user = getUser(email);
        List<Loan> loans = user.getLoans();
        Loan loan = loans.isEmpty() ? null : loans.get(loans.size() - 1);
        return convertToLoanDTO(loan);
    }


    @Transactional
    @Override
    public LoanDTO returnLoan(Integer loanId, String email) {
        Loan loan = getLoan(loanId);
        User user = getUser(email);
        if(loan.getUser() != user) {
            throw new UserException(new ErrorResponse(ErrorCode.UAL));
        }
        checkLoanStatus(loan);
        checkLoanDate(loan);

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.CLOSED);

        loanRepository.save(loan);
        return convertToLoanDTO(loan);
    }

    @Override
    public List<LoanDTO> getMyActiveLoans(String email) {
        userExists(email);
        List<Loan> loans = loanRepository.findActiveLoansFrUsers(email);
        if (loans.isEmpty()) {
            throw new LoanException(
                  new ErrorResponse(
                       ErrorCode.NL
                    ));
        }
        return convertCollection(loans, DTOConverter::convertToLoanDTO, ArrayList::new);
    }

    @Override
    public List<LoanDTO> getAllLoans() {
        List<Loan> loans = getLoans();
        return convertCollection(loans, DTOConverter::convertToLoanDTO, ArrayList::new);
    }

    @Override
    public List<LoanDTO> getUserLoan(Integer userId) {
        User user = getUserById(userId);
        List<Loan> loans = user.getLoans();
        return convertCollection(loans, DTOConverter::convertToLoanDTO, ArrayList::new);
    }

    @Transactional
    @Override
    public boolean deleteAllLoans() {
        List<Loan> loans = getLoans();
        loanRepository.deleteAll(loans);
        return true;
    }

    @Transactional
    @Override
    public boolean deleteLoanById(Integer loanId) {
        Loan loan = getLoan(loanId);
        loanRepository.delete(loan);
        return true;
    }

    @Override
    public Set<LoanDetailsDTO> getLoanDetailsByLoanId(Integer loanId) {
        Loan loan = getLoan(loanId);
        Set<LoanDetails> loanDetails = loan.getLoanDetails();
        return convertCollection(loanDetails, DTOConverter::convertToLoanDetailsDTO, HashSet::new);
    }

    @Transactional
    @Override
    public LocalDate extendLoanDueDate(Integer loanId) {
        Loan loan = getLoan(loanId);
        LocalDate newDueDate = loan.getDueDate().plusDays(14);
        loan.setDueDate(newDueDate);
        loanRepository.save(loan);

        return newDueDate;
    }

    @Override
    public boolean sendLoanReminders() {
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(3); // Send reminders for loans due in 3 days

        List<Loan> loansToRemind = loanRepository.findByDueDateAndStatus(reminderDate, LoanStatus.ACTIVE);

        for (Loan loan : loansToRemind) {
            User user = loan.getUser();
            String userEmail = user.getEmail();
            String message = String.format("Dear %s, your loan for book(s) is due on %s. Please return it on time.",
                    user.getName(), loan.getDueDate());

            System.out.println("Reminder sent to " + userEmail + ": " + message);
        }
        return true;
    }

    @Override
    public List<LoanDTO> getOverdueLoans() {
        List<Loan> loans = loanRepository.findOverdueLoans();
        if(loans.isEmpty()) throw new LoanException(new ErrorResponse(ErrorCode.NL));
        return convertCollection(loans, DTOConverter::convertToLoanDTO, ArrayList::new);
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
    private void userExists(String email) {
        if(!userRepository.existsByEmail(email)) {
            throw new UserException(
                    (new ErrorResponse(
                            ErrorCode.EUN,
                            UNF_EMAIL + email)));
        }
    }
    private User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EUN,
                                UNF_ID + id)));
    }
    private Loan getLoan(Integer id) {
        return loanRepository.findLoanById(id)
                             .orElseThrow(() -> new LoanException(
                                                new ErrorResponse(
                                                        ErrorCode.LNF,
                                                        "Loan not found with id" + id)));
    }
    private void checkLoanStatus(Loan loan) {
        if(loan.getReturnDate() != null ||
                loan.getStatus() == LoanStatus.CLOSED) {
            throw new LoanException(
                    new ErrorResponse(
                            ErrorCode.LAR,
                            "Loan already returned or closed in date: " + loan.getReturnDate() + loan.getStatus()));
        }
    }
    private void checkLoanDate(Loan loan) {
        if(loan.getLoanDate().isAfter(LocalDate.now())) {
            throw new LoanException(
                    new ErrorResponse(
                            ErrorCode.LAR,
                            "Loan already expired in date: " + loan.getDueDate()));

        }
    }
    private List<Loan> getLoans() {
        List<Loan> loans = loanRepository.findAll();
        if(loans.isEmpty()) {
            throw new LoanException(
                    new ErrorResponse(
                            ErrorCode.NL,
                            "No content"));
        }
        return loans;
    }

}

