package org.evpro.bookshopV4.service;

import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.service.functionality.UserFunctions;

import java.sql.SQLException;
import java.util.List;

public class UserService implements UserFunctions {
    @Override
    public boolean addUserAdmin(User user) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteAllUsers() throws SQLException {
        return false;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        return null;
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        return null;
    }

    @Override
    public List<User> getUserByRole(User.UserRole role) throws SQLException {
        return List.of();
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return List.of();
    }

    @Override
    public void updateUserInfo(int id, String name, String surname) throws SQLException {

    }

    @Override
    public void changeUserRole(int id, User.UserRole userRole) throws SQLException {

    }

    @Override
    public void changePassword(int id, String email, String password) throws SQLException {

    }

    @Override
    public void changeEmail(int id, String email, String password) throws SQLException {

    }
}
