package org.evpro.bookshopV4.model;

import lombok.*;
import org.evpro.bookshopV4.model.enums.UserRole;

import java.time.LocalDate;

//POJO (Plain Old Java Objects) class
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserRole role;
    private LocalDate createdAt;


}
