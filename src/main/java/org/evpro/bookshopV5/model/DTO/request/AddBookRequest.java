package org.evpro.bookshopV5.model.DTO.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.time.LocalDate;

@Data
public class AddBookRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Publication year is required")
    @Past(message = "Publication year must be in the past")
    private LocalDate publicationYear;

    @NotBlank(message = "Description is required")
    private String description;

    @Nullable
    private String award;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
            message = "Invalid ISBN format")
    private String ISBN;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be non-negative or 0")
    private Integer quantity;

    @NotNull(message = "Genre is required")
    private BookGenre genre;

    @NotNull(message = "available is required")
    private boolean available;

}
