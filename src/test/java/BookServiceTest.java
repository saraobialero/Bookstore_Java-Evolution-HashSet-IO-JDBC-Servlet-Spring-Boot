import org.evpro.bookshopV5.exception.BookException;
import org.evpro.bookshopV5.model.Book;
import org.evpro.bookshopV5.model.DTO.request.AddBookRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateBookRequest;
import org.evpro.bookshopV5.model.DTO.response.BookDTO;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.repository.BookRepository;
import org.evpro.bookshopV5.repository.CartItemRepository;
import org.evpro.bookshopV5.repository.LoanRepository;
import org.evpro.bookshopV5.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBooks_Success() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        Book book2 = new Book();
        book2.setTitle("Book 2");
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        Set<BookDTO> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void testGetBookById_Success() {
        Book book = new Book();
        book.setId(1);
        book.setTitle("Test Book");
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        BookDTO result = bookService.getBookById(1);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(BookException.class, () -> bookService.getBookById(1));
    }

    @Test
    void testAddBook_NewBook() {
        AddBookRequest request = new AddBookRequest();
        request.setTitle("New Book");
        request.setAuthor("Author");
        request.setISBN("1234567890");
        request.setQuantity(5);
        request.setPublicationYear(LocalDate.now());
        request.setGenre(BookGenre.NOVEL);

        when(bookRepository.findByISBN(request.getISBN())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookDTO result = bookService.addBook(request);

        assertNotNull(result);
        assertEquals("New Book", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testUpdateBook_Success() {
        UpdateBookRequest request = new UpdateBookRequest();
        request.setId(1);
        request.setTitle("Updated Title");

        Book existingBook = new Book();
        existingBook.setId(1);
        existingBook.setTitle("Old Title");

        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookDTO result = bookService.updateBook(request);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testDeleteBookById_Success() {
        Book book = new Book();
        book.setId(1);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        boolean result = bookService.deleteBookById(1);

        assertTrue(result);
        verify(bookRepository).delete(book);
    }

}