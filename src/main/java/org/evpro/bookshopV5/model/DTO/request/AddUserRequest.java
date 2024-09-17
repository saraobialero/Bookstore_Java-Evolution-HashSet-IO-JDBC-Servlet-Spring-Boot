package org.evpro.bookshopV5.model.DTO.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddUserRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    //Pattern
    @NotBlank(message = "Email is required")
    private String email;

    //Pattern
    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Genre is required")
    private List<Role> role;


}
