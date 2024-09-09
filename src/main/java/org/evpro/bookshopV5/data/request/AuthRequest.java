package org.evpro.bookshopV5.data.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
