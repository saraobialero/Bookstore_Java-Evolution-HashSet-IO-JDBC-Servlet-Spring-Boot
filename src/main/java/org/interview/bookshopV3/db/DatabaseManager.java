package org.interview.bookshopV3.db;

import org.interview.bookshopV3.model.Book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Class created to manage principal CRUD operation to DB
public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "bookshop_db";
    private static final String USER = "root";
    private static final String PASSWORD = "^Dvx&5hFzH&s#i";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
    }

    public void initializeDatabase() {
        try {
            executeScript("schema.sql", URL);
            String dbUrl = URL + DB_NAME;
            executeScript("data.sql", dbUrl);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void executeScript(String scriptName, String url) throws SQLException {
        try(Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
            Statement stmt = conn.createStatement()) {

            for (String sql : readSqlFile(scriptName)) {
                 if(!sql.trim().isEmpty()) {
                     stmt.execute(sql);
                 }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readSqlFile(String fileName) throws IOException {
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore comments
                if (line.startsWith("--") || line.startsWith("//")) continue;

                sb.append(line);
                if (line.trim().endsWith(";")) {
                    sqlStatements.add(sb.toString());
                    sb.setLength(0);
                }
            }
        }

        return sqlStatements;
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