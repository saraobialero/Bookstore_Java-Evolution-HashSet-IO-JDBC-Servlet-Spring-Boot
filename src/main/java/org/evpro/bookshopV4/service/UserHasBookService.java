package org.evpro.bookshopV4.service;

import lombok.extern.slf4j.Slf4j;
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

import static org.evpro.bookshopV4.model.enums.CodeAndFormat.NC_CODE;
import static org.evpro.bookshopV4.model.enums.CodeAndFormat.NF_CODE;

@Slf4j
public class UserHasBookService implements UserHasBookFunctions {

    private UserHasBookDAO userHasBookDAO;
    private BookService bookService;
    private UserService userService;


    @Override
    public UserHasBook borrowBook(int userId, int bookId, int quantity) throws SQLException {
        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);
        bookService.checkQuantity(book, quantity);
        bookService.setQuantityDecrease(book, quantity);
        bookService.updateAvailability(book);

        return userHasBookDAO.findByUserIdAndBookId(userId, bookId)
                .map(existingUserHasBook -> {
                    existingUserHasBook.setQuantity(existingUserHasBook.getQuantity() + quantity);
                    userHasBookDAO.updateQuantity(existingUserHasBook.getId(), existingUserHasBook.getQuantity());
                    log.info("loan updated");
                    return existingUserHasBook;
                })
                .orElseGet(() -> {
                        log.info("loan created");
                        return initializeUserHasBook(user, book, quantity);
                });
    }

    @Override
    public List<UserHasBook> getBorrowsForUser(int userId) throws SQLException {
        userService.getUserById(userId);
        List<UserHasBook> userHasBooks = userHasBookDAO.findByUserId(userId);
        if(userHasBooks.isEmpty()) {
            throw new UserException(
                    (NC_CODE),
                    HttpStatusCode.NO_CONTENT);
        }
        return userHasBooks;
    }

    @Override
    public List<UserHasBook> getBorrowsForBook(int bookId) throws SQLException, BookException {
        bookService.getBookById(bookId);
        List<UserHasBook> userHasBooks = userHasBookDAO.findByUserId(bookId);
        if(userHasBooks.isEmpty()) {
            throw new BookException(
                    (NC_CODE),
                    HttpStatusCode.NO_CONTENT);
        }
        return userHasBooks;
    }

    @Override
    public boolean returnBorrow(int id) throws SQLException {
        UserHasBook userHasBook = getUserHasBook(id);
        userHasBook.setReturnDate(LocalDate.now());
        userHasBookDAO.updateReturnDate(id, LocalDate.now());
        log.info("Loan returned{}", userHasBook);
        return true;
    }


    private UserHasBook getUserHasBook(int id) {
        return userHasBookDAO.findById(id)
                .orElseThrow(()
                        -> new UserHasBookException(
                        ("Loan with id " + id + NF_CODE),
                        HttpStatusCode.NOT_FOUND));
    }
    private UserHasBook initializeUserHasBook(User user, Book book, int quantity) {
        UserHasBook userHasBook = new UserHasBook();
        userHasBook.setUserId(user.getId());
        userHasBook.setBookId(book.getId());
        userHasBook.setQuantity(quantity);
        userHasBook.setBorrowDate(LocalDate.now());
        userHasBook.setReturnDate(null);
        userHasBookDAO.save(userHasBook);
        return userHasBook;
    }

}
