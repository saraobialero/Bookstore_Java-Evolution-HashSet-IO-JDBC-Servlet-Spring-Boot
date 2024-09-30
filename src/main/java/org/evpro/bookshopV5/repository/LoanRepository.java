package org.evpro.bookshopV5.repository;


import org.evpro.bookshopV5.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {
    @Query("SELECT l FROM Loan l WHERE l.id = :loanId")
    Optional<Loan> findLoanById(Integer loanId);

}
