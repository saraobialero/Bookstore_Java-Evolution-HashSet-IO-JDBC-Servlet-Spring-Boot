package org.evpro.bookshopV5.data.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

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
