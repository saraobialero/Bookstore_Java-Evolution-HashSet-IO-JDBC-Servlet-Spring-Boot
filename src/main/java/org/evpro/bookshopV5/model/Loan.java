package org.evpro.bookshopV5.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evpro.bookshopV5.model.enums.LoanStatus;
import org.evpro.bookshopV5.model.enums.RoleCode;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@ManyToOne
    //@JoinColumn(name = "user_id", nullable = false)
    //private User user;

    //@ManyToOne
    //@JoinColumn(name = "book_id", nullable = false)
    //private Book book;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;




}


