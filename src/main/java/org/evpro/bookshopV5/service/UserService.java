package org.evpro.bookshopV5.service;


import org.evpro.bookshopV5.model.DTO.request.AddUserRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateRoleRequest;
import org.evpro.bookshopV5.model.DTO.response.CartDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.UserDTO;
import org.evpro.bookshopV5.model.DTO.response.ErrorResponse;
import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.CartRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.service.functions.UserFunctions;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserService implements UserFunctions {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;

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
    public UserDTO updateUserProfile(Integer userId, String newName, String newSurname) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EUN,
                                "User not found with id " + userId)));
        existingUser.setName(newName);
        existingUser.setSurname(newSurname);
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
    public UserDTO addNewUser(AddUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserException(new ErrorResponse(ErrorCode.EAE, "User with this email already exists"));
        }

        User newUser = initializeUserFromRequest(request);
        userRepository.save(newUser);
        return convertToUserDTO(newUser);
    }

    @Override
    public List<UserDTO> addNewUsers(List<AddUserRequest> requests) {
        return List.of();
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
    public UserDTO updateUserRole(Integer userId, UpdateRoleRequest request) {
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
    public boolean deleteAll() {
        return false;
    }

    @Override
    public String resetUserPassword(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(ErrorCode.EUN, "User not found with id " + userId)));

        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return newPassword;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private User initializeUserFromRequest(AddUserRequest request) {

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setName(request.getName());
        newUser.setSurname(request.getSurname());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRoles(request.getRole());
        newUser.setActive(request.isActive());
        return newUser;
    }
    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .roles(user.getRoles())
                .active(user.isActive())
                .build();
    }
}