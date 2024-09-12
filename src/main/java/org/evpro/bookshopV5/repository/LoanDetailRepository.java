package org.evpro.bookshopV5.repository;


import org.evpro.bookshopV5.model.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Integer> {
}
