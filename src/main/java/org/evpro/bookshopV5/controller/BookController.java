package org.evpro.bookshopV5.controller;

import jakarta.validation.Valid;
import org.evpro.bookshopV5.model.DTO.request.AddBookRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateBookRequest;
import org.evpro.bookshopV5.model.DTO.response.SuccessResponse;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookshop/v5/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getAllBooks() {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.getAllBooks()), HttpStatus.OK);
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getBook(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.getBookById(id)), HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<SuccessResponse> getAvailableBooks() {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.getAvailableBooks()), HttpStatus.OK);
    }

    //RequestParam
    @GetMapping("/{genre}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> getBooksByGenre(@PathVariable("genre") BookGenre genre) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.getBooksByGenre(genre)), HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> addBook(@RequestBody @Valid AddBookRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.addBook(request)), HttpStatus.OK);
    }

    @PostMapping("/add/multiple")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> addBooks(@RequestBody List<Book> books) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.addBooks(books)), HttpStatus.OK);
    }

    @PatchMapping("/book/{id}/{quantity}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updateQuantityOfBook(@PathVariable("id") Integer id,
                                                                @PathVariable("quantity") int quantity) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.updateBookQuantity(id, quantity)), HttpStatus.OK);
    }

    @PostMapping("/update/book")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> updateBook(@RequestBody @Valid UpdateBookRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.updateBook(request)), HttpStatus.OK);
    }

    @DeleteMapping("/remove/book/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> deleteBook(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.deleteBookById(id)), HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse> deleteBooks() {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.deleteAll()), HttpStatus.OK);
    }



}
