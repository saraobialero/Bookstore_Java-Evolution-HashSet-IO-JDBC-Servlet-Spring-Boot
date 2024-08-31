package org.evpro.bookshopV4.DAO.implementation;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.BookDAO;
import org.evpro.bookshopV4.exception.DatabaseException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.utilities.ConnectionFactory;
import org.evpro.bookshopV4.utilities.TransactionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BookDAOImplementation implements BookDAO {

    private static final String DB_ERROR = "Error with database connection";

    @Override
    public void save(Book book) {
        String querySQL = "INSERT INTO books (title, author, publication_year, description, isbn, available) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL, Statement.RETURN_GENERATED_KEYS)) {
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
            log.error(DB_ERROR, e);
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new DatabaseException("Book with this ISBN already exists", e.getMessage(), HttpStatusCode.CONFLICT);
            }
            throw new DatabaseException(DB_ERROR, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void saveBooks(List<Book> books) {
        String querySQL = "INSERT INTO books (title, author, publication_year, description, isbn, available) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL, Statement.RETURN_GENERATED_KEYS)) {
                    for (Book book : books) {
                        mapPreparedStatement(preparedStatement, book);
                    }
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating book failed, no rows affected.");
                    }
                    generateIds(preparedStatement, books);
                }
                log.info("{} Books saved successfully", books);
            });
        } catch (SQLException e) {
            log.error(DB_ERROR, e);
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new DatabaseException("Book with this ISBN already exists", e.getMessage(), HttpStatusCode.CONFLICT);
            }
            throw new DatabaseException(DB_ERROR, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Optional<Book> findById(int id) throws SQLException {
        String querySQL = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBook(resultSet);
                }
            }
        } catch (SQLException e) {
            log.error(DB_ERROR, e);
            throw new DatabaseException(DB_ERROR, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Book> findByISBN(String ISBN) {
        String querySQL = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE ISBN = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, ISBN);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBook(resultSet);
                }
            }
        } catch (SQLException e) {
            log.error(DB_ERROR, e);
            throw new DatabaseException(DB_ERROR, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        String querySQL = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE title = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, title);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBook(resultSet);
                }
            }
        } catch (SQLException e) {
            log.error(DB_ERROR, e);
            throw new DatabaseException(DB_ERROR, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        String querySQL = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books WHERE author = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setString(1, author);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToBooks(resultSet);
            }
        } catch (SQLException e) {
            log.error(DB_ERROR, e);
            throw new DatabaseException(DB_ERROR, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Book> findAll() {
        String querySQL = "SELECT id, title, author, publication_year, description, ISBN, quantity, available FROM books";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToBooks(resultSet);
            }
        } catch (SQLException e) {
            log.error(DB_ERROR, e);
            throw new DatabaseException(DB_ERROR, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteById(int id) {
        String querySQL = "DELETE FROM books WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
                    preparedStatement.setInt(1, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Deleting book failed, no rows affected.");
                    }
                    log.info("Book with id {} deleted successfully", id);
                }
            });
        } catch (SQLException e) {
            log.error("Error deleting book with id {}", id, e);
            throw new DatabaseException("Error deleting book", e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteAll() {
        String querySQL = "DELETE FROM books";
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
                    int affectedRows = preparedStatement.executeUpdate();
                    log.info("{} books deleted successfully", affectedRows);
                }
            });
        } catch (SQLException e) {
            log.error("Error deleting all books", e);
            throw new DatabaseException("Error deleting all books", e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private Optional<Book> mapResultSetToBook(ResultSet resultSet) throws SQLException {
        return Optional.of(new Book(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getDate("publication_year"),
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
        preparedStatement.setDate(3, book.getPublicationYear());
        preparedStatement.setString(4, book.getDescription());
        preparedStatement.setString(5, book.getISBN());
        preparedStatement.setBoolean(6, book.isAvailable());
    }
    private void generateId(PreparedStatement preparedStatement, Book book) throws SQLException {
        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating book failed, no ID obtained.");
                }
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

}
