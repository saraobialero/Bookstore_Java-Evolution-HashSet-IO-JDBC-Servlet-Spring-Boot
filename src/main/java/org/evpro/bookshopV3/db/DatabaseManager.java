package org.evpro.bookshopV3.db;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV3.exception.BookException;
import org.evpro.bookshopV3.exception.DatabaseException;
import org.evpro.bookshopV3.exception.ErrorResponse;
import org.evpro.bookshopV3.model.Book;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

//Class created to manage principal CRUD operation to DB
@Slf4j
public class DatabaseManager {


    //Create file for database properties and set details
    private static final String PROP_FILE_PATH = "src/main/resources/database.properties";
    private static String url;
    private static String dbName;
    private static String user;
    private static String password;

    //DB CONNECTION
    static {
        loadDatabaseProperties();
    }
    private static void loadDatabaseProperties() {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(PROP_FILE_PATH)) {
            prop.load(fis);

            url = prop.getProperty("db.url");
            dbName = prop.getProperty("db.name");
            user = prop.getProperty("db.user");

            // Prioritize environment variable for password if available
            password = System.getenv("DB_PASSWORD");
            if (password == null || password.trim().isEmpty()) {
                password = prop.getProperty("db.password");
            }

        } catch (IOException e) {
            log.error("Error loading database properties", e);
            throw new DatabaseException(new ErrorResponse(
                    "Database configuration error",
                    "Failed to load database properties: " + e.getMessage(),
                    500,
                    System.currentTimeMillis()
            ));
        }
    }
    public Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(url + dbName, user, password);
        } catch (SQLException e) {
            log.error("Error establishing database connection", e);
            throw new DatabaseException(new ErrorResponse(
                    "Database connection error",
                    "Failed to establish database connection: " + e.getMessage(),
                    500,
                    System.currentTimeMillis()
            ));
        }
    }

    //DB ENVIRONMENT
    public void initializeDatabase() {
        try {
            executeScript("schema.sql", url);
            String dbUrl = url + dbName;
            executeScript("data.sql", dbUrl);
        } catch (SQLException e) {
            log.error("Error initializing database", e);
            throw new DatabaseException(new ErrorResponse(
                    "Database initialization error",
                    "Failed to initialize database: " + e.getMessage(),
                    500,
                    System.currentTimeMillis()
            ));
        }
    }
    private void executeScript(String scriptName, String url) throws SQLException {
        try(Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement()) {

            for (String sql : readSqlFile(scriptName)) {
                 if(!sql.trim().isEmpty()) {
                     stmt.execute(sql);
                 }
            }

        } catch (SQLException | IOException e) {
            log.error("Error executing script", e);
            throw new DatabaseException(new ErrorResponse(
                    "Script error",
                    "Failed to execute or read script: " + e.getMessage(),
                    500,
                    System.currentTimeMillis()
            ));
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

        } catch (SQLException e) {
            log.error("Error book not found", e);
            throw new BookException(new ErrorResponse(
                    "Book not found",
                    "Failed to found book with id: " + id +  e.getMessage(),
                    404,
                    System.currentTimeMillis()
            ));
        }
        
        return Optional.empty();
    }
    public boolean addBook(Book book) throws SQLException {
        if (book == null || isInvalidBook(book)) {
            throw new IllegalArgumentException("Invalid book data");
        }

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
    public boolean deleteAll() throws  SQLException {
        String querySQL = "DELETE *" + " FROM books";
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows == 0;
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

    private boolean isInvalidBook(Book book) {
        return book.getTitle() == null || book.getTitle().isEmpty() ||
                book.getAuthor() == null || book.getAuthor().isEmpty() ||
                book.getPublicationYear() == null ||
                book.getISBN() == null || book.getISBN().isEmpty();
    }
}