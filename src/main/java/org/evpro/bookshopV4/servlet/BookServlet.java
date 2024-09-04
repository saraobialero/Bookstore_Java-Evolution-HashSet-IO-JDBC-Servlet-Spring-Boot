package org.evpro.bookshopV4.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.service.BookService;
import org.evpro.bookshopV4.utilities.HandlerMapping;
import org.evpro.bookshopV4.utilities.RequireRole;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.evpro.bookshopV4.model.enums.CodeAndFormat.*;

@Slf4j
@WebServlet("/books/*")
public class BookServlet extends BaseServlet {

    private final BookService bookService;
    private final ObjectMapper objectMapper;

    public BookServlet() {
        this.bookService = new BookService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public BookServlet(BookService bookService) {
        this(bookService, new ObjectMapper());
    }

    public BookServlet(BookService bookService, ObjectMapper objectMapper) {
        this.bookService = bookService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @HandlerMapping(path = "/book/filter", method = "GET")
    public void getBook(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String searchType = request.getParameter("search-type");
        String searchValue = request.getParameter("search-value");
        Book book;

        if (searchType == null || searchValue == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing search parameters");
            return;
        }

        try {
            switch (searchType.toLowerCase()) {
                case "id":
                    book = bookService.getBookById(Integer.parseInt(searchValue));
                    break;
                case "isbn":
                    book = bookService.getBookByISBN(searchValue);
                    break;
                case "title":
                    book = bookService.getBookByTitle(searchValue);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid search type");
                    return;
            }
            if (book != null) {
                response.setContentType(AJ_FORMAT);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(book));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            }
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        }
    }

    @HandlerMapping(path = "/filter/available", method = "GET")
    public void getAvailableBooks(HttpServletResponse response) throws IOException {
        try {
            List<Book> availableBooks = bookService.getAvailableBooks();
            if (availableBooks.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No books found");
            } else {
                response.setContentType(AJ_FORMAT);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(availableBooks));
            }
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @HandlerMapping(path = "/filter/author", method = "GET")
    public void getBooksByAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String author = request.getParameter("author");
        if (author == null || author.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Author parameter is required");
            return;
        }
        try {
            List<Book> booksByAuthor = bookService.getBooksByAuthor(author);
            response.setContentType(AJ_FORMAT);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(booksByAuthor));
        } catch (BookException e) {
            log.error("Error retrieving books by author: {}", author, e);
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Database error while retrieving books by author: {}", author, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @HandlerMapping(path = "/filter/year_publication", method = "GET")
    public void getBooksByRange(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String startDateString = request.getParameter("start");
        String endDateString = request.getParameter("end");

        if (startDateString == null || endDateString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing search parameters");
            return;
        }
        LocalDate startDate = LocalDate.parse(startDateString);
        LocalDate endDate = LocalDate.parse(endDateString);
        try {
            List<Book> books = bookService.getBooksByYearRange(startDate, endDate);
            log.info(books.toString());
            if (books.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NO_CONTENT, "No books found");
            } else {
                response.setContentType(AJ_FORMAT);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(books));
            }
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @HandlerMapping(path = "/all", method = "GET")
    public void getAllBooks(HttpServletResponse response) throws SQLException, IOException {
        try {
            List<Book> books = bookService.getAllBooks();
            if (books.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No books found");
            } else {
                response.setContentType(AJ_FORMAT);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(books));
            }
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/update-book", method = "PUT")
    public void handleUpdateOfBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Book book = objectMapper.readValue(request.getReader(), Book.class);
            boolean bookUpdated = bookService.updateBook(book);
            if (!bookUpdated) {
                log.error("Error updating book");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Book updated: {}", book);
            }
        } catch (SQLException e) {
            log.error("Error updating book", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/availability", method = "PUT")
    public void handleBookAvailability(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idString = request.getParameter("id");
        String availableString = request.getParameter("available");
        if (idString == null || availableString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }
        try {
            int id = Integer.parseInt(idString);
            boolean available = Boolean.parseBoolean(availableString);
            boolean availabilityUpdated = bookService.updateBookAvailability(id, available);
            if (!availabilityUpdated) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
                log.info(ABF_CODE);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Book availability updated");
            }
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error updating book availability", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/increase-quantity", method = "PUT")
    public void handleIncreaseBookQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idString = request.getParameter("id");
        String quantityString = request.getParameter("quantity");
        if (idString == null || quantityString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }
        try {
            int id = Integer.parseInt(idString);
            int quantity = Integer.parseInt(quantityString);
            boolean quantityUpdated = bookService.increaseBookQuantity(id, quantity);
            if (!quantityUpdated) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
                log.info(ABF_CODE);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Book quantity increased");
            }
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error increasing book quantity", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/decrease-quantity", method = "PUT")
    public void handleDecreaseBookQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idString = request.getParameter("id");
        String quantityString = request.getParameter("quantity");
        if (idString == null || quantityString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }
        try {
            int id = Integer.parseInt(idString);
            int quantity = Integer.parseInt(quantityString);
            boolean quantityUpdated = bookService.decreaseBookQuantity(id, quantity);
            if (!quantityUpdated) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
                log.info(ABF_CODE);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Book quantity decreased");
            }
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error decreasing book quantity", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/add", method = "POST")
    public void handleAddBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Book book = objectMapper.readValue(request.getReader(), Book.class);
            Book addedOrUpdatedBook = bookService.addBook(book);
            if (addedOrUpdatedBook.getId().equals(book.getId())) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                log.info("Book added: {}", addedOrUpdatedBook);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Book exists, updated quantity: {}", addedOrUpdatedBook);
            }
            response.setContentType(AJ_FORMAT);
            response.getWriter().write(objectMapper.writeValueAsString(addedOrUpdatedBook));
        } catch (SQLException e) {
            log.error("Error adding book", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/add-multiple", method = "POST")
    public void handleAddBooks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Book> books = objectMapper.readValue(request.getReader(), new TypeReference<List<Book>>() {
            });
            if (books == null || books.isEmpty()) {
                throw new IllegalArgumentException("No books provided or deserialization failed");
            }

            List<Book> addedBooks = bookService.addBooks(books);
            String jsonResponse = objectMapper.writeValueAsString(Map.of(
                    "message", "Books processed successfully",
                    "addedBooks", addedBooks
            ));

            response.setContentType(AJ_FORMAT);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            log.error("Error processing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(AJ_FORMAT);
            String errorJson = objectMapper.writeValueAsString(Map.of("error", e.getMessage()));
            response.getWriter().write(errorJson);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/book/delete", method = "DELETE")
    public void handleDeleteOfBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idString = request.getParameter("id");
        if (idString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing book ID");
            return;
        }
        try {
            int id = Integer.parseInt(idString);
            boolean bookDeleted = bookService.deleteBookWithEntireQuantity(id);
            if (!bookDeleted) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
                log.info(ABF_CODE);
            }
            response.setContentType(AJ_FORMAT);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/delete/all", method = "DELETE")
    public void handleDeleteAll(HttpServletResponse response) throws IOException {
        try {
            boolean booksDeleted = bookService.deleteAll();
            if (!booksDeleted) {
                response.sendError(HttpServletResponse.SC_NO_CONTENT, ABF_CODE);
                log.info(ABF_CODE);
            }
            response.setContentType(AJ_FORMAT);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
