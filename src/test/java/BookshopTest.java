import org.evpro.bookshopV1.Book;
import org.evpro.bookshopV1.Bookshop;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class BookshopTest {

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

}