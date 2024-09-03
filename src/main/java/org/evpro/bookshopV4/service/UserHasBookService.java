package org.evpro.bookshopV4.service;

import org.evpro.bookshopV4.model.UserHasBook;
import org.evpro.bookshopV4.service.functionality.UserHasBookFunctions;

import java.awt.print.Book;
import java.sql.SQLException;
import java.util.List;

public class UserHasBookService implements UserHasBookFunctions {

    @Override
    public List<Book> getBooksByUser(int userId) throws SQLException {
        return List.of();
    }

    @Override
    public boolean borrowBook(int userId, int bookId) throws SQLException {
        return false;
    }

    @Override
    public List<UserHasBook> getBorrowsForUser(int id) throws SQLException {
        return List.of();
    }

    @Override
    public List<UserHasBook> getBorrowsForBook(int bookId) throws SQLException {
        return List.of();
    }

    @Override
    public boolean returnBorrow(int id) throws SQLException {
        return false;
    }
}
