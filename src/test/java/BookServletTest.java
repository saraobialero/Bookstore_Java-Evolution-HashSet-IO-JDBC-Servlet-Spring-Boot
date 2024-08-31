import org.evpro.bookshopV4.model.Book;
import org.evpro.bookshopV4.service.BookService;
import org.evpro.bookshopV4.servlet.BookServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import static org.mockito.Mockito.*;

public class BookServletTest {

    @Mock
    private BookService bookService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private BookServlet bookServlet;

    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoPostNewBookAdded() throws Exception {
        when(request.getPathInfo()).thenReturn("/add-book");

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
            return true;
        });

        bookServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(bookService).addBook(any(Book.class));
        verify(printWriter, never()).write(anyString());
    }

    @Test
    void testDoPostBookAlreadyExists() throws Exception {
        when(request.getPathInfo()).thenReturn("/add-book");

        when(request.getParameter("title")).thenReturn("Harper Lee");
        when(request.getParameter("author")).thenReturn("To Kill a Mockingbird");
        when(request.getParameter("isbn")).thenReturn("9780446310789");
        when(request.getParameter("description")).thenReturn("The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it.");
        when(request.getParameter("publication_year")).thenReturn("1960-07-11");
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getParameter("available")).thenReturn("true");

        when(bookService.addBook(any(Book.class))).thenReturn(false);

        bookServlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).addBook(any(Book.class));
        verify(printWriter, never()).write(anyString());
    }




}