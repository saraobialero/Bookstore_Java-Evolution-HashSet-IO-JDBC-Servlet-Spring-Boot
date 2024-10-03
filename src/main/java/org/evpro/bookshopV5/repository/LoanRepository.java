package org.evpro.bookshopV5.repository;


import org.evpro.bookshopV5.model.Loan;
import org.evpro.bookshopV5.model.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {
    @Query("SELECT l FROM Loan l WHERE l.id = :loanId")
    Optional<Loan> findLoanById(Integer loanId);

    @Query("SELECT l FROM Loan l WHERE l.user.email = :email AND l.status = ACTIVE ORDER BY l.dueDate DESC")
    List<Loan> findActiveLoansFrUsers(@Param("email") String email);

    @Query("SELECT l FROM Loan l WHERE l.dueDate < CURRENT_DATE AND l.returnDate IS NULL ORDER BY l.dueDate ASC")
    List<Loan> findOverdueLoans();

    List<Loan> findByDueDateAndStatus(LocalDate dueDate, LoanStatus status);

    boolean existsByReturnDateIsNull();
}
