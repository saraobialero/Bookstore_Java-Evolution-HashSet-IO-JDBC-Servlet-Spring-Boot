package org.evpro.bookshopV4.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.exception.UserHasBookException;
import org.evpro.bookshopV4.model.UserHasBook;
import org.evpro.bookshopV4.service.UserHasBookService;
import org.evpro.bookshopV4.utilities.HandlerMapping;
import org.evpro.bookshopV4.utilities.RequestParameterExtractor;
import org.evpro.bookshopV4.utilities.RequireRole;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.evpro.bookshopV4.utilities.CodeMsg.*;

@Slf4j
@WebServlet("/loans/*")
public class UserHasBookServlet {

    private final UserHasBookService userHasBookService;
    private final ObjectMapper objectMapper;

    public UserHasBookServlet() {
        this.userHasBookService = new UserHasBookService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }
    public UserHasBookServlet(UserHasBookService userHasBookService, ObjectMapper objectMapper) {
        this.userHasBookService = userHasBookService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }

    @RequireRole("USER")
    @HandlerMapping(path = "/borrow", method = "POST")
    public void handleBorrowBook(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        try {
            int userId = RequestParameterExtractor.extractIntParameter(request, "user_id");
            int bookId = RequestParameterExtractor.extractIntParameter(request, "book_id");
            int quantity = RequestParameterExtractor.extractIntParameter(request, "quantity");
            boolean bookBorrowed = userHasBookService.borrowBook(userId, bookId, quantity);

            if (bookBorrowed) {
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, "for book with id " + bookId);
        }
        sendJsonResponse(response, HttpServletResponse.SC_OK, "Book quantity updated with id " + bookId);

        } catch (SQLException e) {
            log.error("Error borrow book", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @HandlerMapping(path = "/borrow/user", method = "GET")
    public void handleGetBorrowForUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int userId = RequestParameterExtractor.extractIntParameter(request, "user_id");
            List<UserHasBook> userHasBooks = userHasBookService.getBorrowsForUser(userId);
            if (!userHasBooks.isEmpty()) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, userHasBooks);
            }
            sendErrorResponse(response, HttpServletResponse.SC_NO_CONTENT, NB_CODE);
        } catch (UserHasBookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException | IOException e) {
            log.error(EVBU_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/borrow/book", method = "GET")
    public void handleGetBorrowForBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int bookId = RequestParameterExtractor.extractIntParameter(request, "book_id");
            List<UserHasBook> userHasBooks = userHasBookService.getBorrowsForBook(bookId);
            if (!userHasBooks.isEmpty()) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, userHasBooks);
            }
            sendErrorResponse(response, HttpServletResponse.SC_NO_CONTENT, NBU_CODE);
        } catch (UserHasBookException e) {
            sendErrorResponse(response, e.getHttpStatus().getCode(), e.getMessage());
        } catch (SQLException | IOException e) {
            log.error(EVBU_CODE, e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, DB_CODE);
        }
    }

    @RequireRole("ADMIN")
    @HandlerMapping(path = "/borrow/return", method = "PUT")
    public void handleReturnBorrow(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = RequestParameterExtractor.extractIntParameter(request, "id");
            boolean returnBorrowed = userHasBookService.returnBorrow(id);
            if (returnBorrowed) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, "Book returned");
            }
        } catch (SQLException e) {
            log.error("Error returning book", e);
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
