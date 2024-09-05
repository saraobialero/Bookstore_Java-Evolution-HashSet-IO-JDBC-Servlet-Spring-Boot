package org.evpro.bookshopV4.service;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.UserDAO;
import org.evpro.bookshopV4.DAO.implementation.UserDAOImplementation;
import org.evpro.bookshopV4.exception.UserException;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.model.enums.UserRole;
import org.evpro.bookshopV4.service.functionality.UserFunctions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.evpro.bookshopV4.utilities.CodeMsg.NC_CODE;
import static org.evpro.bookshopV4.utilities.CodeMsg.NF_CODE;

@Slf4j
public class UserService implements UserFunctions {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImplementation();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User addUserAdmin(User user) throws SQLException {
        return (User) userDAO.findByEmail(user.getEmail())
                .map(existingUser -> {
                    log.info("User already exists: {}", existingUser.getEmail());
                    throw new UserException("User already exists", HttpStatusCode.CONFLICT);
                })
                .orElseGet(() -> {
                    userDAO.save(user);
                    log.info("Added new Admin User: {}", user.getEmail());
                    return user;
                });
    }

    @Override
    public List<User> addUsersAdmin(List<User> users) throws SQLException {
        List<User> processedUsers = new ArrayList<>();
        for (User user : users) {
            User processedUser = userDAO.findByEmail(user.getEmail())
                    .map(existingUser -> {
                        log.info("User with this email already exists: {}", existingUser.getEmail());
                        return existingUser;
                    })
                    .orElse(user);
            processedUsers.add(processedUser);
        }
        userDAO.saveUsers(processedUsers.stream()
                .filter(b -> b.getId() == null)
                .collect(Collectors.toList()));
        log.info("Added new users");
        return processedUsers;
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        User user = getUserById(id);
        userDAO.deleteById(user.getId());
        log.info("User deleted{}", user);
        return true;
    }

    @Override
    public boolean deleteAllUsers() throws SQLException {
        List<User> users = getAllUsers();
        userDAO.deleteAll();
        log.info("Users deleted{}", users);
        return true;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        return userDAO.findById(id)
                .orElseThrow(() -> new UserException(
                        ("User with id " + id + NF_CODE),
                        HttpStatusCode.NOT_FOUND));
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email)
                .orElseThrow(() -> new UserException(
                        ("User with email " + email + NF_CODE),
                        HttpStatusCode.NOT_FOUND));
    }

    @Override
    public List<User> getUserByRole(UserRole role) throws SQLException {
        List<User> usersByRole = userDAO.findByRole(role);
        if (usersByRole.isEmpty()) {
            throw new UserException(
                    ("Users for role " + role + NF_CODE),
                    HttpStatusCode.NOT_FOUND);
        }
        return usersByRole;
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            throw new UserException(
                    (NC_CODE),
                    HttpStatusCode.NO_CONTENT);
        }
        return users;
    }

    @Override
    public boolean updateUserRole(int id, UserRole userRole) throws SQLException {
        User user = getUserById(id);
        userDAO.updateRole(id, userRole);
        log.info("User{} role{} updated", user, userRole);
        return true;
    }

    @Override
    public boolean changePersonalInfo(int id, String name, String surname) throws SQLException {
        User user = getUserById(id);
        user.setName(name);
        user.setSurname(surname);
        userDAO.update(user);
        log.info("User{} name{} and surname{} updated", user, name, surname);
        return true;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        user = getUserById(user.getId());
        userDAO.update(user);
        log.info("User{} updated", user);
        return true;
    }

}
