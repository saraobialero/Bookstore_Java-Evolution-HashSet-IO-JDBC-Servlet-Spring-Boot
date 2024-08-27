import org.evpro.bookshopV2.Book;
import org.evpro.bookshopV2.BookException;
import org.evpro.bookshopV2.Bookshop;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.*;

public class BookshopTest {

    private Bookshop bookshop;
    private final String ORIGINAL_FILE_PATH = "src/main/resources/BookList.csv";
    private File tempFile;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        tempFile = tempFolder.newFile("TestBookList.csv");
        Files.copy(new File(ORIGINAL_FILE_PATH).toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        bookshop = new Bookshop();
        bookshop.readFile(tempFile.getPath());
    }

    @After
    public void tearDown() {
        tempFile.delete();
    }

    @Test
    public void addBook() {
        Book book = new Book("Robin Crusoe", "Daniel Defoe", "9780439023533", true);
        assertTrue(bookshop.addBook(book, tempFile.getPath()));
        assertEquals(11, bookshop.getBooks().size());
    }

    @Test
    public void giveBookIsGood() {
        String expectedISBN = "9780439023528";
        assertTrue(bookshop.giveBook(expectedISBN, tempFile.getPath()));
    }

    @Test
    public void giveBookIsBadNotAvailable() {
        String expectedISBN = "9780307474278";
        assertFalse(bookshop.giveBook(expectedISBN, tempFile.getPath()));
    }

    @Test
    public void giveBookIsBadNotExists() {
        String invalidISBN = "9780307474220";
        BookException exception = assertThrows(BookException.class, () -> {
            bookshop.giveBook(invalidISBN, tempFile.getPath());
        });
        String expectedMessage = "Book with ISBN " + invalidISBN + " not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void returnBookIsGood() {
        String expectedISBN = "9780307474278";
        assertTrue(bookshop.returnBook(expectedISBN, tempFile.getPath()));
    }

    @Test
    public void returnBookIsBadAvailable() {
        String expectedISBN = "9780439023528";
        assertFalse(bookshop.returnBook(expectedISBN, tempFile.getPath()));
    }

    @Test()
    public void returnBookIsBadNotExists() {
        String invalidISBN = "badISBN";
        BookException exception = assertThrows(BookException.class, () -> {
            bookshop.returnBook(invalidISBN, tempFile.getPath());
        });
        String expectedMessage = "Book with ISBN " + invalidISBN + " not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void searchBookIsGood() {
        String ISBN = "9780446310789";
        Book expectedBook = new Book("To Kill a Mockingbird", "Harper Lee", "9780446310789", true);
        Book actualBook = bookshop.searchBookByISBN(ISBN).get();
        assertNotNull(actualBook);
        assertEquals(expectedBook, actualBook);
    }

    @Test
    public void searchBookIsBad() {
        String ISBN = "9780060883287";
        Book expectedBook = new Book("To Kill a Mockingbird", "Harper Lee", "9780446310789", true);
        Book actualBook = bookshop.searchBookByISBN(ISBN).get();
        assertNotEquals(expectedBook, actualBook);
    }

    @Test()
    public void searchBookIsBadNotFound() {
        String invalidISBN = "badISBN";
        BookException exception = assertThrows(BookException.class, () -> {
            bookshop.searchBookByISBN(invalidISBN);
        });
        String expectedMessage = "Book with ISBN " + invalidISBN + " not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void fileContentAfterModification() throws IOException {
        Book book = new Book("Test Book", "Test Author", "1234567890", true);
        bookshop.addBook(book, tempFile.getPath());

        Bookshop newBookshop = new Bookshop();
        newBookshop.readFile(tempFile.getPath());
        assertTrue(newBookshop.getBooks().contains(book));
    }
}