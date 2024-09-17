package org.evpro.bookshopV5.service;


import org.evpro.bookshopV5.model.DTO.response.CartDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.UserDTO;
import org.evpro.bookshopV5.model.DTO.response.ErrorResponse;
import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.CartRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.service.functions.UserFunctions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserFunctions {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public UserService(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public UserDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EUN,
                                "User not found with id " + userId)));
        return convertToUserDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EUN,
                                "User not found with email " + email )));
        return convertToUserDTO(user);
    }

    @Override
    public UserDTO updateUserProfile(Integer userId, String name, String surname) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EUN,
                                "User not found with id " + userId)));
        existingUser.setName(name);
        existingUser.setSurname(surname);
        userRepository.save(existingUser);
        return convertToUserDTO(existingUser);
    }

    @Override
    public boolean changeEmail(Integer userId, String password, String newEmail) {
        return false;
    }

    @Override
    public boolean changeUserPassword(Integer userId, String oldPassword, String newPassword, String confirmNewPassword) {
        return false;
    }


    @Override
    public List<LoanDTO> getUserLoanHistory(Integer userId) {
        return List.of();
    }

    @Override
    public CartDTO getUserCart(Integer userId) {
        return null;
    }

    @Override
    public UserDTO addNewUser(User user) {
        user = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EAE,
                                "User already exists ")));
        userRepository.save(user);
        return convertToUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new UserException(
                  new ErrorResponse(
                            ErrorCode.NCU,
                            "No user found"));
        }
        return users.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUserRole(Integer userId, RoleCode newRole) {
        return null;
    }

    @Override
    public boolean deactivateUser(Integer userId) {
        return false;
    }

    @Override
    public boolean reactivateUser(Integer userId) {
        return false;
    }

    @Override
    public long getTotalUserCount() {
        return 0;
    }

    @Override
    public List<UserDTO> getMostActiveUsers(int limit) {
        return List.of();
    }

    @Override
    public List<UserDTO> getUsersWithOverdueLoans() {
        return List.of();
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return false;
    }

    @Override
    public boolean resetUserPassword(Integer userId) {
        return false;
    }

    @Override
    public List<UserDTO> searchUsers(String searchTerm) {
        return List.of();
    }

    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .build();
    }
}