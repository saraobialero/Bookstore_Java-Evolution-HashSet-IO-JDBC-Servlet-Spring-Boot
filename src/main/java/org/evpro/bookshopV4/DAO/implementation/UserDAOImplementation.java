package org.evpro.bookshopV4.DAO.implementation;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.UserDAO;
import org.evpro.bookshopV4.exception.DatabaseException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.model.User;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.utilities.ConnectionFactory;
import org.evpro.bookshopV4.utilities.TransactionManager;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserDAOImplementation implements UserDAO {

    private static final String DB_ERROR = "Error with database connection";

    @Override
    public void save(User user) {
        String querySQL = "INSERT INTO users (name, surname, email, password, role, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL, Statement.RETURN_GENERATED_KEYS)) {
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
            log.error(DB_ERROR, e);
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new DatabaseException("User with this email already exists", e.getMessage(), HttpStatusCode.CONFLICT);
            }
            throw new DatabaseException(DB_ERROR, e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void saveUsers(List<User> users) {
        String querySQL = "INSERT INTO users (name, surname, email, password, role, created_at) + " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL, Statement.RETURN_GENERATED_KEYS)) {
                    for (User user : users) {
                        mapPreparedStatementAdmin(preparedStatement, user);
                    }
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating user failed, no rows affected.");
                    }
                    generateIds(preparedStatement, users);
                }
                log.info("{} Users saved successfully", users);
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
    public void update(User user) {
        String querySQL = "UPDATE users SET name = ?, surname = ?, email = ?, password = ?, role = ?, created_at = ? WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
                    mapPreparedStatement(preparedStatement, user);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating user failed, no rows affected.");
                    }
                    log.info("User updated successfully: {}", user.getEmail());
                }
            });
        } catch (SQLException e) {
            log.error("Error updating user: {}", user.getEmail(), e);
            throw new DatabaseException("Error updating user", e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateRole(int id, User.UserRole role) {
        String querySQL = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection()) {
            TransactionManager.executeInTransaction(connection, () -> {
                try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
                    preparedStatement.setString(1, role.toString());
                    preparedStatement.setInt(2, id);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating user failed, no rows affected.");
                    }
                    log.info("User role updated successfully for user id: {}", id);
                }
            });
        } catch (SQLException e) {
            log.error("Error updating roel for user with id: {}", id, e);
            throw new DatabaseException("Error updating user role", e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<User> findByRole(User.UserRole role) {
        return List.of();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public void deleteAll() {

    }

    private void generateIds(PreparedStatement preparedStatement, List<User> users) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            for (User user : users) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained for a book.");
                }
            }
        }
    }
    private void generateId(PreparedStatement preparedStatement, User user) throws SQLException {
        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    log.info("User saved with ID: {}", user.getId());
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }
    private void mapPreparedStatementAdmin(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getSurname());
        preparedStatement.setString(3, user.getEmail());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setString(5, User.UserRole.ADMIN.toString());
        preparedStatement.setDate(6, Date.valueOf(user.getCreatedAt().toLocalDate()));

    }
    private void mapPreparedStatement(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getSurname());
        preparedStatement.setString(3, user.getEmail());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setString(5, user.getRole().toString());
        preparedStatement.setDate(6, Date.valueOf(user.getCreatedAt().toLocalDate()));
    }

}
