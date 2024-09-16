package org.evpro.bookshopV5.service;


import org.evpro.bookshopV5.data.DTO.BookDTO;
import org.evpro.bookshopV5.data.response.ErrorResponse;
import org.evpro.bookshopV5.exception.BookException;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.repository.BookRepository;
import org.evpro.bookshopV5.service.functions.BookFunctions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public BookDTO getBookById(Integer bookId) {
        Book book =  bookRepository.findById(bookId)
                                   .orElseThrow(() -> new BookException(
                                                      new ErrorResponse(
                                                                ErrorCode.BNF,
                                                                "Book not found")));
        return convertToBookDTO(book);
    }

    @Override
    public List<Book> searchBooksByTitle(String title) {
        List<Book> books = bookRepository.findAllByTitle(title);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        "No content for research with title " + title));
        return books;
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) {
        List<Book> books = bookRepository.findAllByAuthor(author);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        "No content for research with author " + author));
        return books;
    }

    @Override
    public List<Book> getAvailableBooks() {
        List<Book> books = bookRepository.findByAvailableTrue();
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        "No available books"));
        return books;
    }

    @Override
    public BookDTO updateBookQuantity(Integer bookId, int quantityChange) {
        Book book = bookRepository.findById(bookId)
                                  .orElseThrow(() -> new BookException(
                                                     new ErrorResponse(
                                                            ErrorCode.BNF,
                                                            "Book not found")));
        book.setQuantity(book.getQuantity() + quantityChange);
        bookRepository.save(book);
        return convertToBookDTO(book);
    }

    @Override
    public BookDTO updateBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                            .orElseThrow(() -> new BookException(
                                               new ErrorResponse(
                                                        ErrorCode.BNF,
                                                        "Book not found")));
        existingBook.setId(existingBook.getId());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setAward(book.getAward());
        existingBook.setISBN(book.getISBN());
        existingBook.setAvailable(book.isAvailable());
        existingBook.setGenre(book.getGenre());
        existingBook.setTitle(book.getTitle());
        existingBook.setDescription(book.getDescription());
        existingBook.setPublicationYear(book.getPublicationYear());
        bookRepository.save(existingBook);
        return convertToBookDTO(existingBook);
    }

    @Override
    public BookDTO addBook(Book book) {
        Optional<Book> existingBookOptional = bookRepository.findById(book.getId());
        if (existingBookOptional.isPresent()) {
            Book existingBook = existingBookOptional.get();
            existingBook.setQuantity(book.getQuantity() + 1);
            bookRepository.save(existingBook);
        }
        bookRepository.save(book);
        return convertToBookDTO(book);
    }

    @Override
    public List<BookDTO> addBooks(List<Book> books) {
        if (books.isEmpty()) {
            throw new BookException(
                  new ErrorResponse(
                          ErrorCode.NCB,
                          "No books provided to add"));
        }

        List<BookDTO> addedBooks = new ArrayList<>();

        for (Book book : books) {
            Optional<Book> existingBookOptional = bookRepository.findById(book.getId());
            if (existingBookOptional.isPresent()) {
                Book existingBook = existingBookOptional.get();
                updateBookQuantity(existingBook.getId(), book.getQuantity());
                Book updatedBook = bookRepository.save(existingBook);
                addedBooks.add(convertToBookDTO(updatedBook));
            }
            Book savedBook = bookRepository.save(book);
            addedBooks.add(convertToBookDTO(savedBook));
        }
        return addedBooks;
    }

    @Override
    public boolean deleteBookById(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException(
                        new ErrorResponse(
                                ErrorCode.BNF,
                                "Book not found")));
        bookRepository.delete(book);
        return true;
    }

    @Override
    public boolean deleteAll() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty())
           throw new BookException(
                 new ErrorResponse(
                        ErrorCode.NCB,
                        "No books in store"));
        bookRepository.deleteAll();
        return true;
    }

    @Override
    public List<Book> getBooksByGenre(BookGenre genre) {
        List<Book> books = bookRepository.findAllByGenre(genre);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB, "No content for research"));
        return books;
    }

    private BookDTO convertToBookDTO(Book book) {
        return BookDTO.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .ISBN(book.getISBN())
                .award(book.getAward())
                .available(book.isAvailable())
                .description(book.getDescription())
                .publicationYear(book.getPublicationYear())
                .quantity(book.getQuantity())
                .genre(book.getGenre())
                .build();
    }

}
