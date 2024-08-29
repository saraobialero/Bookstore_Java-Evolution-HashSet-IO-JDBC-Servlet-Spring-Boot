package org.evpro.bookshopV4.DAO;


import org.evpro.bookshopV4.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookDAO {
    void addBook(Book book);
    void save(Book book);
    Optional<Book> findById(int id) throws SQLException;
    Optional<Book> findByISBN(String ISBN);
    Optional<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findAll();
    void deleteById(int id);
    void deleteAll();
   }
