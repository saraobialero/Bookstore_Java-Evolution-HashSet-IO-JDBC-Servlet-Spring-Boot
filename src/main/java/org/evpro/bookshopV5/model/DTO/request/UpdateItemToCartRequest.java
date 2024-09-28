package org.evpro.bookshopV5.model.DTO.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateItemToCartRequest {
    @NotNull(message = "Item ID is required")
    @Positive(message = "Item ID must be a positive number")
    private Integer cartItemId;

    @NotNull(message = "New Quantity is required")
    @Min(value = 1, message = "New Quantity must be at least 1")
    private int newQuantity;
}