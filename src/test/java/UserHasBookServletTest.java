import com.fasterxml.jackson.databind.ObjectMapper;
import org.evpro.bookshopV4.model.UserHasBook;
import org.evpro.bookshopV4.service.UserHasBookService;
import org.evpro.bookshopV4.servlet.UserHasBookServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.evpro.bookshopV4.utilities.CodeMsg.AJ_FORMAT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UserHasBookServletTest {

    @Mock
    private UserHasBookService userHasBookService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private UserHasBookServlet userHasBookServlet;
    private ObjectMapper objectMapper;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        userHasBookServlet = new UserHasBookServlet(userHasBookService, objectMapper);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testHandleBorrowBook() throws Exception {
        when(request.getParameter("user_id")).thenReturn("1");
        when(request.getParameter("book_id")).thenReturn("2");
        when(request.getParameter("quantity")).thenReturn("1");
        when(userHasBookService.borrowBook(1, 2, 1)).thenReturn(true);

        userHasBookServlet.handleBorrowBook(request, response);

        verify(response, atLeastOnce()).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(userHasBookService).borrowBook(1, 2, 1);
        assertTrue(stringWriter.toString().contains("for book with id 2"));
    }

    @Test
    void testHandleGetBorrowForUser() throws Exception {
        when(request.getParameter("user_id")).thenReturn("1");
        List<UserHasBook> mockLoans = Arrays.asList(
                new UserHasBook(1, 1, 1, 1, LocalDate.now(), null),
                new UserHasBook(2, 1, 2, 1, LocalDate.now(), null)
        );
        when(userHasBookService.getBorrowsForUser(1)).thenReturn(mockLoans);

        userHasBookServlet.handleGetBorrowForUser(request, response);

        verify(response).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(userHasBookService).getBorrowsForUser(1);
        assertTrue(stringWriter.toString().contains("\"userId\":1"));
    }

    @Test
    void testHandleGetBorrowForBook() throws Exception {
        when(request.getParameter("book_id")).thenReturn("1");
        List<UserHasBook> mockLoans = Arrays.asList(
                new UserHasBook(1, 1, 1, 1, LocalDate.now(), null),
                new UserHasBook(2, 2, 1, 1, LocalDate.now(), null)
        );
        when(userHasBookService.getBorrowsForBook(1)).thenReturn(mockLoans);

        userHasBookServlet.handleGetBorrowForBook(request, response);

        verify(response).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(userHasBookService).getBorrowsForBook(1);
        assertTrue(stringWriter.toString().contains("\"bookId\":1"));
    }

    @Test
    void testHandleReturnBorrow() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(userHasBookService.returnBorrow(1)).thenReturn(true);

        userHasBookServlet.handleReturnBorrow(request, response);

        verify(response).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(userHasBookService).returnBorrow(1);
        assertTrue(stringWriter.toString().contains("Book returned"));
    }
}