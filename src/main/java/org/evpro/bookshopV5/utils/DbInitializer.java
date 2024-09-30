package org.evpro.bookshopV5.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.enums.BookGenre;
import org.evpro.bookshopV5.model.enums.LoanStatus;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final LoanRepository loanRepository;
    private final LoanDetailRepository loanDetailRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initializeRoles();
        initializeUsers();
        initializeBooks();
        initializeCartsWithItems();
        initializeLoansWithLoanDetails();
    }

    private void initializeRoles() {
        log.info("Initializing roles...");
        List.of(RoleCode.ROLE_USER, RoleCode.ROLE_ADMIN).forEach(roleCode -> {
            if (!roleRepository.existsByRoleCode(roleCode)) {
                roleRepository.save(Role.builder().role(roleCode).build());
                log.info("Role {} created", roleCode);
            }
        });
    }

    private void initializeUsers() {
        log.info("Initializing users...");
        if (userRepository.count() == 0) {
            Role roleAdmin = roleRepository.findByRoleCode(RoleCode.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            Role roleUser = roleRepository.findByRoleCode(RoleCode.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            List<User> users = List.of(
                    User.builder()
                            .email("admin@bookshop.com")
                            .password(passwordEncoder.encode("adminPass123"))
                            .name("Admin")
                            .surname("User")
                            .roles(List.of(roleAdmin))
                            .active(true)
                            .build(),
                    User.builder()
                            .email("john@example.com")
                            .password(passwordEncoder.encode("userPass123"))
                            .name("John")
                            .surname("Doe")
                            .roles(List.of(roleUser))
                            .active(true)
                            .build(),
                    User.builder()
                            .email("jane@example.com")
                            .password(passwordEncoder.encode("userPass456"))
                            .name("Jane")
                            .surname("Smith")
                            .roles(List.of(roleUser))
                            .active(false)
                            .build(),
                    User.builder()
                            .email("bob@example.com")
                            .password(passwordEncoder.encode("userPass789"))
                            .name("Bob")
                            .surname("Johnson")
                            .roles(List.of(roleUser))
                            .active(true)
                            .build()
            );
            userRepository.saveAll(users);
            log.info("{} users created", users.size());
        }
    }

    private void initializeBooks() {
        log.info("Initializing books...");
        if (bookRepository.count() == 0) {
            List<Book> books = List.of(
                    Book.builder()
                            .author("Harper Lee")
                            .title("To Kill a Mockingbird")
                            .publicationYear(LocalDate.of(1960, 7, 11))
                            .description("A classic of modern American literature")
                            .ISBN("9780446310789")
                            .quantity(20)
                            .available(true)
                            .genre(BookGenre.THRILLER)
                            .build(),
                    Book.builder()
                            .author("George Orwell")
                            .title("1984")
                            .publicationYear(LocalDate.of(1949, 6, 8))
                            .description("A dystopian social science fiction novel")
                            .ISBN("9780451524935")
                            .quantity(15)
                            .available(true)
                            .genre(BookGenre.NOVEL)
                            .build(),
                    Book.builder()
                            .author("J.K. Rowling")
                            .title("Harry Potter and the Philosopher's Stone")
                            .publicationYear(LocalDate.of(1997, 6, 26))
                            .description("The first book in the Harry Potter series")
                            .ISBN("9780747532699")
                            .quantity(25)
                            .available(true)
                            .genre(BookGenre.NOVEL)
                            .build(),
                    Book.builder()
                            .author("J.R.R. Tolkien")
                            .title("The Lord of the Rings")
                            .publicationYear(LocalDate.of(1954, 7, 29))
                            .description("An epic high-fantasy novel")
                            .ISBN("9780618640157")
                            .quantity(10)
                            .available(true)
                            .build(),
                    Book.builder()
                            .author("Jane Austen")
                            .title("Pride and Prejudice")
                            .publicationYear(LocalDate.of(1813, 1, 28))
                            .description("A romantic novel of manners")
                            .ISBN("9780141439518")
                            .quantity(12)
                            .available(true)
                            .genre(BookGenre.NOVEL)
                            .build()
            );
            bookRepository.saveAll(books);
            log.info("{} books created", books.size());
        }
    }

    private void initializeCartsWithItems() {
        log.info("Initializing carts and cart items...");
        if (cartRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<Book> books = bookRepository.findAll();

            for (int i = 1; i < users.size(); i++) {
                User user = users.get(i);
                Cart cart = Cart.builder()
                        .user(user)
                        .items(new ArrayList<>())
                        .createdDate(LocalDate.now())
                        .build();
                cartRepository.save(cart);

                for (int j = 0; j < 2; j++) {
                    Book book = books.get((i + j) % books.size());
                    CartItem cartItem = CartItem.builder()
                            .cart(cart)
                            .book(book)
                            .quantity(1)
                            .build();
                    cartItemRepository.save(cartItem);
                    cart.getItems().add(cartItem);
                }
                cartRepository.save(cart);
                log.info("Cart with items created for user: {}", user.getEmail());
            }
        }
    }

    private void initializeLoansWithLoanDetails() {
        log.info("Initializing loans and loan details...");
        if (loanRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<Book> books = bookRepository.findAll();

            for (int i = 1; i < users.size(); i++) {
                User user = users.get(i);
                Loan loan = Loan.builder()
                        .user(user)
                        .loanDate(LocalDate.now().minusDays(i * 2))
                        .dueDate(LocalDate.now().plusDays(14 - i * 2))
                        .status(LoanStatus.ACTIVE)
                        .loanDetails(new HashSet<>())
                        .build();
                loanRepository.save(loan);

                for (int j = 0; j < 2; j++) {
                    Book book = books.get((i + j) % books.size());
                    LoanDetail loanDetail = LoanDetail.builder()
                            .loan(loan)
                            .book(book)
                            .quantity(1)
                            .build();
                    loanDetailRepository.save(loanDetail);
                    loan.getLoanDetails().add(loanDetail);
                }
                loanRepository.save(loan);
                log.info("Loan with details created for user: {}", user.getEmail());
            }
        }
    }
}