package org.evpro.bookshopV5.service;


import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.DTO.request.AddUserRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateRoleRequest;
import org.evpro.bookshopV5.model.DTO.response.*;
import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.CartRepository;
import org.evpro.bookshopV5.repository.RoleRepository;
import org.evpro.bookshopV5.repository.UserRepository;
import org.evpro.bookshopV5.service.functions.UserFunctions;
import org.evpro.bookshopV5.utils.DTOConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.evpro.bookshopV5.utils.CodeMessages.*;
import static org.evpro.bookshopV5.utils.DTOConverter.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserFunctions {


    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EUN,
                                UNF_ID + userId)));
        return convertToUserDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return convertToUserDTO(getUser(email));
    }

    @Override
    public UserDTO updateUserProfile(String email, String newName, String newSurname) {
        User existingUser = getUser(email);
        existingUser.setName(newName);
        existingUser.setSurname(newSurname);
        userRepository.save(existingUser);
        return convertToUserDTO(existingUser);
    }

    @Transactional
    @Override
    public UserDTO changeEmail(String email, String password, String newEmail) {
        User user = getUser(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(new ErrorResponse(ErrorCode.IVP, "Invalid password"));
        }

        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new UserException(new ErrorResponse(ErrorCode.EAE, "Email already in use"));
        }

        user.setEmail(newEmail);
        userRepository.save(user);
        return convertToUserDTO(user);
    }

    @Transactional
    @Override
    public boolean changeUserPassword(Integer userId, String oldPassword, String newPassword, String confirmNewPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(new ErrorResponse(ErrorCode.EUN, UNF_ID + userId)));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserException(new ErrorResponse(ErrorCode.IVP, "Invalid old password"));
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new UserException(new ErrorResponse(ErrorCode.PWM, "New passwords do not match"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public List<LoanDTO> getUserLoanHistory(String email) {
        User user = getUser(email);
        return convertCollection(user.getLoans(), DTOConverter::convertToLoanDTO, ArrayList::new);
    }


    @Override
    public CartDTO getUserCart(String email) {
        User user = getUser(email);
        return convertToCartDTO(user.getCart());
    }

    @Transactional
    @Override
    public UserDTO addNewUser(AddUserRequest request) {
        if (request == null) {
            throw new UserException(
                    new ErrorResponse(
                            ErrorCode.NCU,
                            "No user provided to add"));
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserException(new ErrorResponse(ErrorCode.EAE, "User with this email already exists"));
        }

        User newUser = initializeUserFromRequest(request);
        userRepository.save(newUser);
        return convertToUserDTO(newUser);
    }

    @Transactional
    @Override
    public Set<UserDTO> addNewUsers(List<AddUserRequest> requests) {
        if (requests.isEmpty()) {
            throw new UserException(
                    new ErrorResponse(
                            ErrorCode.NCU,
                            "No user provided to add"));
        }

        Set<UserDTO> addedUser = new HashSet<>();
        for (AddUserRequest request: requests) {
            Optional<User> existingUserOptional = userRepository.findByEmail(request.getEmail());
            if(existingUserOptional.isPresent()) {
                throw new UserException(
                      new ErrorResponse(
                                ErrorCode.EAE,
                                "User with this mail already exists "));
            }

            User newUser = initializeUserFromRequest(request);
            userRepository.save(newUser);
            UserDTO userDTO = convertToUserDTO(newUser);
            addedUser.add(userDTO);
        }
        return addedUser;
    }

    @Override
    public Set<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new UserException(
                  new ErrorResponse(
                            ErrorCode.NCU,
                            NUF));
        }
        return convertCollection(users, DTOConverter::convertToUserDTO, HashSet::new);
    }

    @Transactional
    @Override
    public UserDTO updateUserRole(Integer userId, UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(new ErrorResponse(ErrorCode.EUN, UNF_ID + userId)));

        user.getRoles().clear();

        for (RoleCode roleCode : request.getRoleCodes()) {
            Role role = roleRepository.findByRoleCode(roleCode)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleCode));
            user.getRoles().add(role);
        }

        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Transactional
    @Override
    public boolean deactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(new ErrorResponse(ErrorCode.EUN, UNF_ID + userId)));

        user.setActive(false);
        userRepository.save(user);
        return true;
    }

    @Transactional
    @Override
    public boolean reactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(new ErrorResponse(ErrorCode.EUN, UNF_ID + userId)));

        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public long getTotalUserCount() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new UserException(
                    new ErrorResponse(
                            ErrorCode.NCU,
                            "No user found"));
        }
        return users.size();
    }

    @Override
    public Set<UserDTO> getMostActiveUsers(int limit) {
        List<User> activeUsers = userRepository.findMostActiveUsers(PageRequest.of(0, limit));
        return convertCollection(activeUsers, DTOConverter::convertToUserDTO, HashSet::new);
    }

    @Override
    public Set<UserDTO> getUsersWithOverdueLoans() {
        List<User> usersWithOverdueLoans = userRepository.findUsersWithOverdueLoans();
        return convertCollection(usersWithOverdueLoans, DTOConverter::convertToUserDTO, HashSet::new);
    }

    @Transactional
    @Override
    public boolean deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(
                                ErrorCode.EUN,
                                UNF_ID + userId)));
        userRepository.delete(user);
        return true;
    }

    @Transactional
    @Override
    public boolean deleteAll() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new UserException(
                    new ErrorResponse(
                            ErrorCode.NCU,
                            "No user found"));
        }
        userRepository.deleteAll();
        return true;
    }

    @Override
    public String resetUserPassword(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        new ErrorResponse(ErrorCode.EUN, UNF_ID + userId)));

        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return newPassword;
    }
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UserException(
                                                     new ErrorResponse(
                                                            ErrorCode.EUN,
                                                            UNF_EMAIL + email)));
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
        User user = new User();
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(request.isActive());

        List<Role> roles = new ArrayList<>();
        for (RoleCode roleCode : request.getRoleCodes()) {
            Role role = roleRepository.findByRoleCode(roleCode)
                                       .orElseThrow(() -> new RuntimeException("Role not found: " + roleCode));
            roles.add(role);
        }

        user.setRoles(roles);
        return user;
    }
}