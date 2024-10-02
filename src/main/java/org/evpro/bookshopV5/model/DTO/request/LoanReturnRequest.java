package org.evpro.bookshopV5.model.DTO.request;


import jakarta.validation.constraints.*;
import lombok.Data;



@Data
public class LoanReturnRequest {

    @NotNull(message = "Id is required")
    @Min(value = 1, message = "id must be non-negative or 0")
    private Integer idLoan;

}
