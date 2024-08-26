import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.interview.bookshopV3.db.DatabaseManager;
import org.interview.bookshopV3.model.Book;
import org.interview.bookshopV3.model.Bookshop;

import java.sql.Date;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookshopTest {

    @Mock
    private DatabaseManager dbManager; //To simulate db manager

    @InjectMocks
    private Bookshop bookshop; //Simulate class bookshop and inject mock db in this class

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //Initialize mock before tests
    }

    @Test
    public void testAddBookIsGood() throws SQLException {
        // Create a book with a default ID (0)
        Book book = new Book(0, "Title", "Author", Date.valueOf("2024-08-19"), "Description", "ISBN123", true);

        // Use Mockito to mock the behavior of addBook
        when(dbManager.addBook(any(Book.class))).thenAnswer(invocation -> {
            Book argBook = invocation.getArgument(0);
            // Simulate auto-increment ID assignment
            argBook.setId(1); // Assuming the generated ID is 1 for simplicity
            return true;
        });

        boolean result = bookshop.addBook(book);

        // Assert that the result is true
        assertTrue(result);

        // Assert that the ID was set correctly
        assertEquals(1, book.getId());
        assertNotEquals(0, book.getId());
    }
}
