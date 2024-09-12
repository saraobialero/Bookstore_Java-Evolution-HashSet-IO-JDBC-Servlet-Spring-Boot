package org.evpro.bookshopV5.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.evpro.bookshopV5.model.enums.LoanStatus;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "loans_details")
public class LoanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "quantity", nullable = false)
    private int quantity;




}


