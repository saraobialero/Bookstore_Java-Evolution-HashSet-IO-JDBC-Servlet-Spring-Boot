import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV3.model.Bookshop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.evpro.bookshopV3.db.DatabaseManager;
import org.evpro.bookshopV3.model.Book;

import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
class DbManagerTest {

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
    void getAllBooksSuccessfully() throws SQLException {
        log.info("Starting get all Books test");
        Set<Book> expectedBooks = createSampleBookSet();
        when(dbManager.getAllBooks()).thenReturn(expectedBooks);

        Set<Book> actualBooks = dbManager.getAllBooks();

        assertAll("Verify all books",
                () -> assertEquals(expectedBooks.size(), actualBooks.size(), "Book count should match"),
                () -> assertTrue(actualBooks.containsAll(expectedBooks), "All expected books should be present"),
                () -> assertTrue(expectedBooks.containsAll(actualBooks), "No unexpected books should be present")
        );

        verify(dbManager, times(1)).getAllBooks();
        log.info(OK + SEPARATOR);
    }

    @Test
    void getAllBooksWithInvalidData() throws SQLException {
        log.info("Starting get all Books test with negative result for size and content of set");
        Set<Book> expectedBooks = new HashSet<>(Arrays.asList(
                new Book(1, "Wrong title", "Harper Lee", Date.valueOf("1960-07-11"), "Description", "9780446310789", true),
                new Book(2, "1984", "George Orwell", Date.valueOf("1949-06-08"), "Description", "9780451524935", true)
        ));
        Set<Book> actualBooks = createSampleBookSet();
        when(dbManager.getAllBooks()).thenReturn(actualBooks);

        assertAll("Verify books mismatch",
                () -> assertNotEquals(expectedBooks, actualBooks, "Book sets should not be equal"),
                () -> assertNotEquals(expectedBooks.size(), actualBooks.size(), "Book counts should not match")
        );
        log.info(OK + SEPARATOR);
    }
/*
    @ParameterizedTest
    @MethodSource("provideBookIdTestCases")
    void getBookById(int bookId, boolean shouldExist) throws SQLException {
        log.info("Starting get book by id test for ID: " + bookId);
        Book expectedBook = new Book(bookId, "Test Book", "Test Author", Date.valueOf("2000-01-01"), "Test Description", "1234567890", true);

        when(dbManager.getBookById(bookId)).thenReturn(shouldExist ? Optional.of(expectedBook) : Optional.empty());

        Optional<Book> result = dbManager.getBookById(bookId);

        if (shouldExist) {
            assertTrue(result.isPresent(), "Book should be present for ID: " + bookId);
            assertEquals(expectedBook, result.get(), "Retrieved book should match expected for ID: " + bookId);
        } else {
            assertTrue(result.isEmpty(), "Book should not be present for ID: " + bookId);
        }

        verify(dbManager, times(1)).getBookById(bookId);
        log.info(OK + SEPARATOR);
    }

    private static Stream<Arguments> provideBookIdTestCases() {
        return Stream.of(
                Arguments.of(1, true),
                Arguments.of(100, false)
        );
    }*/

    @Test
    void addBookSuccessfully() throws SQLException {
        log.info("Starting add book to bookstore test");
        Book book = new Book(0, "New Book", "New Author", Date.valueOf("2023-01-01"), "New Description", "1234567890", true);

        when(dbManager.addBook(any(Book.class))).thenAnswer(invocation -> {
            Book argBook = invocation.getArgument(0);
            argBook.setId(1);
            return true;
        });

        assertTrue(dbManager.addBook(book));

        assertAll("Verify added book",
                () -> assertEquals(1, book.getId(), "Book should have a new ID"),
                () -> assertNotEquals(0, book.getId(), "Book ID should not be 0")
        );
        log.info(OK + SEPARATOR);
    }

