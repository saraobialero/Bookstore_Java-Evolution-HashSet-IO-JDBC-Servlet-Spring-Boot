import lombok.extern.slf4j.Slf4j;
import org.interview.bookshopV3.db.DatabaseManager;
import org.interview.bookshopV3.model.Book;
import org.interview.bookshopV3.model.Bookshop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class BookshopTest {

    @Mock
    private DatabaseManager dbManager; //To simulate db manager

    @InjectMocks
    private Bookshop bookshop; //Simulate class bookshop and inject mock db in this class

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //Initialize mock before tests
    }

    //Testing Bookshop functionality for users
    @Test
    void getPublicCatalogIsGood() throws SQLException {
    }

    @Test
    void getPublicCatalogIsBad() throws SQLException {
    }

    @Test
    void giveBookIsGood() throws SQLException {
    }

    @Test
    void giveBookIsBadNotAvailable() throws SQLException {
    }

    @Test
    void giveBookIsBadNotExists() throws SQLException {
    }

    @Test
    void returnBookIsGood() throws SQLException {
    }

    @Test
    void returnBookIsBadNotAvailable() throws SQLException {
    }

    @Test
    void returnBookIsBadNotExists() throws SQLException {
    }

    @Test
    void searchBookIsGood() throws SQLException {
    }

    @Test
    void searchBookIsBadMissingInfo() throws SQLException {
    }

    @Test
    void searchBookIsBadNotExists() throws SQLException {
    }




}
