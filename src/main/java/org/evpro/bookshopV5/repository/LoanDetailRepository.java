package org.evpro.bookshopV5.repository;


import org.evpro.bookshopV5.model.LoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetails, Integer> {
}
