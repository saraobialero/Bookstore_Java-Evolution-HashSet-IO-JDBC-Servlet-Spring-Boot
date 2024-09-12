package org.evpro.bookshopV5.model;

import jakarta.persistence.*;
import lombok.*;
import org.evpro.bookshopV5.model.enums.RoleCode;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "publication_year", nullable = false)
    private LocalDate publicationYear;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "award")
    private String award;

    @Column(name = "isbn", nullable = false)
    private String ISBN;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "available", nullable = false)
    private boolean available;

}


