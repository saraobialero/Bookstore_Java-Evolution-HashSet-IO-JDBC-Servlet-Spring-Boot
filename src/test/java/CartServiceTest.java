import org.evpro.bookshopV5.exception.CartException;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.DTO.response.CartDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.repository.*;
import org.evpro.bookshopV5.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanDetailRepository loanDetailRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartForUser_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Cart cart = new Cart();
        cart.setUser(user);

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(cartRepository.findCartByUserEmail(email)).thenReturn(Optional.of(cart));

        CartDTO result = cartService.getCartForUser(email);

        assertNotNull(result);
        verify(cartRepository).findCartByUserEmail(email);
    }

    @Test
    void testAddItemToCart_NewItem() {
        String email = "test@example.com";
        Integer bookId = 1;
        int quantity = 2;

        User user = new User();
        user.setEmail(email);
        Book book = new Book();
        book.setId(bookId);
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartDTO result = cartService.addItemToCart(email, bookId, quantity);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void testRemoveItemFromCart_Success() {
        String email = "test@example.com";
        Integer cartItemId = 1;

        User user = new User();
        user.setEmail(email);
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cart.getItems().add(cartItem);

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartRepository.findCartByCartItemId(cartItemId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartDTO result = cartService.removeItemFromCart(email, cartItemId);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(cartItemRepository).delete(cartItem);
    }

}