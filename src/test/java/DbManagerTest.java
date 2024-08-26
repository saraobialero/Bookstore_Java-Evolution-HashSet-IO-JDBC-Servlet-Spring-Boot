import lombok.extern.slf4j.Slf4j;
import org.interview.bookshopV3.model.Bookshop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.interview.bookshopV3.db.DatabaseManager;
import org.interview.bookshopV3.model.Book;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class DbManagerTest {

    @Mock
    private DatabaseManager dbManager; //To simulate db manager

    @InjectMocks
    private Bookshop bookshop; //Simulate class bookshop and inject mock db in this class

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //Initialize mock before tests
    }

    //testing connection to db???

    //Testing CRUD Operations
    @Test
    void getAllBooksIsGood() throws SQLException {
        log.info("Starting get all Books test");
        Set<Book> expectedBooks = new HashSet<>();
        expectedBooks.add(new Book(1, "To Kill a Mockingbird", "Harper Lee", Date.valueOf("1960-07-11"), "The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it.", "9780446310789", true));
        expectedBooks.add(new Book(2, "1984", "George Orwell", Date.valueOf("1949-06-08"), "A dystopian social science fiction novel and cautionary tale.", "9780451524935", true));
        expectedBooks.add(new Book(3, "Pride and Prejudice", "Jane Austen", Date.valueOf("1813-01-28"), "A romantic novel of manners.", "9780141439518", true));
        expectedBooks.add(new Book(4, "The Great Gatsby", "F. Scott Fitzgerald", Date.valueOf("1925-04-10"), "A novel of the Jazz Age.", "9780743273565", false));
        expectedBooks.add(new Book(5, "Moby-Dick", "Herman Melville", Date.valueOf("1851-10-18"), "The saga of Captain Ahab and his monomaniacal pursuit of the white whale.", "9780142437247", true));

        when(dbManager.getAllBooks()).thenReturn(expectedBooks);

        Set<Book> actualBooks = dbManager.getAllBooks();

        log.info("Comparing books using streams");
        assertBookSetsEqualUsingStreams(expectedBooks, actualBooks);

        verify(dbManager, times(1)).getAllBooks();
        log.info("getAllBooks test completed");
    }
    @Test
    void getAllBooksIsBad() throws SQLException {
    }
    @Test
    void getBookByIdIsGood() throws SQLException {
    }
    @Test
    void getBookByIdIsBad() throws SQLException {
    }
    @Test
    void AddBookIsGood() throws SQLException {
        // Create a book with a default ID (0)
        log.info("Starting add book to bookstore test");
        Book book = new Book(0, "Title", "Author", Date.valueOf("2024-08-19"), "Description", "ISBN123", true);

        // Use Mockito to mock the behavior of addBook
        when(dbManager.addBook(any(Book.class))).thenAnswer(invocation -> {
            Book argBook = invocation.getArgument(0);
            // Simulate auto-increment ID assignment
            argBook.setId(1); // Assuming the generated ID is 1 for simplicity
            return true;
        });

        boolean result = dbManager.addBook(book);

        // Assert that the result is true
        assertTrue(result);

        // Assert that the ID was set correctly
        assertEquals(1, book.getId());
        assertNotEquals(0, book.getId());
        log.info("Test AddBookIsGood completed");
    }
    @Test
    void AddBookIsBad() throws SQLException {
        // Create a book with a default ID (0)
        log.info("Starting add book to bookstore test");
        Book book = new Book(0, "Title", "Author", Date.valueOf("2024-08-19"), "Description", "ISBN123", true);

        // Use Mockito to mock the behavior of addBook
        when(dbManager.addBook(any(Book.class))).thenAnswer(invocation -> {
            Book argBook = invocation.getArgument(0);
            // Simulate auto-increment ID assignment
            argBook.setId(1); // Assuming the generated ID is 1 for simplicity
            return true;
        });

        boolean result = dbManager.addBook(book);

        // Assert that the result is true
        assertTrue(result);

        // Assert that the ID was set correctly
        assertEquals(1, book.getId());
        assertNotEquals(0, book.getId());
        log.info("Test AddBookIsGood completed");
    }
    @Test
    void deleteBookIsGood() throws SQLException {}
    @Test
    void deleteBookIsBad() throws SQLException {}
    @Test
    void deleteAllIsGood() throws SQLException {}
    @Test
    void updateBookAvailability() throws SQLException {}
    @Test
    void updateBookTitle() throws SQLException {}
    @Test
    void updateBookAuthor() throws SQLException {}
    @Test
    void updateBookPublicationYear() throws SQLException {}
    @Test
    void updateBookDescription() throws SQLException {}
    @Test
    void updateBookISBN() throws SQLException {
    }

    private void assertBookSetsEqualUsingStreams(Set<Book> expected, Set<Book> actual) {
        List<Book> expectedSorted = expected.stream()
                .sorted(Comparator.comparing(Book::getId))
                .toList();

        List<Book> actualSorted = actual.stream()
                .sorted(Comparator.comparing(Book::getId))
                .toList();

        assertEquals(expectedSorted.size(), actualSorted.size(), "size doesn't match");

        for (int i = 0; i < expectedSorted.size(); i++) {
            assertEquals(expectedSorted.get(i), actualSorted.get(i),
                    "Book at index not match " + expectedSorted.get(i) + ", Actual: " + actualSorted.get(i));
        }
    }

}
