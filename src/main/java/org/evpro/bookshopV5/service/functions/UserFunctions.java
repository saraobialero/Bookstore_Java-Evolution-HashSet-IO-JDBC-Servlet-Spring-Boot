package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.enums.RoleCode;

import java.util.List;

public interface UserFunctions {
    // User functions
    User getUserById(Integer userId);
    User getUserByEmail(String email);
    void updateUserProfile(Integer userId, User updatedUser);
    void changeUserPassword(Integer userId, String oldPassword, String newPassword);
    List<Loan> getUserLoanHistory(Integer userId);
    Cart getUserCart(Integer userId);

    // Admin functions
    User addNewUser(User user);
    List<User> getAllUsers();
    void updateUserRole(Integer userId, RoleCode newRole);
    void deactivateUser(Integer userId);
    void reactivateUser(Integer userId);
    long getTotalUserCount();
    List<User> getMostActiveUsers(int limit);
    List<User> getUsersWithOverdueLoans();
    void deleteUser(Integer userId);
    void resetUserPassword(Integer userId);
    List<User> searchUsers(String searchTerm);

}
