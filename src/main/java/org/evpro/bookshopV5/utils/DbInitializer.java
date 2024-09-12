package org.evpro.bookshopV5.utils;

import org.evpro.bookshopV5.data.response.ErrorResponse;
import org.evpro.bookshopV5.exception.BookException;
import org.evpro.bookshopV5.exception.UserException;
import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.enums.CartStatus;
import org.evpro.bookshopV5.model.enums.ErrorCode;
import org.evpro.bookshopV5.model.enums.LoanStatus;
import org.evpro.bookshopV5.model.enums.RoleCode;
import org.evpro.bookshopV5.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//TODO: complete db initializer and add items
@Component
public class DbInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final  PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final LoanRepository loanRepository;
    private final LoanDetailRepository loanDetailRepository;

    public DbInitializer(UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         BookRepository bookRepository,
                         CartRepository cartRepository,
                         CartItemRepository cartItemRepository,
                         LoanRepository loanRepository,
                         LoanDetailRepository loanDetailRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.loanRepository = loanRepository;
        this.loanDetailRepository = loanDetailRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeBooks();
        initializeCartWithItems();
        initializeLoansWithLoanDetails();
        }


    private void initializeUsers() {
        if (!roleRepository.existsByRoleCode(RoleCode.ROLE_USER)) {
            roleRepository.save(Role.builder().role(RoleCode.ROLE_USER).build());
        }
        if (!roleRepository.existsByRoleCode(RoleCode.ROLE_ADMIN)) {
            roleRepository.save(Role.builder().role(RoleCode.ROLE_ADMIN).build());
        }

        Role roleAdmin = roleRepository.findByRoleCode(RoleCode.ROLE_ADMIN).orElseThrow();
        Role roleUser = roleRepository.findByRoleCode(RoleCode.ROLE_USER).orElseThrow();
        if(userRepository.count() == 0) {
            userRepository.saveAll(List.of(
        userRepository.save(User.builder()
                .email("test@mail.com")
                .password(passwordEncoder.encode("psw1234"))
                .name("Test")
                .surname("LastName")
                .roles(List.of(roleAdmin))
                .build()),

        userRepository.save(User.builder()
                .email("anne@mail.com")
                .password(passwordEncoder.encode("newPsw1"))
                .name("Anne")
                .surname("Doe")
                .roles(List.of(roleUser))
                .build())
        ));
        }
    }
    private void initializeBooks() {
        if (bookRepository.count() == 0) {
            bookRepository.saveAll(List.of(
                    Book.builder()
                            .author("Harper Lee")
                            .title("To Kill a Mockingbird")
                            .publicationYear(LocalDate.of(1960,07,11))
                            .description("The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it.")
                            .award("Award234")
                            .ISBN("9780446310789")
                            .quantity(20)
                            .available(true)
                        .build()
            ));
        }
    }
    private void initializeCartWithItems() {
        if (cartRepository.count() == 0 && cartItemRepository.count() == 0) {
            User user = userRepository.findById(2)
                                      .orElseThrow(() -> new UserException(
                                                         new ErrorResponse(
                                                                ErrorCode.EUN,
                                                                 "User not found")));
            Book book = bookRepository.findById(1)
                                      .orElseThrow(() -> new BookException(
                                                         new ErrorResponse(
                                                                 ErrorCode.BNF,
                                                                 "Book not found")));

            Cart cart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .createdDate(LocalDate.now())
                    .status(CartStatus.ACTIVE)
                    .build();
            cartRepository.save(cart);

            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(3)
                    .build();
            cartItemRepository.save(cartItem);

            cart.getItems().add(cartItem);
            cartRepository.save(cart);
        }
    }


    private void initializeLoansWithLoanDetails() {
        if (loanRepository.count() == 0) {

           Loan loan = Loan.builder()
                         .user(userRepository.findById(2)
                                              .orElseThrow(() -> new UserException(
                                                                 new ErrorResponse(
                                                                         ErrorCode.EUN,
                                                                         "User not found"))))
                         .loanDate(LocalDate.now())
                         .dueDate(LocalDate.now().plusDays(30))
                         .returnDate(null)
                         .loanDetails(new HashSet<>())
                         .status(LoanStatus.ACTIVE)
                         .build();
           loanRepository.save(loan);

           LoanDetail loanDetail = LoanDetail.builder()
                                              .book(bookRepository.findById(1)
                                                       .orElseThrow(() -> new BookException(
                                                               new ErrorResponse(
                                                                       ErrorCode.BNF,
                                                                       "Book not found"))))
                                              .loan(loan)
                                              .quantity(3)
                                              .build();
           loanDetailRepository.save(loanDetail);
           loan.getLoanDetails().add(loanDetail);
           loanRepository.save(loan);
        }
    }
}

