package org.evpro.bookshopV4.service;

import org.evpro.bookshopV4.service.functionality.BookFunctions;

import java.awt.print.Book;
import java.sql.SQLException;
import java.util.List;

public class BookService implements BookFunctions {
    @Override
    public boolean addBook(Book book) throws SQLException {
        return false;
    }

    @Override
    public Book getBookById(int id) throws SQLException {
        return null;
    }

    @Override
    public Book searchBookByTitle(String title) throws SQLException {
        return null;
    }

    @Override
    public Book searchBookByISBN(String ISBN) throws SQLException {
        return null;
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) throws SQLException {
        return List.of();
    }

    @Override
    public List<Book> getAvailableBooks(boolean available) throws SQLException {
        return List.of();
    }

    @Override
    public List<Book> getAllBooks() throws SQLException {
        return List.of();
    }

    @Override
    public boolean updateBook(Book book) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteBook(int id) throws SQLException {
        return false;
    }
}
