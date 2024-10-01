package org.evpro.bookshopV5.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.DTO.request.AddItemToCartRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateItemToCartRequest;

import org.evpro.bookshopV5.model.DTO.response.*;


import org.evpro.bookshopV5.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/bookshop/v5/carts")
public class CartController {

    private final CartService cartService;

    @GetMapping("my-cart/details")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> getCartForUser(@AuthenticationPrincipal String userEmail) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.getCartForUser(userEmail)), HttpStatus.OK);
    }

    @PostMapping("/add-item")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> addItem(@AuthenticationPrincipal String userEmail,
                                                            @RequestBody @Valid AddItemToCartRequest addItemToCartRequest) {
        Integer idBook = addItemToCartRequest.getBookId();
        int quantity = addItemToCartRequest.getQuantity();
        return new ResponseEntity<>(new SuccessResponse<>(cartService.addItemToCart(userEmail, idBook, quantity)), HttpStatus.OK);
    }


    @DeleteMapping("/remove-item/{cartItemId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> removeCartItem(@AuthenticationPrincipal String userEmail,
                                                                   @PathVariable ("cartItemId") Integer cartItemId) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.removeItemFromCart(userEmail, cartItemId)), HttpStatus.OK);
    }

    //TODO: Add email in service/ fix(500)
    @PutMapping("/update/item-quantity")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> updateCartItemQuantity(@AuthenticationPrincipal String userEmail,
                                                                           @RequestBody @Valid UpdateItemToCartRequest updateItemToCartRequest) {
        Integer cartItemId = updateItemToCartRequest.getCartItemId();
        int newQuantity = updateItemToCartRequest.getNewQuantity();
        return new ResponseEntity<>(new SuccessResponse<>(cartService.updateCartItemQuantity(cartItemId, newQuantity)), HttpStatus.OK);
    }

    @PutMapping("/clear")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> clearCart(@AuthenticationPrincipal String userEmail) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.clearCart(userEmail)), HttpStatus.OK);
    }

    @PostMapping("/create-loan")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<LoanDTO>> moveCartToLoan(@AuthenticationPrincipal String userEmail) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.moveCartToLoan(userEmail)), HttpStatus.OK);
    }

    @GetMapping("/details/{cartId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> getCart(@PathVariable ("cartId") Integer cartId) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.getCartById(cartId)), HttpStatus.OK);
    }

    @GetMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<List<CartDTO>>> getAllCarts() {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.getAllCarts()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{cartId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deleteCart(@PathVariable ("cartId") Integer cartId) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.deleteCartById(cartId)), HttpStatus.OK);
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> deleteAllCarts() {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.deleteAllCarts()), HttpStatus.OK);
    }








}
