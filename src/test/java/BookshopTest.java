import lombok.extern.slf4j.Slf4j;
import org.interview.bookshopV3.db.DatabaseManager;
import org.interview.bookshopV3.model.Book;
import org.interview.bookshopV3.model.Bookshop;
import org.interview.bookshopV3.model.PublicBookView;
import org.interview.bookshopV3.exception.BookException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class BookshopTest {

    private static final String OK = "Test passed";
    private static final String SEPARATOR = "\n";

    @Mock
    private DatabaseManager dbManager;

    @InjectMocks
    private Bookshop bookshop;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPublicCatalogIsGood() throws SQLException {
        log.info("Starting get public catalog test");
        Set<Book> mockBooks = createSampleBookSet();
        when(dbManager.getAllBooks()).thenReturn(mockBooks);

        Set<PublicBookView> publicCatalog = bookshop.getPublicCatalog();

        assertAll("Verify public catalog",
                () -> assertEquals(mockBooks.size(), publicCatalog.size(), "Catalog size should match"),
                () -> assertTrue(publicCatalog.stream().allMatch(view ->
                        mockBooks.stream().anyMatch(book ->
                                book.getTitle().equals(view.getTitle()) &&
                                        book.getAuthor().equals(view.getAuthor()) &&
                                        book.getISBN().equals(view.getISBN())
                        )
                ), "All books should be present in public view")
        );

        verify(dbManager, times(1)).getAllBooks();
        log.info(OK + SEPARATOR);
    }

    @Test
    void getPublicCatalogIsBad() throws SQLException {
        log.info("Starting get public catalog test with empty result");
        when(dbManager.getAllBooks()).thenReturn(new HashSet<>());

        Set<PublicBookView> publicCatalog = bookshop.getPublicCatalog();

        assertTrue(publicCatalog.isEmpty(), "Public catalog should be empty");
        verify(dbManager, times(1)).getAllBooks();
        log.info(OK + SEPARATOR);
    }

    @ParameterizedTest
    @MethodSource("provideGiveBookTestCases")
    void giveBookTest(int bookId, boolean initialAvailability, boolean expectedResult) throws SQLException {
        log.info("Starting give book test for book ID: " + bookId);
        Book mockBook = new Book(bookId, "Test Book", "Test Author", new Date(System.currentTimeMillis()), "Description", "1234567890", initialAvailability);
        when(dbManager.getBookById(bookId)).thenReturn(Optional.of(mockBook));

        boolean result = bookshop.giveBook(bookId, false);

        assertEquals(expectedResult, result, "Give book result should match expected");
        if (expectedResult) {
            verify(dbManager, times(1)).updateBookAvailability(bookId, false);
        } else {
            verify(dbManager, never()).updateBookAvailability(anyInt(), anyBoolean());
        }
        log.info(OK + SEPARATOR);
    }

    private static Stream<Arguments> provideGiveBookTestCases() {
        return Stream.of(
                Arguments.of(1, true, true),   // Book is available, should succeed
                Arguments.of(2, false, false)  // Book is not available, should fail
        );
    }

    @ParameterizedTest
    @MethodSource("provideReturnBookTestCases")
    void returnBookTest(int bookId, boolean initialAvailability, boolean expectedResult) throws SQLException {
        log.info("Starting return book test for book ID: " + bookId);
        Book mockBook = new Book(bookId, "Test Book", "Test Author", new Date(System.currentTimeMillis()), "Description", "1234567890", initialAvailability);
        when(dbManager.getBookById(bookId)).thenReturn(Optional.of(mockBook));

        boolean result = bookshop.returnBook(bookId, true);

        assertEquals(expectedResult, result, "Return book result should match expected");
        if (expectedResult) {
            verify(dbManager, times(1)).updateBookAvailability(bookId, true);
        } else {
            verify(dbManager, never()).updateBookAvailability(anyInt(), anyBoolean());
        }
        log.info(OK + SEPARATOR);
    }

    private static Stream<Arguments> provideReturnBookTestCases() {
        return Stream.of(
                Arguments.of(1, false, true),  // Book is not available, should succeed
                Arguments.of(2, true, false)   // Book is already available, should fail
        );
    }

    @Test
    void searchBookIsGood() throws SQLException {
        log.info("Starting search book test");
        int bookId = 1;
        Book expectedBook = new Book(bookId, "Test Book", "Test Author", new Date(System.currentTimeMillis()), "Description", "1234567890", true);
        when(dbManager.getBookById(bookId)).thenReturn(Optional.of(expectedBook));

        Book foundBook = bookshop.searchBookById(bookId);

        assertEquals(expectedBook, foundBook, "Found book should match expected");
        verify(dbManager, times(1)).getBookById(bookId);
        log.info(OK + SEPARATOR);
    }

    @Test
    void searchBookIsBadNotExists() throws SQLException {
        log.info("Starting search non-existent book test");
        int nonExistentBookId = 999;
        when(dbManager.getBookById(nonExistentBookId)).thenReturn(Optional.empty());

        assertThrows(BookException.class, () -> bookshop.searchBookById(nonExistentBookId),
                "Searching for non-existent book should throw BookException");
        verify(dbManager, times(1)).getBookById(nonExistentBookId);
        log.info(OK + SEPARATOR);
    }

    @Test
    void searchBookIsBadDatabaseError() throws SQLException {
        log.info("Starting search book with database error test");
        int bookId = 1;
        when(dbManager.getBookById(bookId)).thenThrow(new SQLException("Database error"));

        assertThrows(BookException.class, () -> bookshop.searchBookById(bookId),
                "Database error should result in BookException");
        verify(dbManager, times(1)).getBookById(bookId);
        log.info(OK + SEPARATOR);
    }

    private Set<Book> createSampleBookSet() {
        Set<Book> books = new HashSet<>();
        books.add(new Book(1, "Book 1", "Author 1", new Date(System.currentTimeMillis()), "Description 1", "ISBN1", true));
        books.add(new Book(2, "Book 2", "Author 2", new Date(System.currentTimeMillis()), "Description 2", "ISBN2", false));
        return books;
    }
}