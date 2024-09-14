package org.evpro.bookshopV5.service;


import org.evpro.bookshopV5.data.response.ErrorResponse;
import org.evpro.bookshopV5.exception.BookException;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.repository.BookRepository;
import org.evpro.bookshopV5.service.functions.BookFunctions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService implements BookFunctions {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) throw new BookException(
                                   new ErrorResponse(
                                           ErrorCode.NCB,
                                           "No books content"));
        return books;
    }

    @Override
    public Book getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                             .orElseThrow(() -> new BookException(
                                                new ErrorResponse(
                                                        ErrorCode.BNF,
                                                        "Book not found")));
    }

    @Override
    public List<Book> searchBooksByTitle(String title) {
        List<Book> books = bookRepository.findAllByTitle(title);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        "No content for research"));
        return books;
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) {
        List<Book> books = bookRepository.findAllByAuthor(author);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        "No content for research"));
        return books;
    }

    @Override
    public List<Book> getAvailableBooks() {
        List<Book> books = bookRepository.findByAvailableTrue();
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        "No content for research"));
        return books;
    }

    @Override
    public void updateBookQuantity(Integer bookId, int quantityChange) {
        Book book = getBookById(bookId);
        book.setQuantity(book.getQuantity() + quantityChange);
        bookRepository.save(book);
    }

    @Override
    public List<Book> getBooksByGenre(BookGenre genre) {
        List<Book> books = bookRepository.findAllByGenre(genre);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        "No content for research"));
        return books;
    }

}
