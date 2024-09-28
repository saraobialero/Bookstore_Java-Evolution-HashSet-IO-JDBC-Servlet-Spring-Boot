package org.evpro.bookshopV5.utils;

import org.evpro.bookshopV5.model.*;
import org.evpro.bookshopV5.model.DTO.response.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DTOConverter {

    public static  <T, R, C extends Collection<R>> C convertCollection(Collection<T> source,
                                                                Function<T, R> converter,
                                                                Supplier<C> collectionFactory) {
        return source.stream()
                .map(converter)
                .collect(Collectors.toCollection(collectionFactory));
    }

    public static UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .roles(user.getRoles())
                .active(user.isActive())
                .build();
    }

    public static LoanDTO convertToLoanDTO(Loan loan) {
        return LoanDTO.builder()
                .id(loan.getId())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .loanDetails(convertCollection(loan.getLoanDetails(), DTOConverter::convertToLoanDetailsDTO, HashSet::new))
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .build();
    }

    public static LoanDetailsDTO convertToLoanDetailsDTO(LoanDetail loanDetail) {
        return LoanDetailsDTO.builder()
                .book(convertToBookDTO(loanDetail.getBook()))
                .id(loanDetail.getId())
                .quantity(loanDetail.getQuantity())
                .build();
    }

    public static CartDTO convertToCartDTO(Cart cart) {
        return CartDTO.builder()
                .id(cart.getId())
                .createdDate(cart.getCreatedDate())
                .items(convertCollection(cart.getItems(), DTOConverter::convertToCartItemDTO, ArrayList::new))
                .build();
    }

    public static BookDTO convertToBookDTO(Book book) {
        return BookDTO.builder()
                .author(book.getAuthor())
                .description(book.getDescription())
                .ISBN(book.getISBN())
                .title(book.getTitle())
                .genre(book.getGenre())
                .available(book.isAvailable())
                .award(book.getAward())
                .publicationYear(book.getPublicationYear())
                .quantity(book.getQuantity())
                .build();
    }

    public static CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .quantity(cartItem.getQuantity())
                .book(convertToBookDTO(cartItem.getBook()))
                .build();
    }
}
