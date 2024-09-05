import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDate;
import java.util.Arrays;

import static org.evpro.bookshopV4.utilities.CodeMsg.AJ_FORMAT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BookServletTest {

    @Mock
    private BookService bookService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private BookServlet bookServlet;
    private ObjectMapper objectMapper;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        bookServlet = new BookServlet(bookService, objectMapper);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testGetBook() throws Exception {
        when(request.getParameter("search-type")).thenReturn("id");
        when(request.getParameter("search-value")).thenReturn("1");
        Book mockBook = new Book();
        mockBook.setId(1);
        when(bookService.getBookById(1)).thenReturn(mockBook);

        bookServlet.getBook(request, response);

        verify(response).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).getBookById(1);
    }

    @Test
    void testGetAvailableBooks() throws Exception {
        when(bookService.getAvailableBooks()).thenReturn(Arrays.asList(new Book(), new Book()));

        bookServlet.getAvailableBooks(response);

        verify(response).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).getAvailableBooks();
    }

    @Test
    void testGetBooksByAuthor() throws Exception {
        when(request.getParameter("author")).thenReturn("John Doe");
        when(bookService.getBooksByAuthor("John Doe")).thenReturn(Arrays.asList(new Book(), new Book()));

        bookServlet.getBooksByAuthor(request, response);

        verify(response).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).getBooksByAuthor("John Doe");
    }

    @Test
    void testGetBooksByRange() throws Exception {
        when(request.getParameter("start")).thenReturn("2022-01-01");
        when(request.getParameter("end")).thenReturn("2023-01-01");
        when(bookService.getBooksByYearRange(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(new Book(), new Book()));

        bookServlet.getBooksByRange(request, response);

        verify(response).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).getBooksByYearRange(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testGetAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(new Book(), new Book()));

        bookServlet.getAllBooks(response);

        verify(response).setContentType(AJ_FORMAT);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).getAllBooks();
    }

    @Test
    void testHandleUpdateOfBook() throws Exception {
        Book book = new Book();
        book.setId(1);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(book))));
        when(bookService.updateBook(any(Book.class))).thenReturn(true);

        bookServlet.handleUpdateOfBook(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).updateBook(any(Book.class));
    }

    @Test
    void testHandleBookAvailability() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("available")).thenReturn("true");
        when(bookService.updateBookAvailability(1, true)).thenReturn(true);

        bookServlet.handleBookAvailability(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).updateBookAvailability(1, true);
    }

    @Test
    void testHandleAddNewBook() throws Exception {
        Book newBook = createFullyPopulatedBook(null); // null ID for new book

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(newBook))));
        when(bookService.addBook(any(Book.class))).thenReturn(true);

        bookServlet.handleAddBook(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(bookService).addBook(any(Book.class));
        verify(response).setContentType(AJ_FORMAT);

        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains(newBook.getISBN()), "Response should contain ISBN");
        assertTrue(responseContent.contains(newBook.getTitle()), "Response should contain title");
        assertTrue(responseContent.contains(newBook.getAuthor()), "Response should contain author");
    }

    @Test
    void testHandleAddExistingBook() throws Exception {
        Book existingBook = createFullyPopulatedBook(1);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(existingBook))));
        when(bookService.addBook(any(Book.class))).thenReturn(false);

        bookServlet.handleAddBook(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).addBook(any(Book.class));
        verify(response).setContentType(AJ_FORMAT);

        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains(existingBook.getISBN()), "Response should contain ISBN");
        assertTrue(responseContent.contains(existingBook.getTitle()), "Response should contain title");
        assertTrue(responseContent.contains(existingBook.getAuthor()), "Response should contain author");
    }

    @Test
    void testHandleDeleteOfBook() throws Exception {
        when(request.getParameter("id")).thenReturn("1");
        when(bookService.deleteBookWithEntireQuantity(1)).thenReturn(true);

        bookServlet.handleDeleteOfBook(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).deleteBookWithEntireQuantity(1);
    }

    @Test
    void testHandleDeleteAll() throws Exception {
        when(bookService.deleteAll()).thenReturn(true);

        bookServlet.handleDeleteAll(response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(bookService).deleteAll();
    }

    private Book createFullyPopulatedBook(Integer id) {
        Book book = new Book();
        book.setId(id);
        book.setISBN("1234567890123");
        book.setTitle("Test Book Title");
        book.setAuthor("Test Author");
        book.setPublicationYear(LocalDate.of(2023, 1, 1));
        book.setDescription("This is a test book description");
        book.setQuantity(10);
        book.setAvailable(true);
        return book;
    }
}