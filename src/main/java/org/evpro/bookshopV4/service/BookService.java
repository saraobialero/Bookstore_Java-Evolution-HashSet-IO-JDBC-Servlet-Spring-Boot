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
import java.util.List;
import java.util.Optional;


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
        Optional<Book> existingBook = bookDAO.findByISBN(book.getISBN());
        if (existingBook.isPresent()) {
            increaseBookQuantity(existingBook.get().getId(), existingBook.get().getQuantity());
            bookDAO.update(existingBook.get());
            return existingBook.get();
        }
        bookDAO.save(book);
        return book;
    }

    @Override
    public List<Book> addBooks(List<Book> books) throws SQLException {
        for (Book book : books) {
            Optional<Book> existingBookOpt = bookDAO.findByISBN(book.getISBN());
            if (existingBookOpt.isPresent()) {
                Book existingBook = existingBookOpt.get();
                increaseBookQuantity(existingBook.getId(), existingBook.getQuantity());
                bookDAO.update(existingBook);
            }
            books.add(book);
            bookDAO.saveBooks(books);
        }
        return books;
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
        if(allBooks.isEmpty())
            throw new BookException(
                    ("There aren't any Books in the store"),
                    HttpStatusCode.NO_CONTENT);
        return allBooks;
    }

    @Override
    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> availableBooks = bookDAO.findByAvailability();
        if(availableBooks.isEmpty())
            throw  new BookException(
                    ("There aren't any available Book in the store"),
                    HttpStatusCode.NOT_FOUND);
        return availableBooks;
    }

    @Override
    public List<Book> getBooksByYearRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Book> booksByRange = bookDAO.findByDates(startDate, endDate);
        if(booksByRange.isEmpty())
            throw  new BookException(
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
    public boolean deleteBookWithEntireQuantity(int id) throws SQLException {
        Optional<Book> existingBook = bookDAO.findById(id);
        if (existingBook.isPresent()) {
            bookDAO.deleteById(id);
            return true;
        }
        return false;
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
        Optional<Book> optionalBook = bookDAO.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setQuantity(book.getQuantity() + quantity);
            bookDAO.update(book);
            return true;
        }
        return false;
    }

    @Override
    public boolean decreaseBookQuantity(int id, int quantity) throws SQLException {
        Optional<Book> optionalBook = bookDAO.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (quantity > book.getQuantity()) {
                throw new BookException(
                        ("There aren't enough books"),
                        HttpStatusCode.BAD_REQUEST
                );
            }
            book.setQuantity(book.getQuantity() - quantity);
            bookDAO.update(book);
            if(book.getQuantity() == 0) bookDAO.deleteById(id);
            return true;
        }
        return false;
    }

}
