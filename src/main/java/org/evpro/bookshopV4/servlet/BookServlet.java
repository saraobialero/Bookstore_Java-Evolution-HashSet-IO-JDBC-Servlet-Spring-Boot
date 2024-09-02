package org.evpro.bookshopV4.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.exception.BookException;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.service.BookService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Slf4j
@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private BookService bookService;
    private ObjectMapper objectMapper;

    private final String AJ_FORMAT = "application/json";
    private final String ONF_CODE = "Operation not found";
    private final String UE_CODE = "Unexpected error";
    private final String ABF_CODE = "Any book found";
    private final String EUB_CODE = "Error updating books";

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


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            switch (pathInfo) {
                case "/book/filter":
                    getBook(request, response);
                    break;
                case "/filter/available":
                    getAvailableBooks(response);
                    break;
                case "/filter/author":
                    getBooksByAuthor(request, response);
                    break;
                case "/filter/year_publication":
                    getBooksByRange(request, response);
                    break;
                case "/all":
                    getAllBooks(response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, ONF_CODE);
            }
        } catch (Exception e) {
            log.error(UE_CODE, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UE_CODE);
        }
    }
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
                switch (pathInfo){
                case "/update-book":
                   handleUpdateOfBook(request, response);
                   break;
                case "/availability":
                handleBookAvailability(request, response);
                break;
                case "/increase-quantity":
                handleIncreaseBookQuantity(request, response);
                break;
                case "/decrease-quantity":
                handleDecreaseBookQuantity(request, response);
                break;
                default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation not found");
            }
        } catch (Exception e) {
                log.error(UE_CODE, e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UE_CODE);
                }
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            switch (pathInfo) {
                case "/add":
                    handleAddBook(request, response);
                    break;
                case "/add-multiple":
                    handleAddBooks(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation not found");
            }
        } catch (Exception e) {
            log.error(UE_CODE, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UE_CODE);
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            switch (pathInfo) {
                case "book/delete":
                    handleDeleteOfBook(request, response);
                    break;
                case "/delete/all":
                    handleDeleteAll(response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Operation not found");
            }
        } catch (Exception e) {
            log.error("Unexpected error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
        }
    }


    //POST methods
    private void handleAddBook(HttpServletRequest request, HttpServletResponse response) {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        String description = request.getParameter("description");
        LocalDate publicationYear = Date.valueOf(request.getParameter("publication_year")).toLocalDate();
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        boolean available = Boolean.parseBoolean(request.getParameter("available"));

        Book book = initializeBook(title, author, isbn, description, publicationYear, quantity, available);

        try {
            Book addedOrUpdatedBook = bookService.addBook(book);
            if (addedOrUpdatedBook.getId().equals(book.getId())) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                log.info("Book added: {}", addedOrUpdatedBook);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Book exists, updated quantity: {}", addedOrUpdatedBook);
            }
        } catch (SQLException e) {
            log.error("Error adding book", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void handleAddBooks(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String jsonInput = readJsonFromRequest(request);
        log.info("Received JSON input: {}", jsonInput);

        try {
            List<Book> books = objectMapper.readValue(jsonInput, new TypeReference<List<Book>>() {});
            log.info("Deserialized books: {}", books);

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

    //PUT methods
    private void handleUpdateOfBook(HttpServletRequest request, HttpServletResponse response) {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        String description = request.getParameter("description");
        LocalDate publicationYear = LocalDate.parse(request.getParameter("publication_year"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        boolean available = Boolean.parseBoolean(request.getParameter("available"));

        Book book = initializeBook(title, author, isbn, description, publicationYear, quantity, available);

        try {
            boolean bookUpdated = bookService.updateBook(book);
            if(!bookUpdated) {
                log.error("Error updating book");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("Book updated:" + book);
        } catch (SQLException e) {
            log.error("Error updating book", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void handleBookAvailability(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idString = request.getParameter("id");
        String availableString = request.getParameter("available");
        if (idString == null || availableString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing search parameters");
            return;
        }
        try {
            int id = Integer.parseInt(idString);
            boolean available = Boolean.parseBoolean(availableString);
            boolean availabilityUpdated = bookService.updateBookAvailability(id, available);
            if (!availabilityUpdated){
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
                log.info(ABF_CODE);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("Book updated");
        }  catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error updating books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void handleIncreaseBookQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idString = request.getParameter("id");
        String quantityString = request.getParameter("quantity");
        if (idString == null || quantityString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing search parameters");
            return;
        }
        try {
            int id = Integer.parseInt(idString);
            int quantity = Integer.parseInt(quantityString);
            boolean quantityUpdated = bookService.increaseBookQuantity(id, quantity);
            if (!quantityUpdated){
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
                log.info(ABF_CODE);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("Book updated");
        }  catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error updating Book", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void handleDecreaseBookQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idString = request.getParameter("id");
        String quantityString = request.getParameter("quantity");
        if (idString == null || quantityString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing search parameters");
            return;
        }
        try {
            int id = Integer.parseInt(idString);
            int quantity = Integer.parseInt(quantityString);
            boolean quantityUpdated = bookService.decreaseBookQuantity(id, quantity);
            if (!quantityUpdated){
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ABF_CODE);
                log.info(ABF_CODE);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("Book updated");
        }  catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //GET methods
    private void getBook(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
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
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");

        } catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        }
    }
    private void getBooksByAuthor(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
    private void getBooksByRange(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
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
                response.sendError(HttpServletResponse.SC_NO_CONTENT, "Any book found");
                log.info("Any book found");
            }
            response.setContentType(AJ_FORMAT);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(books));
        }  catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void getAvailableBooks(HttpServletResponse response) throws IOException {
        try {
            List<Book> availableBooks = bookService.getAvailableBooks();
            if (availableBooks.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Any Book found");
            }
            response.setContentType(AJ_FORMAT);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(availableBooks));
        }
        catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException | IOException e) {
            log.error("Error viewing book", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void getAllBooks(HttpServletResponse response) throws SQLException, IOException {
        try {
            List<Book> books = bookService.getAllBooks();
            if (books.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Any Book found");
            }
            response.setContentType(AJ_FORMAT);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(books));
        }
        catch (BookException e) {
          response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing book", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //DELETE methods
    private void handleDeleteAll(HttpServletResponse response) throws IOException {
        try {
            boolean booksDeleted = bookService.deleteAll();
            if (!booksDeleted) {
                response.sendError(HttpServletResponse.SC_NO_CONTENT, ABF_CODE);
                log.info(ABF_CODE);
            }
            response.setContentType(AJ_FORMAT);
            response.setStatus(HttpServletResponse.SC_OK);
        }  catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void handleDeleteOfBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idString = request.getParameter("id");
        if (idString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing search parameters");
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
        }  catch (BookException e) {
            response.sendError(e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException e) {
            log.error("Error viewing books", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //Utilities
    private Book initializeBook(String title, String author, String isbn, String description, LocalDate publicationYear, int quantity, boolean available) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setISBN(isbn);
        book.setDescription(description);
        book.setQuantity(quantity);
        book.setAvailable(available);
        book.setPublicationYear(publicationYear);
        return book;

    }
    private String readJsonFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }


}
