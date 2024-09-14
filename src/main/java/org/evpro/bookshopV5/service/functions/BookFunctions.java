package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.util.List;

public interface BookFunctions {
    List<Book> getAllBooks();
    Book getBookById(Integer bookId);
    List<Book> searchBooksByTitle(String title);
    List<Book> searchBooksByAuthor(String author);
    List<Book> getAvailableBooks();
    void updateBookQuantity(Integer bookId, int quantityChange);
    List<Book> getBooksByGenre(BookGenre genre);
}