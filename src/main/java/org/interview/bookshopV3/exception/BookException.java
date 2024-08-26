package org.interview.bookshopV3.exception;

import lombok.Getter;

@Getter
public class BookException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public BookException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }
}