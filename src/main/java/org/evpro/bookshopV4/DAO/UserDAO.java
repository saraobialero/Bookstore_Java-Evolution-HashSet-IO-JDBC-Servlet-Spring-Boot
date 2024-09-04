package org.evpro.bookshopV4.DAO;

import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    void save(User user);
    void saveUsers(List<User> users);
    void update(User user);
    void updateRole(int id, UserRole role);
    Optional<User> findById(int id);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findAll();
    void deleteById(int id);
    void deleteAll();
    boolean verifyPassword(int userId, String password);
}
