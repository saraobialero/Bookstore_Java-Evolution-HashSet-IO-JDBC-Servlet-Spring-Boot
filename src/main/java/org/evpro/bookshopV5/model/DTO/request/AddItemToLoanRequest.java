package org.evpro.bookshopV5.model.DTO.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddItemToLoanRequest {
    @NotNull(message = "Book ID is required")
    @Positive(message = "Book ID must be a positive number")
    private Integer bookId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}