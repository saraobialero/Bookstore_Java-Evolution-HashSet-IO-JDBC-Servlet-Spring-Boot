package org.evpro.bookshopV5.model.DTO.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.time.LocalDate;

@Data
public class UpdateBookRequest {
    private Integer id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Publication year is required")
    @Past(message = "Publication year must be in the past")
    private LocalDate publicationYear;

    private String description;

    private String award;

    @NotNull(message = "Genre is required")
    private BookGenre genre;

    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;

    private Boolean available;
}