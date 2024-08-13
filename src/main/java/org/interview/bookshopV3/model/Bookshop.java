package org.interview.bookshopV3.model;

import lombok.*;
import org.interview.bookshopV3.db.DatabaseManager;

import java.io.*;
import java.sql.SQLException;
import java.util.HashSet;
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



}
