package org.evpro.bookshopV4.service.functionality;

import org.evpro.bookshopV4.model.User;

import java.sql.SQLException;

public interface AuthenticationFunctions {
    boolean login(String email, String password) throws SQLException;
    boolean signup(User user) throws SQLException;
}
