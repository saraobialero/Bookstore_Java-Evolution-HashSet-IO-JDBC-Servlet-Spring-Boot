package org.evpro.bookshopV5.service;


import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.DTO.request.UpdateBookRequest;
import org.evpro.bookshopV5.model.DTO.response.BookDTO;
import org.evpro.bookshopV5.model.DTO.request.AddBookRequest;
import org.evpro.bookshopV5.model.DTO.response.ErrorResponse;
import org.evpro.bookshopV5.exception.BookException;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.repository.BookRepository;
import org.evpro.bookshopV5.service.functions.BookFunctions;
import org.evpro.bookshopV5.utils.DTOConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.evpro.bookshopV5.utils.CodeMessages.BNF;
import static org.evpro.bookshopV5.utils.CodeMessages.NBC;
import static org.evpro.bookshopV5.utils.DTOConverter.convertCollection;
import static org.evpro.bookshopV5.utils.DTOConverter.convertToBookDTO;


@Service
@RequiredArgsConstructor
public class BookService implements BookFunctions {

    private final BookRepository bookRepository;

    @Override
    public Set<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) throw new BookException(
                                   new ErrorResponse(
                                           ErrorCode.NCB,
                                           "No books content"));
        return convertCollection(books, DTOConverter::convertToBookDTO, HashSet::new);
    }

    @Override
    public BookDTO getBookById(Integer bookId) {
        Book book =  bookRepository.findById(bookId)
                                   .orElseThrow(() -> new BookException(
                                                      new ErrorResponse(
                                                                ErrorCode.BNF,
                                                                BNF)));
        return convertToBookDTO(book);
    }

    @Override
    public BookDTO getBookByISBN(String ISBN) {
        Book book =  bookRepository.findByISBN(ISBN)
                                    .orElseThrow(() -> new BookException(
                                            new ErrorResponse(
                                                    ErrorCode.BNF,
                                                    BNF)));
        return convertToBookDTO(book);
    }

    @Override
    public List<BookDTO> searchBooksByTitle(String title) {
        List<Book> books = bookRepository.findAllByTitle(title);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        NBC + " title " + title));
        return convertCollection(books, DTOConverter::convertToBookDTO, ArrayList::new);
    }

    @Override
    public Set<BookDTO> searchBooksByAuthor(String author) {
        List<Book> books = bookRepository.findAllByAuthor(author);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        NBC + " author " + author));
        return convertCollection(books, DTOConverter::convertToBookDTO, HashSet::new);
    }

    @Override
    public Set<BookDTO> getAvailableBooks() {
        List<Book> books = bookRepository.findByAvailableTrue();
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB,
                        "No available books"));
        return convertCollection(books, DTOConverter::convertToBookDTO, HashSet::new);
    }

    @Override
    public BookDTO updateBookQuantity(Integer bookId, int quantityChange) {
        Book book = bookRepository.findById(bookId)
                                  .orElseThrow(() -> new BookException(
                                                     new ErrorResponse(
                                                            ErrorCode.BNF,
                                                            BNF)));
        book.setQuantity(book.getQuantity() + quantityChange);
        bookRepository.save(book);
        return convertToBookDTO(book);
    }

    @Override
    public BookDTO updateBook(UpdateBookRequest request) {
        Book existingBook = bookRepository.findById(request.getId())
                .orElseThrow(() -> new BookException(
                        new ErrorResponse(ErrorCode.BNF, BNF)));

       initializeValidDataInBook(request, existingBook);


        Book updatedBook = bookRepository.save(existingBook);
        return convertToBookDTO(updatedBook);
    }

    @Override
    @Transactional
    public BookDTO addBook(AddBookRequest request) {
        if (request == null) {
            throw new BookException(
                    new ErrorResponse(
                            ErrorCode.NCB,
                            "No book provided to add"));
        }

        Optional<Book> existingBookOptional = bookRepository.findByISBN(request.getISBN());
        if (existingBookOptional.isPresent()) {
            Book existingBook = existingBookOptional.get();
            existingBook.setQuantity(request.getQuantity() + existingBook.getQuantity());
            bookRepository.save(existingBook);
            return convertToBookDTO(existingBook);
        }
        Book book = initializeBookFromRequest(request);
        bookRepository.save(book);
        return convertToBookDTO(book);
    }

    @Transactional
    @Override
    public List<BookDTO> addBooks(List<AddBookRequest> requests) {
        if (requests.isEmpty()) {
            throw new BookException(
                  new ErrorResponse(
                          ErrorCode.NCB,
                          "No books provided to add"));
        }

        List<BookDTO> addedBooks = new ArrayList<>();

        for (AddBookRequest request : requests) {
            Optional<Book> existingBookOptional = bookRepository.findByISBN(request.getISBN());
            if (existingBookOptional.isPresent()) {
                Book existingBook = existingBookOptional.get();
                existingBook.setQuantity(existingBook.getQuantity() + request.getQuantity());
                Book updatedBook = bookRepository.save(existingBook);
                addedBooks.add(convertToBookDTO(updatedBook));
           } else {
               Book newBook = initializeBookFromRequest(request);
               bookRepository.save(newBook);
               addedBooks.add(convertToBookDTO(newBook));
            }
        }
        return addedBooks;
    }

    @Override
    public boolean deleteBookById(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException(
                        new ErrorResponse(
                                ErrorCode.BNF,
                                BNF)));
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
    public Set<BookDTO> getBooksByGenre(BookGenre genre) {
        List<Book> books = bookRepository.findAllByGenre(genre);
        if (books.isEmpty()) throw new BookException(
                new ErrorResponse(
                        ErrorCode.NCB, NBC + genre));
        return convertCollection(books, DTOConverter::convertToBookDTO, HashSet::new);
    }


    private void initializeValidDataInBook(UpdateBookRequest request, Book existingBook) {
        if (request.getTitle() != null) existingBook.setTitle(request.getTitle());
        if (request.getAuthor() != null) existingBook.setAuthor(request.getAuthor());
        if (request.getPublicationYear() != null) existingBook.setPublicationYear(request.getPublicationYear());
        if (request.getDescription() != null) existingBook.setDescription(request.getDescription());
        if (request.getAward() != null) existingBook.setAward(request.getAward());
        if (request.getGenre() != null) existingBook.setGenre(request.getGenre());
        if (request.getQuantity() != null) existingBook.setQuantity(request.getQuantity());
        if (request.getAvailable() != null) existingBook.setAvailable(request.getAvailable());
    }
    private Book initializeBookFromRequest(AddBookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setDescription(request.getDescription());
        book.setISBN(request.getISBN());
        book.setQuantity(request.getQuantity());
        book.setGenre(request.getGenre());
        book.setAvailable(request.isAvailable());
        return book;
    }

}
