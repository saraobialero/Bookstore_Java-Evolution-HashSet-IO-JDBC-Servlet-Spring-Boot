package org.evpro.bookshopV4.service;

import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.service.functionality.AuthenticationFunctions;

import java.sql.SQLException;

public class AuthenticationService implements AuthenticationFunctions {
    @Override
    public boolean login(String email, String password) throws SQLException {
        return false;
    }

    @Override
    public boolean signup(User user) throws SQLException {
        return false;
    }
}
