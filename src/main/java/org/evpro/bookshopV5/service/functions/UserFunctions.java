package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.data.DTO.CartDTO;
import org.evpro.bookshopV5.data.DTO.LoanDTO;
import org.evpro.bookshopV5.data.DTO.UserDTO;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.enums.RoleCode;

import java.util.List;

public interface UserFunctions {
    // User functions
    UserDTO getUserById(Integer userId);
    UserDTO getUserByEmail(String email);
    UserDTO updateUserProfile(Integer userId, String name, String
            surname);
    boolean changeEmail(Integer userId, String password, String newEmail);
    boolean changeUserPassword(Integer userId, String oldPassword, String newPassword, String confirmNewPassword);
    List<LoanDTO> getUserLoanHistory(Integer userId);
    CartDTO getUserCart(Integer userId);

    // Admin functions
    UserDTO addNewUser(User user);
    List<UserDTO> getAllUsers();
    UserDTO updateUserRole(Integer userId, RoleCode newRole);
    boolean deactivateUser(Integer userId);
    boolean reactivateUser(Integer userId);
    long getTotalUserCount();
    List<UserDTO> getMostActiveUsers(int limit);
    List<UserDTO> getUsersWithOverdueLoans();
    boolean deleteUser(Integer userId);
    boolean resetUserPassword(Integer userId);
    List<UserDTO> searchUsers(String searchTerm);

}
