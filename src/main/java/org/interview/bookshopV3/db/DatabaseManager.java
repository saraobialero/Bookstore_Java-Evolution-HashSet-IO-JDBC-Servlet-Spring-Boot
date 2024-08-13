package org.interview.bookshopV3.db;

import org.interview.bookshopV3.model.Book;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

//Class created to manage principal CRUD operation to DB
public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/bookstore";
    private static final String USER = "root";
    private static final String PASSWORD = "y^Dvx&5hFzH&s#i";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void initializeDatabase() {
        // Code to execute schema and data
    }

    public Set<Book> getAllBooks() throws SQLException {
        Set<Book> books = new HashSet<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDate("publication_year"),
                        rs.getString("description"),
                        rs.getString("isbn"),
                        rs.getBoolean("available")
                );
                books.add(book);
            }
        }
        return books;
    }

    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (id, title, author, publication_year, description, isbn, available) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, book.getId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setDate(4, book.getPublicationYear());
            pstmt.setString(5, book.getDescription());
            pstmt.setString(6, book.getISBN());
            pstmt.setBoolean(7, book.isAvailable());
            pstmt.executeUpdate();
        }
        return true;
    }


    // Altri metodi per updateBook, deleteBook, ecc.
}