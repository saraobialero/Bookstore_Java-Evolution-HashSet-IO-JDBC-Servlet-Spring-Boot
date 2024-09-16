package org.evpro.bookshopV5.data.request;

import lombok.Data;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.time.LocalDate;

@Data
public class BookRequest {
    private String title;
    private String author;
    private LocalDate publicationYear;
    private String description;
    private String award;
    private BookGenre genre;
    private String ISBN;
    private int quantity;
    private boolean available;
}
