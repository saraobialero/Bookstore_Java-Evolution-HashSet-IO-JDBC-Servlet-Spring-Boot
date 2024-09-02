package org.evpro.bookshopV4.service.functionality;

import org.evpro.bookshopV4.model.Book;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public interface BookFunctions {
    Book addBook(Book book) throws SQLException;
    List<Book> addBooks(List<Book> books) throws SQLException;
    Book getBookById(int id) throws SQLException;
    Book getBookByISBN(String ISBN) throws SQLException;
    Book getBookByTitle(String title) throws SQLException;
    List<Book> getBooksByAuthor(String author) throws SQLException;
    List<Book> getAllBooks() throws SQLException;
    List<Book> getAvailableBooks() throws SQLException;
    List<Book> getBooksByYearRange(LocalDate startYear, LocalDate endYear) throws SQLException;
    boolean updateBook(Book book) throws SQLException;
    boolean deleteBookWithEntireQuantity(int id) throws SQLException;
    boolean deleteAll() throws SQLException;
    boolean increaseBookQuantity(int bookId, int quantity) throws SQLException;
    boolean decreaseBookQuantity(int bookId, int quantity) throws SQLException;

}