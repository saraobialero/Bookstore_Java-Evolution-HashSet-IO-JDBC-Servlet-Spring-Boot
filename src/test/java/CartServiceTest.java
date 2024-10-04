import org.evpro.bookshopV5.exception.CartException;
import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.DTO.response.CartDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;
import org.evpro.bookshopV5.model.enums.LoanStatus;
import org.evpro.bookshopV5.repository.*;
import org.evpro.bookshopV5.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;
import java.util.Arrays;
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
        cart.setItems(new ArrayList<>());

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(cartRepository.findCartByUserEmail(email)).thenReturn(Optional.of(cart));

        CartDTO result = cartService.getCartForUser(email);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository).findCartByUserEmail(email);
    }

    @Test
    void testGetCartForUser_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertThrows(UserException.class, () -> cartService.getCartForUser(email));
    }

    @Test
    void testGetCartForUser_CartNotFound() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(cartRepository.findCartByUserEmail(email)).thenReturn(Optional.empty());

        assertThrows(CartException.class, () -> cartService.getCartForUser(email));
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
    void testAddItemToCart_ExistingItem() {
        String email = "test@example.com";
        Integer bookId = 1;
        int quantity = 2;

        User user = new User();
        user.setEmail(email);
        Book book = new Book();
        book.setId(bookId);
        Cart cart = new Cart();
        cart.setUser(user);
        CartItem existingItem = new CartItem();
        existingItem.setBook(book);
        existingItem.setQuantity(1);
        cart.setItems(new ArrayList<>(List.of(existingItem)));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartDTO result = cartService.addItemToCart(email, bookId, quantity);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(3, result.getItems().get(0).getQuantity());
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

    @Test
    void testUpdateCartItemQuantity_Success() {
        Integer cartItemId = 1;
        int newQuantity = 5;

        Book book = new Book();
        book.setId(1);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(2);
        cartItem.setBook(book);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(cartItem)));
        cartItem.setCart(cart);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDTO result = cartService.updateCartItemQuantity(cartItemId, newQuantity);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(newQuantity, result.getItems().get(0).getQuantity());
        verify(cartItemRepository).save(cartItem);
        verify(cartRepository).save(cart);
    }

    @Test
    void testClearCart_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(new CartItem(), new CartItem())));

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(cartRepository.findCartByUserEmail(email)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(cart.getItems());

        boolean result = cartService.clearCart(email);

        assertTrue(result);
        verify(cartItemRepository).deleteAll(cart.getItems());
        verify(cartRepository).save(cart);
    }

    @Test
    void testMoveCartToLoan_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Cart cart = new Cart();
        cart.setUser(user);
        Book book = new Book();
        book.setId(1);
        book.setQuantity(5);
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        when(cartRepository.findCartByUserEmail(email)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(cart.getItems());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            if (loan.getId() == null) {
                loan.setId(1);
            }
            return loan;
        });
        when(loanDetailRepository.save(any(LoanDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanDTO result = cartService.moveCartToLoan(email);

        assertNotNull(result);
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        verify(loanRepository, times(2)).save(any(Loan.class));
        verify(loanDetailRepository).save(any(LoanDetails.class));
        verify(cartItemRepository).deleteAll(cart.getItems());
        verify(bookRepository).save(book);
        assertEquals(3, book.getQuantity());
    }

    @Test
    void testGetAllCarts_Success() {
        Cart cart1 = new Cart();
        cart1.setId(1);
        cart1.setItems(new ArrayList<>());

        Cart cart2 = new Cart();
        cart2.setId(2);
        cart2.setItems(new ArrayList<>());

        List<Cart> carts = Arrays.asList(cart1, cart2);
        when(cartRepository.findAll()).thenReturn(carts);

        List<CartDTO> result = cartService.getAllCarts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllCarts_EmptyList() {
        when(cartRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(CartException.class, () -> cartService.getAllCarts());
    }

    @Test
    void testDeleteCartById_Success() {
        Integer cartId = 1;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new ArrayList<>());
        User user = new User();
        cart.setUser(user);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(new ArrayList<>());
        when(cartRepository.existsById(cartId)).thenReturn(false);

        boolean result = cartService.deleteCartById(cartId);

        assertTrue(result);
        verify(cartRepository).delete(cart);
        verify(userRepository).save(user);
        verify(cartRepository).existsById(cartId);
    }

    @Test
    void testDeleteAllCarts_Success() {
        boolean result = cartService.deleteAllCarts();

        assertTrue(result);
        verify(cartItemRepository).deleteAllInBatch();
        verify(cartRepository).deleteAllInBatch();
    }




}