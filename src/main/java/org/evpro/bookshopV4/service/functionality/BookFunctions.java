package org.evpro.bookshopV4.service.functionality;

import java.awt.print.Book;
import java.sql.SQLException;
import java.util.List;

public interface BookFunctions {
    boolean addBook(Book book) throws SQLException;
    Book getBookById(int id) throws SQLException;
    Book searchBookByTitle(String title) throws SQLException;
    Book searchBookByISBN(String ISBN) throws SQLException;
    List<Book> searchBooksByAuthor(String author) throws SQLException;
    List<Book> getAvailableBooks(boolean available) throws SQLException;
    List<Book> getAllBooks() throws SQLException;
    boolean updateBook(Book book) throws SQLException;
    boolean deleteBook(int id) throws SQLException;

}
