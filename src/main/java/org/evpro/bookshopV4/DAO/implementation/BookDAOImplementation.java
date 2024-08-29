package org.evpro.bookshopV4.DAO.implementation;

import org.evpro.bookshopV4.DAO.BookDAO;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;


public class BookDAOImplementation implements BookDAO {

    @Override
    public void save(Book book) {

    }

    @Override
    public void save(org.evpro.bookshopV4.model.Book book) {

    }

    @Override
    public Optional<org.evpro.bookshopV4.model.Book> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<org.evpro.bookshopV4.model.Book> findByISBN(String ISBN) {
        return Optional.empty();
    }

    @Override
    public Optional<org.evpro.bookshopV4.model.Book> findByTitle(String title) {
        return Optional.empty();
    }

    @Override
    public List<org.evpro.bookshopV4.model.Book> findByAuthor(String author) {
        return List.of();
    }

    @Override
    public List<org.evpro.bookshopV4.model.Book> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public void deleteAll() {

    }
}
