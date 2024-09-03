package org.evpro.bookshopV4.DAO.implementation;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.UserHasBookDAO;
import org.evpro.bookshopV4.exception.DatabaseException;
import org.evpro.bookshopV4.model.UserHasBook;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.utilities.ConnectionFactory;
import org.evpro.bookshopV4.utilities.TransactionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserHasBookDAOImplementation implements UserHasBookDAO {

    private static final String DB_ERROR = "Error with database connection";

    //SQL Queries
    private static final String INSERT_USERHASBOOK = "INSERT INTO users_has_books (user_id, book_id, quantity, borrow_date, return_date) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUANTITY = "UPDATE users_has_books SET quantity = ? WHERE id = ?";
    private static final String UPDATE_RETURN_DATE = "UPDATE users_has_books SET return_date = ? WHERE id = ?";
    private static final String SELECT_USERHASBOOK_BY_ID = "SELECT id, user_id, book_id, quantity, borrow_date, return_date FROM users_has_books WHERE id = ?";
    private static final String SELECT_USERHASBOOK_BY_USERID = "SELECT id, user_id, book_id, quantity, borrow_date, return_date FROM users_has_books WHERE user_id = ?";
    private static final String SELECT_USERHASBOOK_BY_BOOKID = "SELECT id, user_id, book_id, quantity, borrow_date, return_date FROM users_has_books WHERE book_id = ?";
    private static final String SELECT_USERHASBOOK_BY_QUANTITY = "SELECT id, user_id, book_id, quantity, borrow_date, return_date FROM users_has_books WHERE quantity = ?";
    private static final String SELECT_USERHASBOOK_BY_DATES = "SELECT id, user_id, book_id, quantity, borrow_date, return_date FROM users_has_books WHERE borrow_date ? BETWEEN ? AND ?";
    private static final String SELECT_ALL_USERSHASBOOKS = "SELECT id, user_id, book_id, quantity, borrow_date, return_date FROM users_has_books";
    private static final String SELECT_USERHASBOOK_BY_RETURNED= "SELECT id, user_id, book_id, quantity, borrow_date, return_date FROM users_has_books WHERE return_date IS NOT NUL";
    private static final String DELETE_USERHASBOOK_ID = "DELETE FROM users_has_books WHERE id = ?";
    private static final String DELETE_ALL_USERHASBOOKS = "DELETE FROM users_has_books";


    @Override
    public void save(UserHasBook userHasBook) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERHASBOOK, Statement.RETURN_GENERATED_KEYS)) {
                    mapPreparedStatement(preparedStatement, userHasBook);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating loan failed, no rows affected.");
                    }
                    generateId(preparedStatement, userHasBook);
                }
                log.info("Loan saved successfully: {}", userHasBook.getId());
            });
        } catch (SQLException e) {
            handleSQLException(e, "Loan with this ids already exists", "Error saving loan");
        }
    }

    @Override
    public void updateQuantity(int id, int quantity) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUANTITY)) {
                    preparedStatement.setInt(1, quantity);
                    preparedStatement.setInt(2, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating loan quantity failed, no rows affected.");
                    }
                    log.info("Loan quantity updated successfully for book id: {}", id);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error updating loan quantity", "Error updating loan quantity");
        }
    }

    @Override
    public void updateReturnDate(int id, LocalDate returnDate) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUANTITY)) {
                    preparedStatement.setDate(1, Date.valueOf(returnDate));
                    preparedStatement.setInt(2, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating loan date failed, no rows affected.");
                    }
                    log.info("Loan date updated successfully for book id: {}", id);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error updating loan date", "Error updating loan date");
        }
    }

    @Override
    public Optional<UserHasBook> findById(int id) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERHASBOOK_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUserHasBook(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding loan by ID");
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserHasBook> findByUserIdAndBookId(int userId, int bookId) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERHASBOOK_BY_ID)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, bookId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUserHasBook(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding loan by IDs" + userId + bookId);
        }
        return Optional.empty();
    }

    @Override
    public List<UserHasBook> findByUserId(int userId) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERHASBOOK_BY_USERID)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToUserHasBooks(resultSet);
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding loans by userId" + userId);
        }
        return new ArrayList<>();
    }

    @Override
    public List<UserHasBook> findByBookId(int bookId) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERHASBOOK_BY_BOOKID)) {
            preparedStatement.setInt(1, bookId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToUserHasBooks(resultSet);
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding loans by bookId" + bookId);
        }
        return new ArrayList<>();
    }

    @Override
    public List<UserHasBook> findByQuantity(int quantity) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERHASBOOK_BY_QUANTITY)) {
            preparedStatement.setInt(1, quantity);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToUserHasBooks(resultSet);
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding loans with this quantity " + quantity);
        }
        return new ArrayList<>();
    }

    @Override
    public List<UserHasBook> findByRange(LocalDate startDate, LocalDate endDate) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERHASBOOK_BY_DATES)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToUserHasBooks(resultSet);
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding loans by date range");
        }
        return new ArrayList<>();
    }

    @Override
    public List<UserHasBook> findAll() {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERSHASBOOKS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return mapResultSetToUserHasBooks(resultSet);
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding all loans");
        }
        return new ArrayList<>();
    }

    @Override
    public List<UserHasBook> findReturned(LocalDate returnDate) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERHASBOOK_BY_RETURNED)) {
            preparedStatement.setDate(1, Date.valueOf(returnDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToUserHasBooks(resultSet);
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding loans returned " + returnDate);
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USERHASBOOK_ID)) {
                    preparedStatement.setInt(1, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Deleting loan failed, no rows affected.");
                    }
                    log.info("Loan with id {} deleted successfully", id);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error deleting loan", "Error deleting loan");
        }

    }

    @Override
    public void deleteAll() {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_USERHASBOOKS)) {
                    int affectedRows = preparedStatement.executeUpdate();
                    log.info("{} loans deleted successfully", affectedRows);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error deleting all loans", "Error deleting all loans");
        }
    }

    private void generateId(PreparedStatement preparedStatement, UserHasBook userHasBook) {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                userHasBook.setId(generatedKeys.getInt(1));
                log.info("Loan saved with ID: {}", userHasBook.getId());
            } else {
                throw new SQLException("Creating loan failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void mapPreparedStatement(PreparedStatement preparedStatement, UserHasBook userHasBook) throws SQLException {
        preparedStatement.setInt(1, userHasBook.getUserId());
        preparedStatement.setInt(2, userHasBook.getBookId());
        preparedStatement.setInt(3, userHasBook.getQuantity());
        preparedStatement.setDate(4, java.sql.Date.valueOf(userHasBook.getBorrowDate()));
        preparedStatement.setDate(5, java.sql.Date.valueOf(userHasBook.getReturnDate()));

    }
    private Optional<UserHasBook> mapResultSetToUserHasBook(ResultSet resultSet) throws SQLException {
        Date returnDate = resultSet.getDate("return_date");
        LocalDate localReturnDate = returnDate != null ? returnDate.toLocalDate() : null;

        return Optional.of(new UserHasBook(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("book_id"),
                resultSet.getInt("quantity"),
                resultSet.getDate("borrow_date").toLocalDate(),
                localReturnDate
        ));
    }
    private List<UserHasBook> mapResultSetToUserHasBooks(ResultSet resultSet) throws SQLException {
        List<UserHasBook> userHasBooks = new ArrayList<>();
        while (resultSet.next()) {
            mapResultSetToUserHasBook(resultSet).ifPresent(userHasBooks::add);
        }
        return userHasBooks;
    }
    private void handleSQLException(SQLException e, String conflictMessage, String errorMessage) {
        log.error(errorMessage, e);
        if (e instanceof SQLIntegrityConstraintViolationException) {
            throw new DatabaseException(conflictMessage, e.getMessage(), HttpStatusCode.CONFLICT);
        }
        throw new DatabaseException(errorMessage, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
