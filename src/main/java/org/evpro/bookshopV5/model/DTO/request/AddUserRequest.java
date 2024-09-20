package org.evpro.bookshopV5.model.DTO.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.enums.RoleCode;

import java.util.List;

@Data
public class AddUserRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
    private String password;

    @NotEmpty(message = "At least one role is required")
    private List<RoleCode> roleCodes;

    @NotNull(message = "active is required")
    private boolean active;


}
