package org.evpro.bookshopV4.service;

import org.evpro.bookshopV4.DAO.BookDAO;
import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.service.functionality.BookFunctions;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BookService implements BookFunctions {

    private BookDAO bookDAO;
    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Override
    public boolean addBook(Book book) throws SQLException {
        Optional<Book> existingBook = Optional.of(getBookByISBN(book.getISBN()));
        if (existingBook.isPresent()) {
            increaseBookQuantity(existingBook.get().getId(), existingBook.get().getQuantity());
            bookDAO.update(existingBook.get());
            return false;
        }
        bookDAO.save(book);
        return true;
    }

    @Override
    public boolean addBooks(List<Book> books) throws SQLException {
        return false;
    }

    @Override
    public Book getBookById(int id) throws SQLException {
        return bookDAO.findById(id)
                .orElseThrow(() -> new BookException(
                        ("Book with id " + id + " Not found"),
                        HttpStatusCode.NOT_FOUND));
    }

    @Override
    public Book getBookByISBN(String ISBN) throws SQLException {
        return bookDAO.findByISBN(ISBN)
                      .orElseThrow(() -> new BookException(
                                        ("Book with ISBN " + ISBN + " Not found"),
                                        HttpStatusCode.NOT_FOUND));
    }

    @Override
    public Book getBookByTitle(String title) throws SQLException {
        return bookDAO.findByTitle(title)
                .orElseThrow(() -> new BookException(
                        ("Book with ISBN " + title + " Not found"),
                        HttpStatusCode.NOT_FOUND));
    }

    @Override
    public List<Book> getBooksByAuthor(String author) throws SQLException {
        return List.of();
    }

    @Override
    public List<Book> getAllBooks() throws SQLException {
        return List.of();
    }

    @Override
    public List<Book> getAvailableBooks() throws SQLException {
        return List.of();
    }

    @Override
    public List<Book> getBooksByCategory(String category) throws SQLException {
        return List.of();
    }

    @Override
    public List<Book> getBooksByYearRange(Date startYear, Date endYear) throws SQLException {
        return List.of();
    }

    @Override
    public List<Book> searchBooks(String keyword) throws SQLException {
        return List.of();
    }

    @Override
    public List<Book> getMostPopularBooks(int limit) throws SQLException {
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

    @Override
    public boolean increaseBookQuantity(int bookId, int quantity) throws SQLException {
        Optional<Book> optionalBook = bookDAO.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setQuantity(book.getQuantity() + quantity);
            bookDAO.update(book);
            return true;
        }
        return false;
    }

    @Override
    public boolean decreaseBookQuantity(int bookId, int quantity) throws SQLException {
        return false;
    }

    @Override
    public List<Book> getBooksPaginated(int page, int pageSize) throws SQLException {
        return List.of();
    }
}
