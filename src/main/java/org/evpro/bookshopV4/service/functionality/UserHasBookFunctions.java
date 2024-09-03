package org.evpro.bookshopV4.service.functionality;

import org.evpro.bookshopV4.model.UserHasBook;

import java.awt.print.Book;
import java.sql.SQLException;
import java.util.List;

public interface UserHasBookFunctions {
    List<Book> getBooksByUser(int userId) throws SQLException;
    boolean borrowBook(int userId, int bookId) throws SQLException;
    List<UserHasBook> getBorrowsForUser (int userId) throws SQLException;
    List<UserHasBook> getBorrowsForBook (int bookId) throws SQLException;
    boolean returnBorrow (int id) throws SQLException;

}