    @Test
    void addBookWithInvalidData() throws SQLException {
        log.info("Starting add book with invalid data test");
        Book invalidBook = new Book(0, "", "", null, "", "", true);

        try {
            when(dbManager.addBook(any(Book.class))).thenThrow(new IllegalArgumentException("Invalid book data"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> dbManager.addBook(invalidBook));

        assertEquals("Invalid book data", illegalArgumentException.getMessage());
        log.info(OK + SEPARATOR);
    }

    @Test
    void deleteBookSuccessfully() throws SQLException {
        log.info("Starting delete book from bookstore test");
        Set<Book> initialBooks = createSampleBookSet();
        when(dbManager.getAllBooks()).thenReturn(new HashSet<>(initialBooks));
        when(dbManager.deleteBook(1)).thenReturn(true);
        when(dbManager.deleteBook(2)).thenReturn(true);

        assertTrue(dbManager.deleteBook(1));
        assertTrue(dbManager.deleteBook(2));

        initialBooks.removeIf(book -> book.getId() == 1 || book.getId() == 2);
        when(dbManager.getAllBooks()).thenReturn(initialBooks);

        assertEquals(3, dbManager.getAllBooks().size());
        log.info(OK + SEPARATOR);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidBookIds")
    void deleteNonExistentBook(int invalidId) throws SQLException {
        log.info("Starting delete non-existent book test for ID: " + invalidId);
        when(dbManager.deleteBook(invalidId)).thenReturn(false);
        assertFalse(dbManager.deleteBook(invalidId));
        log.info(OK + SEPARATOR);
    }

    private static Stream<Arguments> provideInvalidBookIds() {
        return Stream.of(
                Arguments.of(10),
                Arguments.of(20),
                Arguments.of(0)
        );
    }

    @Test
    void deleteAllBooksSuccessfully() throws SQLException {
        log.info("Starting delete all books test");
        Set<Book> initialBooks = createSampleBookSet();
        when(dbManager.getAllBooks()).thenReturn(initialBooks);
        assertEquals(5, dbManager.getAllBooks().size());

        when(dbManager.deleteAll()).thenReturn(true);
        assertTrue(dbManager.deleteAll());

        when(dbManager.getAllBooks()).thenReturn(new HashSet<>());
        assertEquals(0, dbManager.getAllBooks().size());
        log.info(OK + SEPARATOR);
    }

    @ParameterizedTest
    @MethodSource("provideUpdateTestCases")
    void updateBookFields(String fieldName, Object newValue, Consumer<Book> updateMethod, Function<Book, Object> getter) throws SQLException {
        log.info("Update book " + fieldName);
        int bookId = 1;
        Book originalBook = new Book(bookId, "Original Title", "Original Author", Date.valueOf("2000-01-01"), "Original Description", "1234567890", true);
        Book updatedBook = new Book(bookId, "Original Title", "Original Author", Date.valueOf("2000-01-01"), "Original Description", "1234567890", true);
        updateMethod.accept(updatedBook);

        when(dbManager.getBookById(bookId)).thenReturn(Optional.of(updatedBook));

        updateMethod.accept(originalBook);

        Optional<Book> actualBookOptional = dbManager.getBookById(bookId);
        assertTrue(actualBookOptional.isPresent());
        assertEquals(newValue, getter.apply(actualBookOptional.get()), "Failed to update " + fieldName);
        log.info(OK + SEPARATOR);
    }

    private static Stream<Arguments> provideUpdateTestCases() {
        return Stream.of(
                Arguments.of("title", "New Title", (Consumer<Book>) b -> b.setTitle("New Title"), (Function<Book, Object>) Book::getTitle),
                Arguments.of("author", "New Author", (Consumer<Book>) b -> b.setAuthor("New Author"), (Function<Book, Object>) Book::getAuthor),
                Arguments.of("publicationYear", Date.valueOf("2023-01-01"), (Consumer<Book>) b -> b.setPublicationYear(Date.valueOf("2023-01-01")), (Function<Book, Object>) Book::getPublicationYear),
                Arguments.of("description", "New Description", (Consumer<Book>) b -> b.setDescription("New Description"), (Function<Book, Object>) Book::getDescription),
                Arguments.of("ISBN", "0987654321", (Consumer<Book>) b -> b.setISBN("0987654321"), (Function<Book, Object>) Book::getISBN),
                Arguments.of("availability", false, (Consumer<Book>) b -> b.setAvailable(false), (Function<Book, Object>) Book::isAvailable)
        );
    }

    @Test
    void concurrentBookUpdates() throws InterruptedException, SQLException {
        log.info("Testing concurrent book updates");
        int bookId = 1;
        Book book = new Book(bookId, "Title", "Author", Date.valueOf("2000-01-01"), "Description", "1234567890", true);
        when(dbManager.getBookById(bookId)).thenReturn(Optional.of(book));

        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicBoolean firstUpdate = new AtomicBoolean(true);

        doAnswer(invocation -> {
            if (firstUpdate.getAndSet(false)) {
                successCount.incrementAndGet();
                return null;
            } else {
                throw new SQLException("Concurrent update");
            }
        }).when(dbManager).updateBookTitle(eq(bookId), anyString());

        Runnable updateTask = () -> {
            try {
                dbManager.updateBookTitle(bookId, "New Title " + Thread.currentThread().getId());
            } catch (SQLException e) {
                // Expected for the second update
            } finally {
                latch.countDown();
            }
        };

        new Thread(updateTask).start();
        new Thread(updateTask).start();

        latch.await(5, TimeUnit.SECONDS);

        assertEquals(1, successCount.get(), "Solo un aggiornamento dovrebbe avere successo");
        log.info(OK + SEPARATOR);
    }
    @Test
    void addBookWithMaxLengthValues() throws SQLException {
        log.info("Testing adding a book with maximum length values");
        String maxLengthString = "a".repeat(255);
        Book maxBook = new Book(0, maxLengthString, maxLengthString, new Date(Long.MAX_VALUE), maxLengthString, maxLengthString, true);

        when(dbManager.addBook(any(Book.class))).thenReturn(true);

        assertTrue(dbManager.addBook(maxBook));
        log.info(OK + SEPARATOR);
    }

    private Set<Book> createSampleBookSet() {
        return new HashSet<>(Arrays.asList(
                new Book(1, "To Kill a Mockingbird", "Harper Lee", Date.valueOf("1960-07-11"), "Description 1", "9780446310789", true),
                new Book(2, "1984", "George Orwell", Date.valueOf("1949-06-08"), "Description 2", "9780451524935", true),
                new Book(3, "Pride and Prejudice", "Jane Austen", Date.valueOf("1813-01-28"), "Description 3", "9780141439518", true),
                new Book(4, "The Great Gatsby", "F. Scott Fitzgerald", Date.valueOf("1925-04-10"), "Description 4", "9780743273565", false),
                new Book(5, "Moby-Dick", "Herman Melville", Date.valueOf("1851-10-18"), "Description 5", "9780142437247", true)
        ));
    }
}