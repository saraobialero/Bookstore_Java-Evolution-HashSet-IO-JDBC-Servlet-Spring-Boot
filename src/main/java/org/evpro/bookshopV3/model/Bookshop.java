package org.evpro.bookshopV3.model;

import lombok.*;
import org.evpro.bookshopV3.exception.BookException;
import org.evpro.bookshopV3.db.DatabaseManager;
import org.evpro.bookshopV3.exception.ErrorResponse;

import java.io.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<PublicBookView> getPublicCatalog() throws SQLException {
        if (books.isEmpty()) {
            loadBooks();
        }
        return books.stream()
                .map(this::toPublicView)
                .collect(Collectors.toSet());
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

    private PublicBookView toPublicView(Book book) {
        return new PublicBookView(
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear(),
                book.getDescription(),
                book.getISBN()
        );
    }
}
