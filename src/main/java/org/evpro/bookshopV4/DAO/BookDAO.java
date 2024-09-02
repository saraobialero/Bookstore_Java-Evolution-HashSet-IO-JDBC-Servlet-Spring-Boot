package org.evpro.bookshopV4.DAO;


import org.evpro.bookshopV4.model.Book;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookDAO {
    void save(Book book);
    void update(Book book);
    void saveBooks(List<Book> books);
    Optional<Book> findById(int id);
    Optional<Book> findByISBN(String ISBN);
    Optional<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByAvailability();
    List<Book> findByDates(LocalDate startYear, LocalDate endYear);
    List<Book> findAll();
    void deleteById(int id);
    void deleteAll();
   }
