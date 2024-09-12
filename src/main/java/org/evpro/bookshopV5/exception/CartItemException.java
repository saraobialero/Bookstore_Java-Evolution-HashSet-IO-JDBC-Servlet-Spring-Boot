package org.evpro.bookshopV5.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.evpro.bookshopV5.data.response.ErrorResponse;

@AllArgsConstructor
@Setter
@Getter
public class CartItemException extends RuntimeException {
    private ErrorResponse response;

    @Override
    public String getMessage() {
        return response.getMessage();
    }
}
