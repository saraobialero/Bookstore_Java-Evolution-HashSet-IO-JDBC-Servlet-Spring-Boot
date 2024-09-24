package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.DTO.request.UpdateBookRequest;
import org.evpro.bookshopV5.model.DTO.response.BookDTO;
import org.evpro.bookshopV5.model.DTO.request.AddBookRequest;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;

import java.util.List;
import java.util.Set;

public interface BookFunctions {
    Set<BookDTO> getAllBooks();
    BookDTO getBookById(Integer bookId);
    BookDTO getBookByISBN(String ISBN);
    List<BookDTO> searchBooksByTitle(String title);
    Set<BookDTO> searchBooksByAuthor(String author);
    Set<BookDTO> getAvailableBooks();
    Set<BookDTO> getBooksByGenre(BookGenre genre);
    BookDTO updateBookQuantity(Integer bookId, int quantityChange);
    BookDTO updateBook(UpdateBookRequest request);
    BookDTO addBook(AddBookRequest request);
    List<BookDTO> addBooks(List<AddBookRequest> request);
    boolean deleteBookById(Integer bookId);
    boolean deleteAll();


}