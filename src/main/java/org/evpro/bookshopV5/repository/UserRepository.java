package org.evpro.bookshopV5.repository;


import org.evpro.bookshopV5.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN u.loans l GROUP BY u ORDER BY COUNT(l) DESC")
    List<User> findMostActiveUsers(Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.loans l WHERE l.returnDate > l.dueDate")
    List<User> findUsersWithOverdueLoans();
}
