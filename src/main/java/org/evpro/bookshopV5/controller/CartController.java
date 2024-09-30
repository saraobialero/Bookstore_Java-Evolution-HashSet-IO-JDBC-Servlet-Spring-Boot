package org.evpro.bookshopV5.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.model.DTO.request.AddItemToCartRequest;
import org.evpro.bookshopV5.model.DTO.request.UpdateItemToCartRequest;

import org.evpro.bookshopV5.model.DTO.response.*;


import org.evpro.bookshopV5.service.CartService;
import org.evpro.bookshopV5.service.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/bookshop/v5/carts")
public class CartController {

    private final CartService cartService;

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> getCartForUser(@AuthenticationPrincipal String userEmail) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.getCartForUser(userEmail)), HttpStatus.OK);
    }



    @PostMapping("/{userId}/add")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> addItem(@PathVariable ("userId") Integer userId,
                                                            @RequestBody @Valid AddItemToCartRequest addItemToCartRequest) {
        Integer idBook = addItemToCartRequest.getBookId();
        int quantity = addItemToCartRequest.getQuantity();
        return new ResponseEntity<>(new SuccessResponse<>(cartService.addItemToCart(userId, idBook, quantity)), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/remove")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> removeCartItem(@PathVariable ("userId") Integer userId,
                                                                   @PathVariable ("cartItemId") Integer cartItemId) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.removeItemFromCart(userId, cartItemId)), HttpStatus.OK);
    }

    @PutMapping("/{userId}/update")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<CartDTO>> updateCartItemQuantity(@PathVariable ("userId") Integer userId,
                                                                           @RequestBody @Valid UpdateItemToCartRequest updateItemToCartRequest) {
        Integer cartItemId = updateItemToCartRequest.getCartItemId();
        int newQuantity = updateItemToCartRequest.getNewQuantity();
        return new ResponseEntity<>(new SuccessResponse<>(cartService.updateCartItemQuantity(cartItemId, newQuantity)), HttpStatus.OK);
    }

    @PutMapping("/{userId}/clear")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<Boolean>> clearCart(@PathVariable ("userId") Integer userId ) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.clearCart(userId)), HttpStatus.OK);
    }

    @PostMapping("/{userId}/create-loan")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<SuccessResponse<LoanDTO>> moveCartToLoan(@PathVariable ("userId") Integer userId ) {
        return new ResponseEntity<>(new SuccessResponse<>(cartService.moveCartToLoan(userId)), HttpStatus.OK);
    }




}
