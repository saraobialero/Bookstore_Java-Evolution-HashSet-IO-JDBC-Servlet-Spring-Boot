package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.DTO.request.AddUserRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateRoleRequest;
import org.evpro.bookshopV5.model.DTO.response.AuthenticationResponse;
import org.evpro.bookshopV5.model.DTO.response.CartDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.UserDTO;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.enums.RoleCode;

import java.util.List;
import java.util.Set;

public interface UserFunctions {
    // User functions
    UserDTO getUserById(Integer userId);
    UserDTO getUserByEmail(String email);
    UserDTO updateUserProfile(String email, String name, String surname);
    AuthenticationResponse changeEmail(String email, String password, String newEmail);
    boolean changeUserPassword(String email, String oldPassword, String newPassword, String confirmNewPassword);

    // Admin functions
    UserDTO addNewUser(AddUserRequest request);
    Set<UserDTO> addNewUsers(List<AddUserRequest> requests);
    Set<UserDTO> getAllUsers();
    UserDTO updateUserRole(Integer userId, UpdateRoleRequest request);
    boolean deactivateUser(Integer userId);
    boolean reactivateUser(Integer userId);
    long getTotalUserCount();
    Set<UserDTO> getMostActiveUsers(int limit);
    Set<UserDTO> getUsersWithOverdueLoans();
    String resetUserPassword(Integer userId);
    boolean deleteUser(Integer userId);
    boolean deleteAll();


}
