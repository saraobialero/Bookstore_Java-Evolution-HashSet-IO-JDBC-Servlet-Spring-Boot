package org.evpro.bookshopV4.service;

import lombok.NoArgsConstructor;
import org.evpro.bookshopV4.DAO.BookDAO;
import org.evpro.bookshopV4.DAO.implementation.BookDAOImplementation;
import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.service.functionality.BookFunctions;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class BookService implements BookFunctions {

    private BookDAO bookDAO;
    private String NF_CODE = " Not found";

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public BookService() {
        this.bookDAO = new BookDAOImplementation(); // Or use dependency injection
    }

    @Override
    public Book addBook(Book book) throws SQLException {
        return bookDAO.findByISBN(book.getISBN())
                .map(existingBook -> {
                    existingBook.setQuantity(existingBook.getQuantity() + book.getQuantity());
                    bookDAO.update(existingBook);
                    return existingBook;
                })
                .orElseGet(() -> {
                    bookDAO.save(book);
                    return book;
        });
    }

    @Override
    public List<Book> addBooks(List<Book> books) throws SQLException {
        List<Book> processedBooks = new ArrayList<>();
        for (Book book : books) {
            Book processedBook = bookDAO.findByISBN(book.getISBN())
                    .map(existingBook -> {
                        existingBook.setQuantity(existingBook.getQuantity() + book.getQuantity());
                        bookDAO.update(existingBook);
                        return existingBook;
                    })
                    .orElse(book);
            processedBooks.add(processedBook);
        }
        bookDAO.saveBooks(processedBooks.stream()
                .filter(b -> b.getId() == null)
                .collect(Collectors.toList()));
        return processedBooks;
    }

    @Override
    public Book getBookById(int id) throws SQLException {
        return bookDAO.findById(id)
                .orElseThrow(() -> new BookException(
                        ("Book with id " + id + NF_CODE),
                        HttpStatusCode.NOT_FOUND));
    }

    @Override
    public Book getBookByISBN(String ISBN) throws SQLException {
        return bookDAO.findByISBN(ISBN)
                .orElseThrow(() -> new BookException(
                        ("Book with ISBN " + ISBN + NF_CODE),
                        HttpStatusCode.NOT_FOUND));
    }

    @Override
    public Book getBookByTitle(String title) throws SQLException {
        return bookDAO.findByTitle(title)
                .orElseThrow(() -> new BookException(
                        ("Book with ISBN " + title + NF_CODE),
                        HttpStatusCode.NOT_FOUND));
    }

    @Override
    public List<Book> getBooksByAuthor(String author) throws SQLException {
        List<Book> booksFromAuthor = bookDAO.findByAuthor(author);
        if (booksFromAuthor.isEmpty())
            throw new BookException(
                    ("Books for the author " + author + NF_CODE),
                    HttpStatusCode.NOT_FOUND);
        return booksFromAuthor;
    }

    @Override
    public List<Book> getAllBooks() throws SQLException {
        List<Book> allBooks = bookDAO.findAll();
        if (allBooks.isEmpty())
            throw new BookException(
                    ("There aren't any Books in the store"),
                    HttpStatusCode.NO_CONTENT);
        return allBooks;
    }

    @Override
    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> availableBooks = bookDAO.findByAvailability();
        if (availableBooks.isEmpty())
            throw new BookException(
                    ("There aren't any available Book in the store"),
                    HttpStatusCode.NOT_FOUND);
        return availableBooks;
    }

    @Override
    public List<Book> getBooksByYearRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Book> booksByRange = bookDAO.findByDates(startDate, endDate);
        if (booksByRange.isEmpty())
            throw new BookException(
                    ("There aren't any available Book for range selected"),
                    HttpStatusCode.NOT_FOUND);
        return booksByRange;
    }


    @Override
    public boolean updateBook(Book book) throws SQLException {
        book = bookDAO.findById(book.getId())
                .orElseThrow(() -> new BookException(
                        (NF_CODE),
                        HttpStatusCode.NOT_FOUND));
        bookDAO.update(book);
        return true;
    }

    @Override
    public boolean updateBookAvailability(int id, boolean available) throws SQLException {
        Book book = bookDAO.findById(id)
                .orElseThrow(() -> new BookException(
                        (NF_CODE),
                        HttpStatusCode.NOT_FOUND));
        bookDAO.updateAvailability(book.getId(), available);
        return true;
    }

    @Override
    public boolean deleteBookWithEntireQuantity(int id) throws SQLException {
        Book book = bookDAO.findById(id)
                .orElseThrow(() -> new BookException(
                        (NF_CODE),
                        HttpStatusCode.NOT_FOUND));
        bookDAO.deleteById(book.getId());
        return true;
    }

    @Override
    public boolean deleteAll() throws SQLException {
        List<Book> books = bookDAO.findAll();
        if (books.isEmpty()) {
            throw new BookException(
                    ("There aren't any Books in the store"),
                    HttpStatusCode.NO_CONTENT);
        }
        bookDAO.deleteAll();
        return true;
    }

    @Override
    public boolean increaseBookQuantity(int id, int quantity) throws SQLException {
        Book book = bookDAO.findById(id)
                .orElseThrow(() -> new BookException(
                        (NF_CODE),
                        HttpStatusCode.NOT_FOUND));
        book.setQuantity(book.getQuantity() + quantity);
        bookDAO.update(book);
        return true;
    }


    @Override
    public boolean decreaseBookQuantity(int id, int quantity) throws SQLException {
        Book book = bookDAO.findById(id)
                .orElseThrow(() -> new BookException(
                        (NF_CODE),
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
            bookDAO.updateAvailability(id, false);
        }
        return true;
    }

}
