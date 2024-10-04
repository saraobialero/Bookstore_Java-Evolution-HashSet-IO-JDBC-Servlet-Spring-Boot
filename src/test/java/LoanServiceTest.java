import org.evpro.bookshopV5.exception.LoanException;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.DTO.request.AddItemToLoanRequest;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDetailsDTO;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.model.enums.LoanStatus;
import org.evpro.bookshopV5.repository.*;
import org.evpro.bookshopV5.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanDetailRepository loanDetailRepository;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDirectLoan_Success() {
        String email = "test@example.com";
        AddItemToLoanRequest request = new AddItemToLoanRequest();
        request.setBookId(1);
        request.setQuantity(1);

        User user = new User();
        user.setEmail(email);
        Book book = new Book();
        book.setId(1);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setId(1);
            return loan;
        });

        LoanDTO result = loanService.createDirectLoan(email, request);

        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        verify(loanRepository).save(any(Loan.class));
        verify(loanDetailRepository).save(any(LoanDetails.class));
    }

    @Test
    void testGetMyLoans_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Loan loan1 = new Loan();
        loan1.setId(1);
        Loan loan2 = new Loan();
        loan2.setId(2);
        user.setLoans(Arrays.asList(loan1, loan2));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        List<LoanDTO> result = loanService.getMyLoans(email);

        assertEquals(2, result.size());
    }

    @Test
    void testReturnLoan_Success() {
        Integer loanId = 1;
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setUser(user);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLoanDate(LocalDate.now().minusDays(7));

        when(loanRepository.findLoanById(loanId)).thenReturn(Optional.of(loan));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanDTO result = loanService.returnLoan(loanId, email);

        assertNotNull(result);
        assertEquals(LoanStatus.CLOSED, result.getStatus());
        assertNotNull(result.getReturnDate());
        verify(loanRepository).save(loan);
    }

    @Test
    void testGetLoanDetailsByLoanId_Success() {
        Integer loanId = 1;
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setReturnDate(null);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);
        LoanDetails loanDetail1 = new LoanDetails();
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Title");
        book1.setGenre(BookGenre.ROMANCE);
        book1.setAuthor("Author");
        book1.setISBN("1234567890");
        book1.setQuantity(5);
        book1.setPublicationYear(LocalDate.now().minusDays(10));

        Book book2 = new Book();
        book1.setId(2);
        book1.setTitle("Title");
        book1.setGenre(BookGenre.ROMANCE);
        book1.setAuthor("Author");
        book1.setISBN("1234567893");
        book1.setQuantity(5);
        book1.setPublicationYear(LocalDate.now().minusDays(10));

        LoanDetails loanDetail2 = new LoanDetails();
        loanDetail1.setBook(book1);
        loanDetail2.setBook(book2);


        loan.setLoanDetails(new HashSet<>(Arrays.asList(loanDetail1, loanDetail2)));

        when(loanRepository.findLoanById(loanId)).thenReturn(Optional.of(loan));

        Set<LoanDetailsDTO> result = loanService.getLoanDetailsByLoanId(loanId);

        assertEquals(1, result.size());
    }

    @Test
    void testExtendLoanDueDate_Success() {
        Integer loanId = 1;
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setDueDate(LocalDate.now());

        when(loanRepository.findLoanById(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LocalDate result = loanService.extendLoanDueDate(loanId);

        assertEquals(LocalDate.now().plusDays(14), result);
        verify(loanRepository).save(loan);
    }

    @Test
    void testGetMyLastLoan_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Loan loan1 = new Loan();
        loan1.setId(1);
        Loan loan2 = new Loan();
        loan2.setId(2);
        user.setLoans(Arrays.asList(loan1, loan2));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        LoanDTO result = loanService.getMyLastLoan(email);

        assertNotNull(result);
        assertEquals(2, result.getId());
    }

    @Test
    void testGetMyActiveLoans_Success() {
        String email = "test@example.com";
        List<Loan> activeLoans = Arrays.asList(new Loan(), new Loan());

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(loanRepository.findActiveLoansFrUsers(email)).thenReturn(activeLoans);

        List<LoanDTO> result = loanService.getMyActiveLoans(email);

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllLoans_Success() {
        List<Loan> allLoans = Arrays.asList(new Loan(), new Loan(), new Loan());

        when(loanRepository.findAll()).thenReturn(allLoans);

        List<LoanDTO> result = loanService.getAllLoans();

        assertEquals(3, result.size());
    }

    @Test
    void testGetUserLoan_Success() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        List<Loan> userLoans = Arrays.asList(new Loan(), new Loan());
        user.setLoans(userLoans);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<LoanDTO> result = loanService.getUserLoan(userId);

        assertEquals(2, result.size());
    }

    @Test
    void testDeleteAllLoans_Success() {
        List<Loan> allLoans = Arrays.asList(new Loan(), new Loan());

        when(loanRepository.findAll()).thenReturn(allLoans);

        boolean result = loanService.deleteAllLoans();

        assertTrue(result);
        verify(loanRepository).deleteAll(allLoans);
    }

    @Test
    void testDeleteLoanById_Success() {
        Integer loanId = 1;
        Loan loan = new Loan();
        loan.setId(loanId);

        when(loanRepository.findLoanById(loanId)).thenReturn(Optional.of(loan));

        boolean result = loanService.deleteLoanById(loanId);

        assertTrue(result);
        verify(loanRepository).delete(loan);
    }

    @Test
    void testSendLoanReminders_Success() {
        LocalDate reminderDate = LocalDate.now().plusDays(3);

        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setName("User 1");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setName("User 2");

        Loan loan1 = new Loan();
        loan1.setUser(user1);
        loan1.setDueDate(reminderDate);

        Loan loan2 = new Loan();
        loan2.setUser(user2);
        loan2.setDueDate(reminderDate);

        List<Loan> loansToRemind = Arrays.asList(loan1, loan2);

        when(loanRepository.findByDueDateAndStatus(eq(reminderDate), eq(LoanStatus.ACTIVE))).thenReturn(loansToRemind);

        boolean result = loanService.sendLoanReminders();

        assertTrue(result);
        verify(loanRepository).findByDueDateAndStatus(eq(reminderDate), eq(LoanStatus.ACTIVE));
    }

    @Test
    void testGetOverdueLoans_Success() {
        List<Loan> overdueLoans = Arrays.asList(new Loan(), new Loan());

        when(loanRepository.findOverdueLoans()).thenReturn(overdueLoans);

        List<LoanDTO> result = loanService.getOverdueLoans();

        assertEquals(2, result.size());
    }

    @Test
    void testGetOverdueLoans_EmptyList() {
        when(loanRepository.findOverdueLoans()).thenReturn(new ArrayList<>());

        assertThrows(LoanException.class, () -> loanService.getOverdueLoans());
    }


}