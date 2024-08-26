package org.interview.bookshopV3.db;

import org.interview.bookshopV3.model.Book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.sql.Date;
import java.util.*;

//Class created to manage principal CRUD operation to DB
public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "bookshop_db";
    private static final String USER = "root";
    private static final String PASSWORD = "^Dvx&5hFzH&s#i";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
    }

    //DB ENVIRONMENT
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


    //CRUD
    public Set<Book> getAllBooks() throws SQLException {
        Set<Book> books = new HashSet<>();
        String querySQL = "SELECT *" + " FROM books";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
             ResultSet resultSet = preparedStatement.executeQuery(querySQL)) {
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getDate("publication_year"),
                        resultSet.getString("description"),
                        resultSet.getString("isbn"),
                        resultSet.getBoolean("available")
                );
                books.add(book);
            }
        }
        return books;
    }
    public Optional<Book> getBookById(int id) throws SQLException {
        String querySQL = "SELECT *" + " FROM books WHERE id = ?";
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery(querySQL)) {
                if (resultSet.next()) {
                    return Optional.of(new Book(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getDate("publication_year"),
                            resultSet.getString("description"),
                            resultSet.getString("isbn"),
                            resultSet.getBoolean("available")
                    )
                );
              }
            }

        }
        return Optional.empty();
    }
    public boolean addBook(Book book) throws SQLException {
        String querySQL = "INSERT INTO books (title, author, publication_year, description, isbn, available) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, book.getTitle());
                preparedStatement.setString(2, book.getAuthor());
                preparedStatement.setDate(3, book.getPublicationYear());
                preparedStatement.setString(4, book.getDescription());
                preparedStatement.setString(5, book.getISBN());
                preparedStatement.setBoolean(6, book.isAvailable());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Set the generated ID on the book object
                        book.setId(generatedKeys.getInt(1));
                        return true;
                    } else {
                        throw new SQLException("Creating book failed, no ID obtained.");
                    }
                }
            }
        }
        return false;
    }
    public boolean deleteBook(int id) throws  SQLException {
        String querySQL = "DELETE FROM books WHERE id = ?";
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }
    public void updateBookAvailability(int id, boolean available) throws  SQLException {
        String querySQL = "UPDATE books SET available = ? WHERE id = ?";
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setBoolean(1, available);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
    public void updateBookTitle(int id, String title) throws  SQLException {
        String querySQL = "UPDATE books SET title = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
    public void updateBookAuthor(int id, String author) throws  SQLException {
        String querySQL = "UPDATE books SET author = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, author);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
    public void updateBookPublicationYear(int id, Date publicationYear) throws  SQLException {
        String querySQL = "UPDATE books SET publicationYear = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setDate(1, publicationYear);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
    public void updateBookDescription(int id, String description) throws  SQLException {
        String querySQL = "UPDATE books SET description = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, description);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
    public void updateBookISBN(int id, String ISBN) throws  SQLException {
        String querySQL = "UPDATE books SET ISBN = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, ISBN);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
}