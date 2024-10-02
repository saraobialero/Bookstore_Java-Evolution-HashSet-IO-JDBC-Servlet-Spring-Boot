package org.evpro.bookshopV5.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.DTO.request.AddItemToLoanRequest;
import org.evpro.bookshopV5.model.DTO.request.LoanReturnRequest;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDetailsDTO;
import org.evpro.bookshopV5.model.DTO.response.SuccessResponse;
import org.evpro.bookshopV5.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookshop/v5/loans")
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<LoanDTO>> createLoan(@AuthenticationPrincipal String userEmail,
                                                               @RequestBody @Valid AddItemToLoanRequest addItemToLoanRequest) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.createDirectLoan(userEmail, addItemToLoanRequest)), HttpStatus.OK);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<List<LoanDTO>>> getMyLoans(@AuthenticationPrincipal String userEmail) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.getMyLoans(userEmail)), HttpStatus.OK);
    }

    @GetMapping("/last-loan")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<LoanDTO>> getMyLastLoans(@AuthenticationPrincipal String userEmail) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.getMyLastLoan(userEmail)), HttpStatus.OK);
    }

    @PutMapping("/return-loan")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<LoanDTO>> returnLoan(@AuthenticationPrincipal String userEmail,
                                                               @RequestBody @Valid LoanReturnRequest loanReturnRequest) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.returnLoan(loanReturnRequest.getIdLoan(), userEmail)), HttpStatus.OK);
    }

    @GetMapping("/history/active")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<List<LoanDTO>>> myActiveLoans(@AuthenticationPrincipal String userEmail) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.getMyActiveLoans(userEmail)), HttpStatus.OK);
    }


    @GetMapping("/details/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<List<LoanDTO>>> getAllLoans() {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.getAllLoans()), HttpStatus.OK);
    }

    @GetMapping("user/{userId}/details")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<List<LoanDTO>>> getLoans(@PathVariable("userId") Integer userId) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.getUserLoan(userId)), HttpStatus.OK);
    }

    @DeleteMapping("/delete/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deleteAll() {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.deleteAllLoans()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{loanId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deleteLoan(@PathVariable("loanId") Integer loanId) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.deleteLoanById(loanId)), HttpStatus.OK);
    }

    @GetMapping("/{loanId}/details")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Set<LoanDetailsDTO>>> getLoanDetailsByLoanId(@PathVariable("loanId") Integer loanId) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.getLoanDetailsByLoanId(loanId)), HttpStatus.OK);
    }

    @PutMapping("/{loanId}/extend")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<LocalDate>> extendLoan(@PathVariable("loanId") Integer loanId) {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.extendLoanDueDate(loanId)), HttpStatus.OK);
    }

    @PostMapping("/send-reminder")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> sendRemind() {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.sendLoanReminders()), HttpStatus.OK);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<List<LoanDTO>>> getOverdue() {
        return new ResponseEntity<>(new SuccessResponse<>(loanService.getOverdueLoans()), HttpStatus.OK);
    }











}
