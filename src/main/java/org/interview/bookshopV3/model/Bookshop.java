package org.interview.bookshopV3.model;

import lombok.*;
import org.interview.bookshopV3.db.DatabaseManager;
import org.interview.bookshopV3.exception.BookException;
import org.interview.bookshopV3.exception.ErrorResponse;

import java.io.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

//Class created to manage user functionalities
public class Bookshop implements Serializable {
    private Set<Book> books;
    private transient DatabaseManager dbManager;

    public Bookshop(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.books = new HashSet<>();
    }

    public void loadBooks() throws SQLException {
        this.books = new HashSet<>(dbManager.getAllBooks());
    }

    public boolean addBook(Book book) throws SQLException {
        if (dbManager.addBook(book)) {
            return books.add(book);
        }
        return false;
    }


    public boolean giveBook(int id, boolean available) throws SQLException {
        Book book = searchBookById(id);
        if (!book.isAvailable()) {
            return false;
        }
        dbManager.updateBookAvailability(id, available);
        return true;
    }

    public boolean returnBook(int id, boolean available) throws SQLException {
        Book book = searchBookById(id);
        if (book.isAvailable()) {
            return false;
        }
        dbManager.updateBookAvailability(id, available);
        return true;
    }

    public Book searchBookById(int id) throws SQLException {
        try {
            return dbManager.getBookById(id)
                    .orElseThrow(() -> new BookException(
                            new ErrorResponse(
                                    "Book not found",
                                    "No book found with ID: " + id,
                                    404,
                                    System.currentTimeMillis()
                            )
                    ));
        } catch (SQLException e) {
            throw new BookException(
                    new ErrorResponse(
                            "Database error",
                            "Error occurred while searching for book with ID: " + id,
                            500,
                            System.currentTimeMillis()
                    )
            );
        }
    }


}
