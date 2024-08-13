package org.interview.bookshopV3.model;

import lombok.*;
import org.interview.bookshopV3.db.DatabaseManager;

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
    private DatabaseManager dbManager;

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

    //TODO: methods
    //GiveBook
    public boolean giveBook(int id) {
        return false;
    }

    //ReturnBook
    public boolean returnBook(int id) {
        return false;
    }

    //Found book by ISBN
    public Optional<Book> searchBookById(int id) {
        return Optional.of(null);
    }

    //verify if the book is available
    private boolean isAvailable (int id) {
        return false;
    }


}
