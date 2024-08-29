package org.evpro.bookshopV4.DAO.implementation;

import org.evpro.bookshopV4.DAO.UserDAO;
import org.evpro.bookshopV4.model.User;

import java.util.List;
import java.util.Optional;

public class UserDAOImplementation implements UserDAO {
    @Override
    public void save(User user) {

    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<User> findByRole(User.UserRole role) {
        return List.of();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public void deleteAll() {

    }
}
