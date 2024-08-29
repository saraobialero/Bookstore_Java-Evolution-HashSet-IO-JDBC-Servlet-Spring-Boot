package org.evpro.bookshopV4.DAO.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.DAO.BookDAO;
import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.exception.DatabaseException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.utilities.ConnectionFactory;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BookDAOImplementation implements BookDAO {
    @Override
    public void addBook(Book book) {

    }

    @Override
    public void save(Book book) {

    }

    @Override
    public Optional<Book> findById(int id) throws SQLException {
        String querySQL = "SELECT * FROM " + " books WHERE ID = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
             preparedStatement.setInt(1, id);
             try (ResultSet resultSet = preparedStatement.executeQuery()) {
                 if (resultSet.next()) {
                     mapResultSetToBook(resultSet);
                 }
             }
        } catch (SQLException e ) {
            log.error("Error with database connection", e);
            throw new DatabaseException("Error with database connection", e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
        throw new BookException("Book with id " + id + " not found" , HttpStatusCode.NOT_FOUND);
    }


    @Override
    public Optional<Book> findByISBN(String ISBN) {
        return Optional.empty();
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        return Optional.empty();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return List.of();
    }

    @Override
    public List<Book> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public void deleteAll() {

    }

    private Optional<Book> mapResultSetToBook( ResultSet resultSet) throws SQLException {
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
}
