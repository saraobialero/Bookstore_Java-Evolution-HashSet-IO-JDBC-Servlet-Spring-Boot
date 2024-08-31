package org.evpro.bookshopV4.model;

import lombok.*;

import java.sql.Date;

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
    private Date publicationYear;
    private String description;
    private String ISBN;
    private int quantity;
    private boolean available;
}
