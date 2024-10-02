package org.evpro.bookshopV5.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.DTO.request.AddBookRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateBookRequest;
import org.evpro.bookshopV5.model.DTO.response.BookDTO;
import org.evpro.bookshopV5.model.DTO.response.SuccessResponse;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookshop/v5/books")
public class BookController {

    private final BookService bookService;


    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Set<BookDTO>>> getAllBooks() {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.getAllBooks()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<BookDTO>> getBook(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.getBookById(id)), HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<SuccessResponse<Set<BookDTO>>> getAvailableBooks() {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.getAvailableBooks()), HttpStatus.OK);
    }


    @GetMapping("/{genre}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Set<BookDTO>>> getBooksByGenre(@PathVariable("genre") BookGenre genre) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.getBooksByGenre(genre)), HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<BookDTO>> addBook(@RequestBody @Valid AddBookRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.addBook(request)), HttpStatus.OK);
    }

    @PostMapping("/add/multiple")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<List<BookDTO>>> addBooks(@RequestBody List<AddBookRequest> requests) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.addBooks(requests)), HttpStatus.OK);
    }

    @PatchMapping("/{id}/{quantity}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<BookDTO>> updateQuantityOfBook(@PathVariable("id") Integer id,
                                                                @PathVariable("quantity") int quantity) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.updateBookQuantity(id, quantity)), HttpStatus.OK);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<BookDTO>> updateBook(@RequestBody @Valid UpdateBookRequest request) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.updateBook(request)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/remove")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deleteBook(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.deleteBookById(id)), HttpStatus.OK);
    }

    @DeleteMapping("/remove/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deleteBooks() {
        return new ResponseEntity<>(new SuccessResponse<>(bookService.deleteAll()), HttpStatus.OK);
    }

}
