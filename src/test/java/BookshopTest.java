import org.evpro.bookshopV1.Book;
import org.evpro.bookshopV1.BookException;
import org.evpro.bookshopV1.Bookshop;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class BookshopTest {

    private Bookshop bookshop;
    private Book book1, book2, book3, book4, book5;
    private Set<Book> books;

    @Before
    public void setUp() {
    book1  = new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", true);
    book2 = new Book("The Catcher in the Rye", "J.D. Salinger", "9780316769174", false);
    book3 = new Book("The Hunger Games", "Suzanne Collins", "9780439023528", true);
    book4 = new Book("One Hundred Years of Solitude", "Gabriel García Márquez",	"9780060883287", true);
    book5 = new Book("1984", "George Orwell","9780451524935",true);

    books = new HashSet<>(Arrays.asList(book1, book2, book3, book4, book5));
    bookshop = new Bookshop(books);
    }

    @Test
    public void addBookIsGood() {
        Book book6 = new Book("The Da Vinci Code", "Dan Brown","9780307474278",true);
        int initialSize = bookshop.getBooks().size();

        assertTrue(bookshop.addBook(book6));
        assertEquals(initialSize + 1, bookshop.getBooks().size());
        assertTrue(bookshop.getBooks().contains(book6));
    }

    @Test
    public void giveBookIsGood() {
        Book book7 = new Book("Brave New World", "Aldous Huxley","9780060850524",true);
        bookshop.addBook(book7);

        assertTrue(bookshop.giveBook(book7.getISBN()));
    }

    @Test()
    public void giveBookIsBadNotExists() {
        Book book8 = new Book("Book not added", "author", "9780060850533", true);
        String invalidISBN = book8.getISBN();

        BookException exception = assertThrows(BookException.class, () -> {
            bookshop.giveBook(invalidISBN);
        });

        String expectedMessage = "Book with ISBN " + invalidISBN + " not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test()
    public void giveBookIsBadNotAvailable() {
        Book book9 = new Book("To Kill a Mockingbird", "Harper Lee", "9780446310789", false);
        bookshop.addBook(book9);

        BookException exception = assertThrows(BookException.class, () -> {
            bookshop.giveBook(book9.getISBN());
        });

        String expectedMessage = "Book is not available";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void returnBookIsGood() {
        Book book10 = new Book("Pride and Prejudice", "Jane Austen","9780141439518",false);
        bookshop.addBook(book10);
        assertTrue(bookshop.returnBook(book10.getISBN()));
    }

    @Test()
    public void returnBookIsBadNotExists() {
        Book book8 = new Book("Book not added", "author", "9780060850533", true);
        String invalidISBN = book8.getISBN();

        BookException exception = assertThrows(BookException.class, () -> {
            bookshop.returnBook(invalidISBN);
        });

        String expectedMessage = "Book with ISBN " + invalidISBN + " not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test()
    public void giveBookIsBadIsAvailable() {
        Book book11 = new Book("To Kill a Mockingbird", "Harper Lee", "9780446310789", true);
        bookshop.addBook(book11);

        BookException exception = assertThrows(BookException.class, () -> {
            bookshop.returnBook(book11.getISBN());
        });

        String expectedMessage = "Book is already available";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}