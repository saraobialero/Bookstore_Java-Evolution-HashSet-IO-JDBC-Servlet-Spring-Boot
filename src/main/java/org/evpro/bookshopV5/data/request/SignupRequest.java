package org.evpro.bookshopV5.data.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
    private String surname;
}