
package org.evpro.bookshopV5.model.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateEmailRequest {
    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "Email is required")
    private String newEmail;
}
