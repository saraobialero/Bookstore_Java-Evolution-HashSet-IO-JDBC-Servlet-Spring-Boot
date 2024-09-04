package org.evpro.bookshopV4.service.functionality;

import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.model.UserHasBook;

import java.awt.print.Book;
import java.sql.SQLException;
import java.util.List;

public interface UserHasBookFunctions {
    UserHasBook borrowBook(int userId, int bookId, int quantity) throws SQLException;
    List<UserHasBook> getBorrowsForUser (int userId) throws SQLException;
    List<UserHasBook> getBorrowsForBook (int bookId) throws SQLException, BookException;
    boolean returnBorrow (int id) throws SQLException;

}
