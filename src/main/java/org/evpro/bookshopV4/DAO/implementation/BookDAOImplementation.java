package org.evpro.bookshopV4.DAO.implementation;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.BookDAO;
import org.evpro.bookshopV4.exception.DatabaseException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.utilities.ConnectionFactory;
import org.evpro.bookshopV4.utilities.TransactionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
public class BookDAOImplementation implements BookDAO {

    private static final String DB_ERROR = "Error with database connection";

    // SQL Queries
    private static final String INSERT_BOOK = "INSERT INTO books (title, author, publication_year, description, isbn, quantity, available) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, publication_year = ?, description = ?, ISBN = ?, quantity = ?, available = ? WHERE id = ?";
    private static final String UPDATE_AVAILABILITY = "UPDATE books SET available = ? WHERE id = ?";
    private static final String SELECT_BOOK_BY_ID = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE id = ?";
    private static final String SELECT_BOOK_BY_ISBN = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE ISBN = ?";
    private static final String SELECT_BOOK_BY_TITLE = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE title = ?";
    private static final String SELECT_BOOKS_BY_AUTHOR = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE author = ?";
    private static final String SELECT_AVAILABLE_BOOKS = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE available = 1";
    private static final String SELECT_BOOKS_BY_DATE_RANGE = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE publication_year BETWEEN ? AND ?";
    private static final String SELECT_ALL_BOOKS = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books";
    private static final String DELETE_BOOK_BY_ID = "DELETE FROM books WHERE id = ?";
    private static final String DELETE_ALL_BOOKS = "DELETE FROM books";

    @Override
    public void save(Book book) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS)) {
                    mapPreparedStatement(preparedStatement, book);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating book failed, no rows affected.");
                    }
                    generateId(preparedStatement, book);
                }
                log.info("Book saved successfully: {}", book.getTitle());
            });
        } catch (SQLException e) {
            handleSQLException(e, "Book with this ISBN already exists", "Error saving book");
        }
    }

    @Override
    public void update(Book book) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOK)) {
                    mapPreparedStatement(preparedStatement, book);
                    preparedStatement.setInt(8, book.getId());
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating book failed, no rows affected.");
                    }
                    log.info("Book updated successfully: {}", book.getTitle());
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error updating book", "Error updating book");
        }
    }

    @Override
    public void updateAvailability(int id, boolean available) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_AVAILABILITY)) {
                    preparedStatement.setBoolean(1, available);
                    preparedStatement.setInt(2, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating book availability failed, no rows affected.");
                    }
                    log.info("Book availability updated successfully for book id: {}", id);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error updating book availability", "Error updating book availability");
        }
    }

    @Override
    public void saveBooks(List<Book> books) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS)) {
                    for (Book book : books) {
                        mapPreparedStatement(preparedStatement, book);
                        preparedStatement.addBatch();
                    }
                    int[] affectedRows = preparedStatement.executeBatch();
                    if (IntStream.of(affectedRows).sum() == 0) {
                        throw new SQLException("Creating books failed, no rows affected.");
                    }
                    generateIds(preparedStatement, books);
                }
                log.info("{} Books saved successfully", books.size());
            });
        } catch (SQLException e) {
            handleSQLException(e, "One or more books with this ISBN already exist", "Error saving books");
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBook(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding book by ID");
        }
        return Optional.empty();
    }

    @Override
    public Optional<Book> findByISBN(String ISBN) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_ISBN)) {
            preparedStatement.setString(1, ISBN);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBook(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding book by ISBN");
        }
        return Optional.empty();
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_TITLE)) {
            preparedStatement.setString(1, title);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBook(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding book by title");
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOKS_BY_AUTHOR)) {
            preparedStatement.setString(1, author);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToBooks(resultSet);
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding books by author");
        }
        return new ArrayList<>();
    }

    @Override
    public List<Book> findByAvailability() {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVAILABLE_BOOKS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return mapResultSetToBooks(resultSet);
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding available books");
        }
        return new ArrayList<>();
    }

    @Override
    public List<Book> findByDates(LocalDate startYear, LocalDate endYear) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOKS_BY_DATE_RANGE)) {
            preparedStatement.setDate(1, Date.valueOf(startYear));
            preparedStatement.setDate(2, Date.valueOf(endYear));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToBooks(resultSet);
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding books by date range");
        }
        return new ArrayList<>();
    }

    @Override
    public List<Book> findAll() {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BOOKS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return mapResultSetToBooks(resultSet);
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding all books");
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOK_BY_ID)) {
                    preparedStatement.setInt(1, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Deleting book failed, no rows affected.");
                    }
                    log.info("Book with id {} deleted successfully", id);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error deleting book", "Error deleting book");
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_BOOKS)) {
                    int affectedRows = preparedStatement.executeUpdate();
                    log.info("{} books deleted successfully", affectedRows);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error deleting all books", "Error deleting all books");
        }
    }

    //Utilities methods
    private Optional<Book> mapResultSetToBook(ResultSet resultSet) throws SQLException {
        return Optional.of(new Book(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getDate("publication_year").toLocalDate(),
                resultSet.getString("description"),
                resultSet.getString("ISBN"),
                resultSet.getInt("quantity"),
                resultSet.getBoolean("available")
        ));
    }
    private List<Book> mapResultSetToBooks(ResultSet resultSet) throws SQLException {
        List<Book> books = new ArrayList<>();
        while (resultSet.next()) {
            mapResultSetToBook(resultSet).ifPresent(books::add);
        }
        return books;
    }
    private void mapPreparedStatement(PreparedStatement preparedStatement, Book book) throws SQLException {
        preparedStatement.setString(1, book.getTitle());
        preparedStatement.setString(2, book.getAuthor());
        preparedStatement.setDate(3, java.sql.Date.valueOf(book.getPublicationYear()));
        preparedStatement.setString(4, book.getDescription());
        preparedStatement.setString(5, book.getISBN());
        preparedStatement.setInt(6, book.getQuantity());
        preparedStatement.setBoolean(7, book.isAvailable());
    }
    private void generateId(PreparedStatement preparedStatement, Book book) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                book.setId(generatedKeys.getInt(1));
                log.info("Book saved with ID: {}", book.getId());
            } else {
                throw new SQLException("Creating book failed, no ID obtained.");
            }
        }
    }
    private void generateIds(PreparedStatement preparedStatement, List<Book> books) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            for (Book book : books) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating book failed, no ID obtained for a book.");
                }
            }
        }
    }
    private void handleSQLException(SQLException e, String conflictMessage, String errorMessage) {
        log.error(errorMessage, e);
        if (e instanceof SQLIntegrityConstraintViolationException) {
            throw new DatabaseException(conflictMessage, e.getMessage(), HttpStatusCode.CONFLICT);
        }
        throw new DatabaseException(errorMessage, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}