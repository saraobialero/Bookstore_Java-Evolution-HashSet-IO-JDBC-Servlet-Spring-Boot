package org.evpro.bookshopV4.service.functionality;

import org.evpro.bookshopV4.model.Book;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookFunctions {
    boolean addBook(Book book) throws SQLException;
    boolean addBooks(List<Book> books) throws SQLException;
    Book getBookById(int id) throws SQLException;
    Book getBookByISBN(String ISBN) throws SQLException;
    Book getBookByTitle(String title) throws SQLException;
    List<Book> getBooksByAuthor(String author) throws SQLException;
    List<Book> getAllBooks() throws SQLException;
    List<Book> getAvailableBooks() throws SQLException;
    List<Book> getBooksByCategory(String category) throws SQLException;
    List<Book> getBooksByYearRange(Date startYear, Date endYear) throws SQLException;
    List<Book> searchBooks(String keyword) throws SQLException;
    List<Book> getMostPopularBooks(int limit) throws SQLException;
    boolean updateBook(Book book) throws SQLException;
    boolean deleteBook(int id) throws SQLException;
    boolean increaseBookQuantity(int bookId, int quantity) throws SQLException;
    boolean decreaseBookQuantity(int bookId, int quantity) throws SQLException;
    List<Book> getBooksPaginated(int page, int pageSize) throws SQLException;
}