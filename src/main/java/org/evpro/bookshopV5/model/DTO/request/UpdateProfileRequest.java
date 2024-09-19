package org.evpro.bookshopV5.model.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Name is required")
    private String newName;

    @NotBlank(message = "Surname is required")
    private String newSurname;
}
