package org.interview.bookshopV3;

import org.interview.bookshopV3.db.DatabaseManager;
import org.interview.bookshopV3.model.Book;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws SQLException {

        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        Set<Book> books =  dbManager.getAllBooks();
        System.out.println("Books:");
        books.forEach(System.out::println);

    }
}




