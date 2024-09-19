package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.DTO.request.UpdateBookRequest;
import org.evpro.bookshopV5.model.DTO.response.BookDTO;
import org.evpro.bookshopV5.model.DTO.request.AddBookRequest;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.util.List;

public interface BookFunctions {
    List<Book> getAllBooks();
    BookDTO getBookById(Integer bookId);
    BookDTO getBookByISBN(String ISBN);
    List<Book> searchBooksByTitle(String title);
    List<Book> searchBooksByAuthor(String author);
    List<Book> getAvailableBooks();
    List<Book> getBooksByGenre(BookGenre genre);
    BookDTO updateBookQuantity(Integer bookId, int quantityChange);
    BookDTO updateBook(UpdateBookRequest request);
    BookDTO addBook(AddBookRequest request);
    List<BookDTO> addBooks(List<AddBookRequest> booksRequest);
    boolean deleteBookById(Integer bookId);
    boolean deleteAll();


}