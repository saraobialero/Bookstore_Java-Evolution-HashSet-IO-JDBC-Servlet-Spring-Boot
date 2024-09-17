package org.evpro.bookshopV5.model.DTO.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
