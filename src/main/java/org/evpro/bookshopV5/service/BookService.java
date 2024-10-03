package org.evpro.bookshopV5.service;


import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.DTO.request.UpdateBookRequest;
import org.evpro.bookshopV5.model.DTO.response.BookDTO;
import org.evpro.bookshopV5.model.DTO.request.AddBookRequest;
import org.evpro.bookshopV5.model.DTO.response.ErrorResponse;
import org.evpro.bookshopV5.exception.BookException;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.Loan;
import org.evpro.bookshopV5.model.LoanDetails;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.repository.*;
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
    private final LoanRepository loanRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Set<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        bookListIsEmpty(books, " all");
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
        bookListIsEmpty(books, title);
        return convertCollection(books, DTOConverter::convertToBookDTO, ArrayList::new);
    }

    @Override
    public Set<BookDTO> searchBooksByAuthor(String author) {
        List<Book> books = bookRepository.findAllByAuthor(author);
        bookListIsEmpty(books, author);
        return convertCollection(books, DTOConverter::convertToBookDTO, HashSet::new);
    }

    @Override
    public Set<BookDTO> getAvailableBooks() {
        List<Book> books = bookRepository.findByAvailableTrue();
        bookListIsEmpty(books, " available");
        return convertCollection(books, DTOConverter::convertToBookDTO, HashSet::new);
    }

    @Transactional
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

    @Transactional
    @Override
    public BookDTO updateBook(UpdateBookRequest request) {
        Book existingBook = bookRepository.findById(request.getId())
                .orElseThrow(() -> new BookException(
                        new ErrorResponse(ErrorCode.BNF, BNF)));

       initializeValidDataInBook(request, existingBook);


        Book updatedBook = bookRepository.save(existingBook);
        return convertToBookDTO(updatedBook);
    }

    @Transactional
    @Override
    public BookDTO addBook(AddBookRequest request) {
        checkNotNullRequest(request);
        Optional<Book> existingBookOptional = bookRepository.findByISBN(request.getISBN());
        if (existingBookOptional.isPresent()) {
            Book existingBook = updateBookQuantity(existingBookOptional, request);
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
        checkNotEmptyListRequest(requests);
        List<BookDTO> addedBooks = new ArrayList<>();
        for (AddBookRequest request : requests) {
            Optional<Book> existingBookOptional = bookRepository.findByISBN(request.getISBN());
            if (existingBookOptional.isPresent()) {
                Book existingBook = updateBookQuantity(existingBookOptional, request);
                bookRepository.save(existingBook);
                addedBooks.add(convertToBookDTO(existingBook));
           } else {
               Book newBook = initializeBookFromRequest(request);
               bookRepository.save(newBook);
               addedBooks.add(convertToBookDTO(newBook));
            }
        }
        return addedBooks;
    }

    @Transactional
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

    @Transactional
    @Override
    public boolean deleteAll() {
        List<Book> books = bookRepository.findAll();
        bookListIsEmpty(books, " all");

        if (loanRepository.existsByReturnDateIsNull()) {
            throw new BookException(
                  new ErrorResponse(
                          ErrorCode.BL,
                          "Cannot delete all books. Some books are currently on loan"));
        }

        boolean anyBookInCart = cartItemRepository.count() > 0;
        if (anyBookInCart) {
            throw new BookException(new ErrorResponse(ErrorCode.BIC, "Cannot delete all books. Some books are in users' carts"));
        }

        bookRepository.deleteAll();
        return true;
    }

    @Override
    public Set<BookDTO> getBooksByGenre(BookGenre genre) {
        List<Book> books = bookRepository.findAllByGenre(genre);
        bookListIsEmpty(books, genre);
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
    private <T> void bookListIsEmpty(List<Book> books, T argument){
        if (books.isEmpty()) {
            throw new BookException(
                    new ErrorResponse(
                            ErrorCode.NCB,
                            NBC + argument));
        }
    }
    private void checkNotNullRequest(AddBookRequest request) {
        if (request == null) {
            throw new BookException(
                    new ErrorResponse(
                            ErrorCode.NCB,
                            "No book provided to add"));
        }

    }
    private void checkNotEmptyListRequest(List<AddBookRequest> requests) {
        if (requests.isEmpty()) {
            throw new BookException(
                    new ErrorResponse(
                            ErrorCode.NCB,
                            "No books provided to add"));
        }

    }
    private Book updateBookQuantity(Optional<Book> existingBookOptional, AddBookRequest request  ) {
        Book existingBook = existingBookOptional.get();
        existingBook.setQuantity(existingBook.getQuantity() + request.getQuantity());
        return existingBook;
    }

}
