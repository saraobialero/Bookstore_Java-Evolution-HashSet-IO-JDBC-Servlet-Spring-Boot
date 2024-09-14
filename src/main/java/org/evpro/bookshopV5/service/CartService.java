package org.evpro.bookshopV5.service;

import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.Cart;
import org.evpro.bookshopV5.service.functions.CartFunctions;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService implements CartFunctions {

    @Override
    public Cart getCartForUser(Integer userId) {
        return null;
    }

    @Override
    public void addItemToCart(Integer userId, Integer bookId, int quantity) {

    }

    @Override
    public void removeItemFromCart(Integer userId, Integer cartItemId) {

    }

    @Override
    public void updateCartItemQuantity(Integer cartItemId, int newQuantity) {

    }

    @Override
    public void clearCart(Integer userId) {

    }

    @Override
    public void moveCartToLoan(Integer cartId) {

    }
}
