package org.evpro.bookshopV4.DAO.implementation;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.UserDAO;
import org.evpro.bookshopV4.exception.DatabaseException;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.model.enums.UserRole;
import org.evpro.bookshopV4.utilities.ConnectionFactory;
import org.evpro.bookshopV4.utilities.TransactionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
public class UserDAOImplementation implements UserDAO {

    private static final String DB_ERROR = "Error with database connection";

    //SQL Queries
    private static final String INSERT_USER = "INSERT INTO users (name, surname, email, password, role, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, surname = ?, email = ?, password = ?, role = ?, created_at = ? WHERE id = ?";
    private static final String UPDATE_USER_ROLE = "UPDATE users SET role = ? WHERE id = ?";
    private static final String SELECT_USER_BY_ID = "SELECT id, name, surname, email, password, role, created_at FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_EMAIL = "SELECT id, name, surname, email, password, role, created_at FROM users WHERE email = ?";
    private static final String SELECT_USERS_BY_ROLE = "SELECT id, name, surname, email, password, role, created_at FROM users WHERE role = ?";
    private static final String SELECT_ALL_USERS = "SELECT id, name, surname, email, password, role, created_at FROM users";
    private static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_ALL_USERS = "DELETE FROM users";

    @Override
    public void save(User user) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
                    mapPreparedStatementAdmin(preparedStatement, user);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating user failed, no rows affected.");
                    }
                    generateId(preparedStatement, user);
                }
                log.info("User saved successfully: {}", user.getId());
            });
        } catch (SQLException e) {
            handleSQLException(e, "User with this email already exists", "Error saving user");
        }
    }

    @Override
    public void saveUsers(List<User> users) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
                    for (User user : users) {
                        mapPreparedStatementAdmin(preparedStatement, user);
                        preparedStatement.addBatch();
                    }
                    int[] affectedRows = preparedStatement.executeBatch();
                    if (IntStream.of(affectedRows).sum() == 0) {
                        throw new SQLException("Creating users failed, no rows affected.");
                    }
                    generateIds(preparedStatement, users);
                }
                log.info("{} Users saved successfully", users.size());
            });
        } catch (SQLException e) {
            handleSQLException(e, "One or more users with this email already exist", "Error saving users");
        }
    }

    @Override
    public void update(User user) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)) {
                    mapPreparedStatement(preparedStatement, user);
                    preparedStatement.setInt(7, user.getId());
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating user failed, no rows affected.");
                    }
                    log.info("User updated successfully: {}", user.getEmail());
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error updating user", "Error updating user");
        }
    }

    @Override
    public void updateRole(int id, UserRole role) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_ROLE)) {
                    preparedStatement.setString(1, role.toString());
                    preparedStatement.setInt(2, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating user role failed, no rows affected.");
                    }
                    log.info("User role updated successfully for user id: {}", id);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error updating user role", "Error updating user role");
        }
    }

    @Override
    public Optional<User> findById(int id) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding user by ID");
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_EMAIL)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding user by email");
        }
        return Optional.empty();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERS_BY_ROLE)) {
            preparedStatement.setString(1, role.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToUsers(resultSet);
            }
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding users by role");
        }
        return new ArrayList<>();
    }

    @Override
    public List<User> findAll() {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return mapResultSetToUsers(resultSet);
        } catch (SQLException e) {
            handleSQLException(e, DB_ERROR, "Error finding all users");
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteById(int id) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_ID)) {
                    preparedStatement.setInt(1, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Deleting user failed, no rows affected.");
                    }
                    log.info("User with id {} deleted successfully", id);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error deleting user", "Error deleting user");
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_USERS)) {
                    int affectedRows = preparedStatement.executeUpdate();
                    log.info("{} users deleted successfully", affectedRows);
                }
            });
        } catch (SQLException e) {
            handleSQLException(e, "Error deleting all users", "Error deleting all users");
        }
    }

    //Utilities methods
    private void generateIds(PreparedStatement preparedStatement, List<User> users) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            for (User user : users) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained for a user.");
                }
            }
        }
    }
    private void generateId(PreparedStatement preparedStatement, User user) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
                log.info("User saved with ID: {}", user.getId());
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }
    private void mapPreparedStatementAdmin(PreparedStatement preparedStatement, User user) throws SQLException {
        mapPreparedStatement(preparedStatement, user);
        preparedStatement.setString(5, UserRole.ADMIN.toString());
    }
    private void mapPreparedStatement(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getSurname());
        preparedStatement.setString(3, user.getEmail());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setString(5, user.getRole().toString());
        preparedStatement.setDate(6, Date.valueOf(user.getCreatedAt()));
    }
    private Optional<User> mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return Optional.of(new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("surname"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                UserRole.valueOf(resultSet.getString("role")),
                resultSet.getDate("created_at").toLocalDate()
        ));
    }
    private List<User> mapResultSetToUsers(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            mapResultSetToUser(resultSet).ifPresent(users::add);
        }
        return users;
    }
    private void handleSQLException(SQLException e, String conflictMessage, String errorMessage) {
        log.error(errorMessage, e);
        if (e instanceof SQLIntegrityConstraintViolationException) {
            throw new DatabaseException(conflictMessage, e.getMessage(), HttpStatusCode.CONFLICT);
        }
        throw new DatabaseException(errorMessage, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}