import org.evpro.bookshopV5.exception.LoanException;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.DTO.request.AddItemToLoanRequest;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDetailsDTO;
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
        LoanDetails loanDetail1 = new LoanDetails();
        LoanDetails loanDetail2 = new LoanDetails();
        loan.setLoanDetails(new HashSet<>(Arrays.asList(loanDetail1, loanDetail2)));

        when(loanRepository.findLoanById(loanId)).thenReturn(Optional.of(loan));

        Set<LoanDetailsDTO> result = loanService.getLoanDetailsByLoanId(loanId);

        assertEquals(2, result.size());
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


}