package org.evpro.bookshopV4.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.exception.BadRequestException;
import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.exception.ErrorResponse;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;
import org.evpro.bookshopV4.service.BookService;
import org.evpro.bookshopV4.utilities.HandlerMapping;
import org.evpro.bookshopV4.utilities.RequireRole;
import org.evpro.bookshopV4.utilities.RequestParameterExtractor;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.evpro.bookshopV4.utilities.CodeMsg.*;

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
    public BookServlet(BookService bookService, ObjectMapper objectMapper) {
        this.bookService = bookService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @HandlerMapping(path = "/book/filter", method = "GET")
    public void getBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String searchType = RequestParameterExtractor.extractStringParameter(request, "search-type");
            String searchValue = RequestParameterExtractor.extractStringParameter(request, "search-value");
            Book book;

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
                    throw new BadRequestException("Invalid search type", HttpStatusCode.BAD_REQUEST);
            }

            sendJsonResponse(response, HttpServletResponse.SC_OK, book);
        } catch (BadRequestException | BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(DB_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @HandlerMapping(path = "/filter/available", method = "GET")
    public void getAvailableBooks(HttpServletResponse response) throws IOException {
        try {
            List<Book> availableBooks = bookService.getAvailableBooks();
            sendJsonResponse(response, HttpServletResponse.SC_OK, availableBooks);
        } catch (BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(EVB_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @HandlerMapping(path = "/filter/author", method = "GET")
    public void getBooksByAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String author = RequestParameterExtractor.extractStringParameter(request, "author");
            List<Book> booksByAuthor = bookService.getBooksByAuthor(author);
            sendJsonResponse(response, HttpServletResponse.SC_OK, booksByAuthor);
        } catch (BadRequestException | BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Database error while retrieving books by author", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @HandlerMapping(path = "/filter/year_publication", method = "GET")
    public void getBooksByRange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            LocalDate startDate = RequestParameterExtractor.extractLocalDateParameter(request, "start");
            LocalDate endDate = RequestParameterExtractor.extractLocalDateParameter(request, "end");
            List<Book> books = bookService.getBooksByYearRange(startDate, endDate);
            sendJsonResponse(response, HttpServletResponse.SC_OK, books);
        } catch (BadRequestException | BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(EVB_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @HandlerMapping(path = "/all", method = "GET")
    public void getAllBooks(HttpServletResponse response) throws IOException {
        try {
            List<Book> books = bookService.getAllBooks();
            sendJsonResponse(response, HttpServletResponse.SC_OK, books);
        } catch (BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(EVB_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/book/update", method = "PUT")
    public void handleUpdateOfBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Book book = objectMapper.readValue(request.getReader(), Book.class);
            boolean bookUpdated = bookService.updateBook(book);
            if (bookUpdated) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "Book updated successfully");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to update book");
            }
        } catch (SQLException e) {
            log.error("Error updating book", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/book/update/availability", method = "PUT")
    public void handleBookAvailability(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = RequestParameterExtractor.extractIntParameter(request, "id");
            boolean available = RequestParameterExtractor.extractBooleanParameter(request, "available");
            boolean availabilityUpdated = bookService.updateBookAvailability(id, available);
            if (availabilityUpdated) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "Book availability updated successfully");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
            }
        } catch (BadRequestException | BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error updating book availability", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/increase-quantity", method = "PUT")
    public void handleIncreaseBookQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = RequestParameterExtractor.extractIntParameter(request, "id");
            int quantity = RequestParameterExtractor.extractIntParameter(request, "quantity");
            boolean quantityUpdated = bookService.increaseBookQuantity(id, quantity);
            if (quantityUpdated) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "Book quantity increased successfully");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
            }
        } catch (BadRequestException | BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error increasing book quantity", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/decrease-quantity", method = "PUT")
    public void handleDecreaseBookQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = RequestParameterExtractor.extractIntParameter(request, "id");
            int quantity = RequestParameterExtractor.extractIntParameter(request, "quantity");
            boolean quantityUpdated = bookService.decreaseBookQuantity(id, quantity);
            if (quantityUpdated) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "Book quantity decreased successfully");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
            }
        } catch (BadRequestException | BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error decreasing book quantity", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/add", method = "POST")
    public void handleAddBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Book book = objectMapper.readValue(request.getReader(), Book.class);
            boolean addedBook = bookService.addBook(book);
            if (addedBook) {
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, book);
            }
            sendJsonResponse(response, HttpServletResponse.SC_OK, "Book quantity updated " + book);
        } catch (SQLException e) {
              }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/add-multiple", method = "POST")
    public void handleAddBooks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Book> books = objectMapper.readValue(request.getReader(), new TypeReference<List<Book>>() {});
            if (books == null || books.isEmpty()) {
                throw new BadRequestException("No books provided or deserialization failed", HttpStatusCode.BAD_REQUEST);
            }

            List<Book> addedBooks = bookService.addBooks(books);
            Map<String, Object> responseData = Map.of(
                    "message", "Books processed successfully",
                    "addedBooks", addedBooks
            );
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, responseData);
        } catch (BadRequestException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error processing books", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing books");
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/book/delete", method = "DELETE")
    public void handleDeleteOfBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = RequestParameterExtractor.extractIntParameter(request, "id");
            boolean bookDeleted = bookService.deleteBookWithEntireQuantity(id);
            if (bookDeleted) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "Book deleted successfully");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
            }
        } catch (BadRequestException | BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(EVB_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/delete/all", method = "DELETE")
    public void handleDeleteAll(HttpServletResponse response) throws IOException {
        try {
            boolean booksDeleted = bookService.deleteAll();
            if (booksDeleted) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "All books deleted successfully");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NO_CONTENT, ABF_CODE);
            }
        } catch (BookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error(EVB_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setContentType(AJ_FORMAT);
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), data);
    }
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.sendError(status, message);
    }
}