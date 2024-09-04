package org.evpro.bookshopV4.service;

import org.evpro.bookshopV4.exception.UserException;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.service.functionality.AuthenticationFunctions;
import org.evpro.bookshopV4.DAO.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.regex.Pattern;

@Slf4j
public class AuthenticationService implements AuthenticationFunctions {

    private final UserDAO userDAO;
    private final UserService userService;

    public AuthenticationService(UserDAO userDAO, UserService userService) {
        this.userDAO = userDAO;
        this.userService = userService;
    }

    @Override
    public boolean login(String email, String password) throws SQLException {
        User user = userService.getUserByEmail(email);
        if (user != null && verifyPassword(password, user.getPassword())) {
            log.info("User logged in successfully: {}", email);
            return true;
        }
        log.info("Login failed for user: {}", email);
        return false;
    }

    @Override
    public boolean signup(User user) throws SQLException {
        if (userDAO.findByEmail(user.getEmail()).isPresent()) {
            log.info("Signup failed. User already exists: {}", user.getEmail());
            return false;
        }

        user.setPassword(hashPassword(user.getPassword()));
        userDAO.save(user);
        log.info("User signed up successfully: {}", user.getEmail());
        return true;
    }

    @Override
    public void changePassword(int id, String currentPassword, String newPassword) throws SQLException {
        User user = userService.getUserById(id);
        if (verifyPassword(currentPassword, user.getPassword())) {
            if (isValidPassword(newPassword)) {
                String hashedNewPassword = hashPassword(newPassword);
                user.setPassword(hashedNewPassword);
                userDAO.update(user);
                log.info("Password changed for user: {}", user.getEmail());
            } else {
                throw new UserException("New password does not meet security requirements", HttpStatusCode.BAD_REQUEST);
            }
        } else {
            throw new UserException("Current password is incorrect", HttpStatusCode.UNAUTHORIZED);
        }
    }

    @Override
    public void changeEmail(int id, String newEmail, String password) throws SQLException {
        User user = userService.getUserById(id);
        if (verifyPassword(password, user.getPassword())) {
            if (isValidEmail(newEmail)) {
                user.setEmail(newEmail);
                userDAO.update(user);
                log.info("Email changed for user ID: {} to: {}", id, newEmail);
            } else {
                throw new UserException("Invalid email format", HttpStatusCode.BAD_REQUEST);
            }
        } else {
            throw new UserException("Password is incorrect", HttpStatusCode.UNAUTHORIZED);
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email).matches()) {
            return false;
        }

        String[] commonDomains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "proton.me"};
        String domain = email.substring(email.lastIndexOf("@") + 1);
        for (String commonDomain : commonDomains) {
            if (domain.equalsIgnoreCase(commonDomain)) {
                return true;
            }
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = !password.matches("[A-Za-z0-9 ]*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }


    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    private boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}