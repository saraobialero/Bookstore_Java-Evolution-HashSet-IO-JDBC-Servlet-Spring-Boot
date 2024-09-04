package org.evpro.bookshopV4.service;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

import static org.evpro.bookshopV4.model.enums.ErrorCode.NC_CODE;
import static org.evpro.bookshopV4.model.enums.ErrorCode.NF_CODE;

@Slf4j
@AllArgsConstructor
public class BookService implements BookFunctions {

    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAOImplementation();
    }

    @Override
    public Book addBook(Book book) throws SQLException {
        return bookDAO.findByISBN(book.getISBN())
                .map(existingBook -> {
                    existingBook.setQuantity(existingBook.getQuantity() + book.getQuantity());
                    bookDAO.update(existingBook);
                    log.info("Updated quantity for existing book with ISBN: {}", existingBook.getISBN());
                    return existingBook;
                })
                .orElseGet(() -> {
                    bookDAO.save(book);
                    log.info("Added new book with ISBN: {}", book.getISBN());
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
        log.info("Added new books and updated existing books");
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
        if (booksFromAuthor.isEmpty()) {
            throw new BookException(
                    ("Books for the author " + author + NF_CODE),
                    HttpStatusCode.NOT_FOUND);
        }
        return booksFromAuthor;
    }

    @Override
    public List<Book> getAllBooks() throws SQLException {
        List<Book> allBooks = bookDAO.findAll();
        if (allBooks.isEmpty())
            throw new BookException(
                    (NC_CODE),
                    HttpStatusCode.NO_CONTENT);
        return allBooks;
    }

    @Override
    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> availableBooks = bookDAO.findByAvailability();
        if (availableBooks.isEmpty())
            throw new BookException(
                    (NC_CODE + "any available Book in the store"),
                    HttpStatusCode.NOT_FOUND);
        return availableBooks;
    }

    @Override
    public List<Book> getBooksByYearRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Book> booksByRange = bookDAO.findByDates(startDate, endDate);
        if (booksByRange.isEmpty())
            throw new BookException(
                    (NC_CODE + "There aren't any available Book for range selected"),
                    HttpStatusCode.NOT_FOUND);
        return booksByRange;
    }

    @Override
    public boolean updateBook(Book book) throws SQLException {
        book = getBookById(book.getId());
        bookDAO.update(book);
        log.info("Book updated{}", book);
        return true;
    }

    @Override
    public boolean updateBookAvailability(int id, boolean available) throws SQLException {
        Book book = getBookById(id);
        bookDAO.updateAvailability(book.getId(), available);
        log.info("Book availability updated{}", book.isAvailable());
        return true;
    }

    @Override
    public boolean deleteBookWithEntireQuantity(int id) throws SQLException {
        Book book = getBookById(id);
        bookDAO.deleteById(book.getId());
        log.info("Book deleted{}", book);
        return true;
    }

    @Override
    public boolean deleteAll() throws SQLException {
        List<Book> books = getAllBooks();
        if (books.isEmpty()) {
            throw new BookException(
                    (NC_CODE + "No Books in the store"),
                    HttpStatusCode.NO_CONTENT);
        }
        bookDAO.deleteAll();
        log.info("Books deleted{}",books);
        return true;
    }

    @Override
    public boolean increaseBookQuantity(int id, int quantity) throws SQLException {
        Book book = getBookById(id);
        book.setQuantity(book.getQuantity() + quantity);
        bookDAO.update(book);
        log.info("Book quantity increased{}", book.getQuantity());
        return true;
    }

    @Override
    public boolean decreaseBookQuantity(int id, int quantity) throws SQLException {
        Book book = getBookById(id);
        checkQuantity(book, quantity);
        setQuantityDecrease(book, quantity);
        updateAvailability(book);
        log.info("Book quantity decreased{}", book.getQuantity());
        return true;
    }

    protected void checkQuantity(Book book, int quantity) {
        if (quantity > book.getQuantity()) {
            throw new BookException(
                    ("There aren't enough books"),
                    HttpStatusCode.BAD_REQUEST
            );
        }
    }
    protected void setQuantityDecrease(Book book, int quantity) {
        book.setQuantity(book.getQuantity() - quantity);
        bookDAO.update(book);
    }
    protected void updateAvailability(Book book) {
        if (book.getQuantity() == 0) {
            book.setAvailable(false);
            bookDAO.update(book);
        }
    }

}
