package org.evpro.bookshopV4.service;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.BookDAO;
import org.evpro.bookshopV4.DAO.UserDAO;
import org.evpro.bookshopV4.DAO.UserHasBookDAO;
import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.exception.UserException;
import org.evpro.bookshopV4.exception.UserHasBookException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.UserHasBook;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.service.functionality.UserHasBookFunctions;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
public class UserHasBookService implements UserHasBookFunctions {

    private BookDAO bookDAO;
    private UserDAO userDAO;
    private UserHasBookDAO userHasBookDAO;
    private final String NF_CODE = " Not found";


    @Override
    public UserHasBook borrowBook(int userId, int bookId, int quantity) throws SQLException {
        User user = userDAO.findById(userId).orElseThrow(()
                -> new UserException(
                ("User with id " + userId + NF_CODE),
                HttpStatusCode.NOT_FOUND));

        Book book = bookDAO.findById(bookId).orElseThrow(()
                -> new BookException(
                ("Book with id " + bookId + NF_CODE),
                HttpStatusCode.NOT_FOUND));

        if (quantity > book.getQuantity()) {
            throw new BookException(
                    ("There aren't enough books"),
                    HttpStatusCode.BAD_REQUEST
            );
        }

        book.setQuantity(book.getQuantity() - quantity);
        bookDAO.update(book);
        if (book.getQuantity() == 0) {
            bookDAO.updateAvailability(bookId, false);
        }

        return userHasBookDAO.findByUserIdAndBookId(userId, bookId)
                .map(existingUserHasBook -> {
                    existingUserHasBook.setQuantity(existingUserHasBook.getQuantity() + quantity);
                    userHasBookDAO.updateQuantity(existingUserHasBook.getId(), quantity);
                    return existingUserHasBook;
                })
                .orElseGet(() -> {
                    UserHasBook userHasBook = new UserHasBook();
                    userHasBook.setUserId(user.getId());
                    userHasBook.setBookId(book.getId());
                    userHasBook.setQuantity(quantity);
                    userHasBook.setBorrowDate(LocalDate.now());
                    userHasBook.setReturnDate(null);
                    userHasBookDAO.save(userHasBook);
                    return userHasBook;
                });


    }

    @Override
    public List<UserHasBook> getBorrowsForUser(int userId) throws SQLException {
        userDAO.findById(userId).orElseThrow(()
                -> new UserException(
                ("User with id " + userId + NF_CODE),
                HttpStatusCode.NOT_FOUND));

        List<UserHasBook> userHasBooks = userHasBookDAO.findByUserId(userId);
        if(userHasBooks.isEmpty()) {
            throw new UserException(
                    ("User haven't any books "),
                    HttpStatusCode.NO_CONTENT);
        }
        return userHasBooks;
    }

    @Override
    public List<UserHasBook> getBorrowsForBook(int bookId) throws SQLException {
        bookDAO.findById(bookId).orElseThrow(()
                -> new UserException(
                ("Book with id " + bookId + NF_CODE),
                HttpStatusCode.NOT_FOUND));

        List<UserHasBook> userHasBooks = userHasBookDAO.findByUserId(bookId);
        if(userHasBooks.isEmpty()) {
            throw new BookException(
                    ("Book haven't any borrow "),
                    HttpStatusCode.NO_CONTENT);
        }
        return userHasBooks;
    }

    @Override
    public boolean returnBorrow(int id) throws SQLException {
        UserHasBook userHasBook = userHasBookDAO.findById(id)
                                                .orElseThrow(()
                                                -> new UserHasBookException(
                                                ("Loan with id " + id + NF_CODE),
                                                HttpStatusCode.NOT_FOUND));
        userHasBook.setReturnDate(LocalDate.now());
        userHasBookDAO.updateReturnDate(id, LocalDate.now());
        return true;
    }
}
