package org.evpro.bookshopV5.service;

import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.service.functions.BookFunctions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService implements BookFunctions {


    @Override
    public List<Book> getAllBooks() {
        return List.of();
    }

    @Override
    public Book getBookById(Integer bookId) {
        return null;
    }

    @Override
    public List<Book> searchBooksByTitle(String title) {
        return List.of();
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) {
        return List.of();
    }

    @Override
    public List<Book> getAvailableBooks() {
        return List.of();
    }

    @Override
    public void updateBookQuantity(Integer bookId, int quantityChange) {

    }

    @Override
    public List<Book> getBooksByGenre(BookGenre genre) {
        return List.of();
    }

    @Override
    public List<Book> getNewArrivals() {
        return List.of();
    }
}
