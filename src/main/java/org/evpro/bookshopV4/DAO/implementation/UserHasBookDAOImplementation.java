package org.evpro.bookshopV4.DAO.implementation;

import org.evpro.bookshopV4.DAO.UserHasBookDAO;
import org.evpro.bookshopV4.model.UserHasBook;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserHasBookDAOImplementation implements UserHasBookDAO {
    @Override
    public void save(UserHasBook userHasBook) {

    }

    @Override
    public Optional<UserHasBook> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<UserHasBook> findByUserId(int userId) {
        return List.of();
    }

    @Override
    public List<UserHasBook> findByBookId(int bookId) {
        return List.of();
    }

    @Override
    public List<UserHasBook> findByRange(Date startDate, Date endDate) {
        return List.of();
    }

    @Override
    public List<UserHasBook> findAll() {
        return List.of();
    }

    @Override
    public List<UserHasBook> findReturned(Date returnDate) {
        return List.of();
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public void deleteAll() {

    }
}
