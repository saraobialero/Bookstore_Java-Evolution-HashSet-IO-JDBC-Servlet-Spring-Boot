import org.interview.bookshopV2.Book;
import org.interview.bookshopV2.BookException;
import org.interview.bookshopV2.Bookshop;
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
    private final String ORIGINAL_FILE_PATH = "src/main/java/org/interview/bookshopV2/BookList.csv";
    private File tempFile;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        // Crea un file temporaneo per i test
        tempFile = tempFolder.newFile("TestBookList.csv");

        // Copia il contenuto del file originale nel file temporaneo
        Files.copy(new File(ORIGINAL_FILE_PATH).toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        bookshop = new Bookshop();
        bookshop.readFile(tempFile.getPath());
    }

    @After
    public void tearDown() {
        // Elimina il file temporaneo dopo ogni test
        tempFile.delete();
    }

    @Test
    public void testAddBook() throws IOException {
        Book book = new Book("Robin Crusoe", "Daniel Defoe", "9780439023533", true);
        assertTrue(bookshop.addBook(book, tempFile.getPath()));
        assertEquals(11, bookshop.getBooks().size());
    }

    @Test
    public void testGiveBookIsGood() {
        String expectedISBN = "9780439023528";
        assertTrue(bookshop.giveBook(expectedISBN, tempFile.getPath()));
    }

    @Test
    public void testGiveBookIsBadNotAvailable() {
        String expectedISBN = "9780307474278";
        assertFalse(bookshop.giveBook(expectedISBN, tempFile.getPath()));
    }

    @Test(expected = BookException.class)
    public void testGiveBookIsBadNotExists() {
        String INVALID_ISBN = "9780307474220";
        bookshop.giveBook(INVALID_ISBN, tempFile.getPath());
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

    @Test(expected = BookException.class)
    public void testReturnBookIsBadNotExists() {
        String INVALID_ISBN = "badISBN";
        bookshop.returnBook(INVALID_ISBN, tempFile.getPath());
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

    @Test(expected = BookException.class)
    public void searchBookIsBadNotFound() {
        String INVALID_ISBN = "badISBN";
        bookshop.searchBookByISBN(INVALID_ISBN);
    }

    @Test
    public void testFileContentAfterModification() throws IOException {
        // Test che verifica il contenuto del file dopo una modifica
        Book book = new Book("Test Book", "Test Author", "1234567890", true);
        bookshop.addBook(book, tempFile.getPath());

        // Rileggi il file e verifica che il nuovo libro sia stato aggiunto
        Bookshop newBookshop = new Bookshop();
        newBookshop.readFile(tempFile.getPath());
        assertTrue(newBookshop.getBooks().contains(book));
    }
}