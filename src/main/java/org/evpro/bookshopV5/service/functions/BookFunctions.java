package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.data.DTO.BookDTO;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.util.List;

public interface BookFunctions {
    List<Book> getAllBooks();
    BookDTO getBookById(Integer bookId);
    List<Book> searchBooksByTitle(String title);
    List<Book> searchBooksByAuthor(String author);
    List<Book> getAvailableBooks();
    List<Book> getBooksByGenre(BookGenre genre);
    BookDTO updateBookQuantity(Integer bookId, int quantityChange);
    BookDTO updateBook(Book book);
    BookDTO addBook(Book book);
    List<BookDTO> addBooks(List<Book> books);
    boolean deleteBookById(Integer bookId);
    boolean deleteAll();


}