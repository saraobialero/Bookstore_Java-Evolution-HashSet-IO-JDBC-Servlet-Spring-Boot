package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.DTO.response.CartDTO;
import org.evpro.bookshopV5.model.DTO.response.LoanDTO;

import java.util.List;


public interface CartFunctions {
    CartDTO getCartForUser(Integer userId);
    CartDTO addItemToCart(Integer userId, Integer bookId, int quantity);
    CartDTO removeItemFromCart(Integer userId, Integer cartItemId);
    CartDTO updateCartItemQuantity(Integer cartItemId, int newQuantity);
    boolean clearCart(Integer userId);
    LoanDTO moveCartToLoan(Integer cartId);

    //Admin functions
    List<CartDTO> getAllCarts();
    CartDTO getCartById(Integer cartId);
    boolean deleteCartById(Integer cartId);
    boolean deleteAllCarts();
}
