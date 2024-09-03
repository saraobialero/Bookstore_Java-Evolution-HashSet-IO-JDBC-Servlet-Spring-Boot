package org.evpro.bookshopV4.service.functionality;

import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.UserRole;

import java.sql.SQLException;
import java.util.List;

public interface UserFunctions {
    //Administration functionality
    boolean addUserAdmin(User user) throws SQLException;
    boolean deleteUser(int id) throws SQLException;
    boolean deleteAllUsers() throws SQLException;
    User getUserById(int id) throws SQLException;
    User getUserByEmail(String email) throws SQLException;
    List<User> getUserByRole(UserRole role) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    boolean changeUserRole(int id, UserRole userRole) throws SQLException;
    boolean updateUser(User user) throws SQLException;

    //User basic functionality
    void updateUserInfo(int id, String name, String surname) throws SQLException;
    void changePassword(int id, String email, String password) throws SQLException;
    void changeEmail(int id, String email, String password) throws SQLException;

}
