package org.evpro.bookshopV5.service.functions;

import org.evpro.bookshopV5.model.Cart;

import java.math.BigDecimal;

public interface CartFunctions {
    Cart getCartForUser(Integer userId);
    void addItemToCart(Integer userId, Integer bookId, int quantity);
    void removeItemFromCart(Integer userId, Integer cartItemId);
    void updateCartItemQuantity(Integer cartItemId, int newQuantity);
    void clearCart(Integer userId);
    void moveCartToLoan(Integer cartId);
}
