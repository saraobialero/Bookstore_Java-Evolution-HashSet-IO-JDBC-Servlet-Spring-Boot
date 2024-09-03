import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.service.BookService;
import org.evpro.bookshopV4.servlet.BookServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UserHasBookServletTest {

    @Mock
    private BookService bookService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private BookServlet bookServlet;

    private PrintWriter printWriter;

    private ObjectMapper objectMapper;

    // Remove the mock for ObjectMapper and use a real instance
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        printWriter = mock(PrintWriter.class);
        bookServlet = new BookServlet(bookService, objectMapper);
        when(response.getWriter()).thenReturn(printWriter);
    }


    @Test
    void testDoPostNewBookAdded() throws Exception {
        when(request.getPathInfo()).thenReturn("/add");

        when(request.getParameter("title")).thenReturn("Sample Title");
        when(request.getParameter("author")).thenReturn("Sample Author");
        when(request.getParameter("isbn")).thenReturn("1234567890");
        when(request.getParameter("description")).thenReturn("Sample Description");
        when(request.getParameter("publication_year")).thenReturn("2023-01-01");
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getParameter("available")).thenReturn("true");

        when(bookService.addBook(any(Book.class))).thenAnswer(invocation -> {
            Book addedBook = invocation.getArgument(0);
            addedBook.setId(1);
            return addedBook;
        });

        bookServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(bookService).addBook(any(Book.class));
        verify(printWriter, never()).write(anyString());
    }

    @Test
    void testDoPostAddBookAlreadyExists() throws Exception {
        when(request.getPathInfo()).thenReturn("/add");

        when(request.getParameter("title")).thenReturn("Harper Lee");
        when(request.getParameter("author")).thenReturn("To Kill a Mockingbird");
        when(request.getParameter("isbn")).thenReturn("9780446310789");
        when(request.getParameter("description")).thenReturn("The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it.");
        when(request.getParameter("publication_year")).thenReturn("1960-07-11");
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getParameter("available")).thenReturn("true");

        final Book existingBook = getBook();

        when(bookService.addBook(any(Book.class))).thenReturn(existingBook);

        bookServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).addBook(any(Book.class));
        verify(printWriter, never()).write(anyString());
    }

    @Test
    void testDoPostAddBooks() throws Exception {
        // Prepare test data
        List<Book> inputBooks = Arrays.asList(
                new Book(null, "Book 1", "Author 1", LocalDate.of(1960, 7, 11), "Description 1", "ISBN1", 5, true),
                new Book(null, "Book 2", "Author 2", LocalDate.of(1965, 2, 1), "Description 2", "ISBN2", 3, true)
        );

        List<Book> outputBooks = Arrays.asList(
                new Book(1, "Book 1", "Author 1", LocalDate.of(1960, 7, 11), "Description 1", "ISBN1", 5, true),
                new Book(2, "Book 2", "Author 2", LocalDate.of(1965, 2, 1), "Description 2", "ISBN2", 3, true)
        );

        // Create actual JSON input
        String jsonInput = objectMapper.writeValueAsString(inputBooks);

        // Use the TypeReference directly
        TypeReference<List<Book>> bookListTypeRef = new TypeReference<List<Book>>() {};

        // Mock request
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        when(request.getPathInfo()).thenReturn("/add-multiple");

        // Mock response
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Mock service
        when(bookService.addBooks(anyList())).thenReturn(outputBooks);

        // Execute
        bookServlet.doPost(request, response);

        // Verify
        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(bookService).addBooks(inputBooks);

        // Check response body
        writer.flush();
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("Books processed successfully"));
        assertTrue(responseBody.contains("addedBooks"));
    }


    @Test
    void testDoPostSQLException() throws Exception {
        when(request.getPathInfo()).thenReturn("/add");

        when(request.getParameter("title")).thenReturn("Sample Title");
        when(request.getParameter("author")).thenReturn("Sample Author");
        when(request.getParameter("isbn")).thenReturn("1234567890");
        when(request.getParameter("description")).thenReturn("Sample Description");
        when(request.getParameter("publication_year")).thenReturn("2023-01-01");
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getParameter("available")).thenReturn("true");

        when(bookService.addBook(any(Book.class))).thenThrow(new SQLException("Database error"));

        bookServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(printWriter, never()).write(anyString());

    }

    private static Book getBook() {
        Book existingBook = new Book();
        existingBook.setId(1);
        existingBook.setTitle("To Kill a Mockingbird");
        existingBook.setAuthor("Harper Lee");
        existingBook.setISBN("9780446310789");
        existingBook.setDescription("The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it.");
        existingBook.setPublicationYear(LocalDate.of(1960, 07, 11));
        existingBook.setQuantity(14);
        existingBook.setAvailable(true);
        return existingBook;
    }

}