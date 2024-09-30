package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.DTO.response.CartDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;

import java.util.List;


public interface CartFunctions {
    CartDTO getCartForUser(String email);
    CartDTO addItemToCart(String email, Integer bookId, int quantity);
    CartDTO removeItemFromCart(String email, Integer cartItemId);
    CartDTO updateCartItemQuantity(Integer cartItemId, int newQuantity);
    boolean clearCart(String email);
    LoanDTO moveCartToLoan(String email);

    //Admin functions
    List<CartDTO> getAllCarts();
    CartDTO getCartById(Integer cartId);
    boolean deleteCartById(Integer cartId);
    boolean deleteAllCarts();
}
