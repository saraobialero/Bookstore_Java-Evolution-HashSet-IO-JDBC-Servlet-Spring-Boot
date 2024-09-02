package org.evpro.bookshopV4.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

//POJO (Plain Old Java Objects) class
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book {
    private Integer id;
    private String title;
    private String author;
    private LocalDate publicationYear;
    private String description;
    private String ISBN;
    private int quantity;
    private boolean available;
}
